package org.tlauncher.injection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class InjectionHelper {
   private ClassNode classNode;
   private ClassWriter classWriter;
   private Processor processor;

   public Processor init(byte[] bytes) {
      return this.init(bytes, 0);
   }

   public Processor init(byte[] bytes, int classReaderFlags) {
      this.classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(this.classNode, classReaderFlags);
      this.classWriter = new ClassWriter(0);
      return this.processor = new Processor();
   }

   public byte[] getClassBytes() {
      this.classNode.accept(this.classWriter);
      return this.classWriter.toByteArray();
   }

   public ClassNode getClassNode() {
      return this.classNode;
   }

   public class Processor {
      public MethodNodeContainer<Object> findMethod(String name, String desc) {
         for(MethodNode method : InjectionHelper.this.classNode.methods) {
            if (method.name.equals(name) && method.desc.equals(desc)) {
               return InjectionHelper.this.new MethodNodeContainer<Object>(method);
            }
         }

         return InjectionHelper.this.new MethodNodeContainer<Object>((MethodNode)null);
      }

      public MethodNodeContainer<Object> findMethod(String name, MethodDescFilter methodDescFilter) {
         for(MethodNode method : InjectionHelper.this.classNode.methods) {
            if (method.name.equals(name) && methodDescFilter.test(method.desc)) {
               return InjectionHelper.this.new MethodNodeContainer<Object>(method);
            }
         }

         return InjectionHelper.this.new MethodNodeContainer<Object>((MethodNode)null);
      }

      public List<MethodNodeContainer<Object>> findMethod(String name) {
         List<MethodNodeContainer<Object>> list = new ArrayList();

         for(MethodNode method : InjectionHelper.this.classNode.methods) {
            if (method.name.equals(name)) {
               list.add(InjectionHelper.this.new MethodNodeContainer(method));
               break;
            }
         }

         return list;
      }

      public List<FieldNodeContainer<Object>> findField(String name) {
         List<FieldNodeContainer<Object>> list = new ArrayList();

         for(FieldNode field : InjectionHelper.this.classNode.fields) {
            if (field.name.equals(name)) {
               list.add(InjectionHelper.this.new FieldNodeContainer(field));
               break;
            }
         }

         return list;
      }

      public FieldNode findField(String name, String desc) {
         for(FieldNode field : InjectionHelper.this.classNode.fields) {
            if (field.name.equals(name) && field.desc.equals(desc)) {
               return field;
            }
         }

         return null;
      }

      private boolean isPrimitiveType(String type) {
         String primitives = "JISBCFDZV";

         for(char primitive : primitives.toCharArray()) {
            if (type.charAt(0) == primitive) {
               return true;
            }
         }

         return false;
      }

      public byte[] getClassBytes() {
         InjectionHelper.this.classNode.accept(InjectionHelper.this.classWriter);
         return InjectionHelper.this.classWriter.toByteArray();
      }
   }

   public class FieldNodeContainer<V> extends Processor {
      private final FieldNode source;
      private V nullResult;
      private boolean chain;

      public FieldNodeContainer(FieldNode source) {
         this.chain = true;
         this.source = source;
      }

      public FieldNodeContainer(FieldNode methodNode, V nullResult) {
         this(methodNode);
         this.nullResult = nullResult;
      }

      public FieldNodeContainer<V> nonNull() {
         this.chain = this.source != null;
         return this;
      }

      public FieldNodeContainer<V> isNull() {
         this.chain = this.source == null;
         return this;
      }

      public <T> FieldNodeContainer<T> ifNullResult(T result) {
         return InjectionHelper.this.new FieldNodeContainer<T>(this.source, result);
      }

      public Processor thenAccept(Consumer<FieldNode> consumer) {
         if (this.chain) {
            consumer.accept(this.source);
         }

         return InjectionHelper.this.processor;
      }

      public <T> T thenApply(Function<FieldNode, T> function) {
         return (T)this.thenApply(function, this.nullResult == null ? null : this.nullResult);
      }

      public <T> T thenApply(Function<FieldNode, T> function, T ifNull) {
         return (T)(this.chain ? function.apply(this.source) : ifNull);
      }
   }

   public class MethodNodeContainer<V> extends Processor {
      private final MethodNodeContainer<V>.MethodNodeWrap source;
      private V nullResult;
      private boolean chain;

      public MethodNodeContainer(MethodNode source) {
         this.chain = true;
         this.source = new MethodNodeWrap(source);
      }

      public MethodNodeContainer(MethodNode methodNode, V nullResult) {
         this(methodNode);
         this.nullResult = nullResult;
      }

      public MethodNodeContainer<V> nonNull() {
         this.chain = this.source.getSource() != null;
         return this;
      }

      public MethodNodeContainer<V> isNull() {
         this.chain = this.source.getSource() == null;
         return this;
      }

      public <T> MethodNodeContainer<T> ifNullResult(T result) {
         return InjectionHelper.this.new MethodNodeContainer<T>(this.source.getSource(), result);
      }

      public Processor thenAccept(Consumer<MethodNodeContainer<V>.MethodNodeWrap> consumer) {
         if (this.chain) {
            consumer.accept(this.source);
         }

         return InjectionHelper.this.processor;
      }

      public <T> T thenApply(Function<MethodNodeContainer<V>.MethodNodeWrap, T> function) {
         return (T)this.thenApply(function, this.nullResult == null ? null : this.nullResult);
      }

      public <T> T thenApply(Function<MethodNodeContainer<V>.MethodNodeWrap, T> function, T ifNull) {
         return (T)(this.chain ? function.apply(this.source) : ifNull);
      }

      public class MethodNodeWrap extends Processor {
         private final MethodNode source;

         @Nullable
         public AbstractInsnNode findInstruction(Predicate<AbstractInsnNode> predicate) {
            if (this.source != null) {
               ListIterator<AbstractInsnNode> iterator = this.getInstructions().iterator();

               while(iterator.hasNext()) {
                  AbstractInsnNode next = (AbstractInsnNode)iterator.next();
                  if (predicate.test(next)) {
                     return next;
                  }
               }
            }

            return null;
         }

         public void addInstruction(InsnNode insnNode) {
            if (this.source != null) {
               this.source.instructions.add(insnNode);
            }

         }

         public Stream<AbstractInsnNode> findInstruction() {
            return this.source != null ? Arrays.stream(this.getInstructions().toArray()) : Stream.empty();
         }

         public InsnList getInstructions() {
            return this.source.instructions;
         }

         public MethodNodeWrap(MethodNode source) {
            this.source = source;
         }

         public MethodNode getSource() {
            return this.source;
         }
      }
   }

   public static class MethodModel {
      private final int accessLevel;
      private final String name;
      private final String returnType;
      private final List<String> params;
      private final InsnList instructions;

      MethodModel(int accessLevel, String name, String returnType, List<String> params, InsnList instructions) {
         this.accessLevel = accessLevel;
         this.name = name;
         this.returnType = returnType;
         this.params = params;
         this.instructions = instructions;
      }

      public static MethodModelBuilder builder() {
         return new MethodModelBuilder();
      }

      public static class MethodModelBuilder {
         private int accessLevel;
         private String name;
         private String returnType;
         private ArrayList<String> params;
         private InsnList instructions;

         MethodModelBuilder() {
         }

         public MethodModelBuilder accessLevel(int accessLevel) {
            this.accessLevel = accessLevel;
            return this;
         }

         public MethodModelBuilder name(String name) {
            this.name = name;
            return this;
         }

         public MethodModelBuilder returnType(String returnType) {
            this.returnType = returnType;
            return this;
         }

         public MethodModelBuilder param(String param) {
            if (this.params == null) {
               this.params = new ArrayList();
            }

            this.params.add(param);
            return this;
         }

         public MethodModelBuilder params(Collection<? extends String> params) {
            if (params == null) {
               throw new NullPointerException("params cannot be null");
            } else {
               if (this.params == null) {
                  this.params = new ArrayList();
               }

               this.params.addAll(params);
               return this;
            }
         }

         public MethodModelBuilder clearParams() {
            if (this.params != null) {
               this.params.clear();
            }

            return this;
         }

         public MethodModelBuilder instructions(InsnList instructions) {
            this.instructions = instructions;
            return this;
         }

         public MethodModel build() {
            List<String> params;
            switch (this.params == null ? 0 : this.params.size()) {
               case 0 -> params = Collections.emptyList();
               case 1 -> params = Collections.singletonList((String)this.params.get(0));
               default -> params = Collections.unmodifiableList(new ArrayList(this.params));
            }

            return new MethodModel(this.accessLevel, this.name, this.returnType, params, this.instructions);
         }

         public String toString() {
            return "InjectionHelper.MethodModel.MethodModelBuilder(accessLevel=" + this.accessLevel + ", name=" + this.name + ", returnType=" + this.returnType + ", params=" + this.params + ", instructions=" + this.instructions + ")";
         }
      }
   }
}
