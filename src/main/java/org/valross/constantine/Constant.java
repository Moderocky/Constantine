package org.valross.constantine;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.lang.constant.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import static java.lang.constant.ConstantDescs.DEFAULT_NAME;

/**
 * A CONSTANT is a VALUE class.
 * A type is CONSTANT iff:
 * for all fields in type (field is FINAL && type of field is CONSTANT)
 * && for all instances of type (instance can be expressed in a definite series of "constables")
 */
public interface Constant extends Constable, Constantive, Serializable, Cloneable {

    ClassDesc CONSTANT_DESC = describe(Constant.class);
    ClassDesc ARRAY_DESC = describe(Array.class);
    DirectMethodHandleDesc BOOTSTRAP_MAKE = ConstantDescs.ofConstantBootstrap(CONSTANT_DESC, "bootstrap",
                                                                              CONSTANT_DESC, describe(Object[].class));
    DirectMethodHandleDesc BOOTSTRAP_ARRAY = ConstantDescs.ofConstantBootstrap(CONSTANT_DESC, "bootstrapArray",
                                                                               ARRAY_DESC, describe(Object[].class));

    static boolean isConstant(Class<?> type) {
        if (isJavaConstant(type)) return true;
        if (!Constant.class.isAssignableFrom(type)) return false;
        for (Field field : type.getFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers)) continue;
            if (!Modifier.isFinal(modifiers)) return false;
            if (!isConstant(field.getType())) return false;
        }
        final Class<?> parent = type.getSuperclass();
        return parent == null || parent == Object.class || isConstant(parent);
    }

    private static boolean hasCanonicalConstructor(Class<?> type, Class<?>... parameters) {
        try {
            MethodHandles.lookup().findConstructor(type, MethodType.methodType(void.class, parameters));
            return true;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            return false;
        }
    }

    private static boolean isSuperConstant(Class<?> type) {
        return type.isPrimitive() || Constable.class.isAssignableFrom(type);
    }

    private static boolean isJavaConstant(Class<?> type) {
        return type.isPrimitive() || type.isEnum() || type == Record.class || (Constable.class.isAssignableFrom(type) && type.getPackageName()
                                                                                                                             .startsWith("java."));
    }

    static ClassDesc describe(Class<?> type) {
        return ClassDesc.ofDescriptor(type.descriptorString());
    }

    static Constant bootstrap(MethodHandles.Lookup lookup, String ignored, Class<?> type, Object... serial)
        throws Throwable {
        final MethodType signature = (MethodType) serial[0];
        final Object[] arguments = new Object[serial.length - 1];
        System.arraycopy(serial, 1, arguments, 0, serial.length - 1);
        final MethodHandle constructor = lookup.findConstructor(type, signature);
        return (Constant) constructor.invokeWithArguments(arguments);
    }

    static Array bootstrapArray(MethodHandles.Lookup lookup, String ignored, Class<?> type, Object... serial)
        throws Throwable {
        final MethodHandle constructor = lookup.findConstructor(type, MethodType.methodType(void.class,
                                                                                            Constable[].class));
        return (Array) constructor.invokeWithArguments(serial);
    }

    @Contract(pure = true)
    default boolean validate() {
        return isConstant(this.getClass()) && hasCanonicalConstructor(this.getClass(), this.canonicalParameters());
    }

    @Contract(pure = true)
    Constable[] serial() throws Throwable;

    @Contract(pure = true)
    Class<?>[] canonicalParameters();

    default @Override Optional<? extends ConstantDesc> describeConstable() {
        assert this.validate(); // test only, make sure this is actually what it pretends to be
        final Constable[] constables;
        try {
            constables = this.serial();
        } catch (Throwable e) {
            throw new ConstantDeconstructionError(e);
        }
        final ConstantDesc[] arguments = new ConstantDesc[constables.length + 1];
        for (int i = 0; i < constables.length; i++) {
            final Constable constable = constables[i];
            if (constable instanceof ConstantDesc self) arguments[i + 1] = self;
            else arguments[i + 1] = constable.describeConstable().orElse(null);
        }
        arguments[0] = MethodType.methodType(void.class, this.canonicalParameters()).describeConstable().orElse(null);
        return Optional.of(DynamicConstantDesc.ofNamed(BOOTSTRAP_MAKE, DEFAULT_NAME, describe(this.getClass()),
                                                       arguments));
    }

    @Contract(pure = true)
    default @Override Constant constant() {
        return this;
    }

    static Constant fromConstable(Constable constable) {
        record ConstantWrapper(Constable constable) implements Constant {

            @Override
            public Constable[] serial() {
                return new Constable[] {constable};
            }

            @Override
            public Class<?>[] canonicalParameters() {
                return new Class<?>[] {Constable.class};
            }

        }

        return new ConstantWrapper(constable);
    }

    /**
     * A constant interface for a unit type: a type that allows only one value and holds no information.
     * This would be a class with no possible constructor arguments and only constant fields.
     */
    interface UnitConstant extends Constant {

        @Override
        default Constable[] serial() {
            return new Constable[0];
        }

        @Override
        default Class<?>[] canonicalParameters() {
            return new Class[0];
        }

    }

}
