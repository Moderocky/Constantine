package org.valross.constantine;

import java.lang.constant.Constable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.RecordComponent;

public interface RecordConstant extends Constantive, Constant {

    @Override
    default boolean validate() {
        return this.getClass().isRecord() && Constant.super.validate();
    }

    default @Override
    Constable[] serial() throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Class<?> us = this.getClass();
        final RecordComponent[] components = us.getRecordComponents();
        final Constable[] arguments = new Constable[components.length];
        for (int i = 0; i < components.length; i++) {
            final MethodHandle handle = lookup.unreflect(components[i].getAccessor());
            arguments[i] = (Constable) handle.invokeWithArguments(this);
        }
        return arguments;
    }

    default @Override
    Class<?>[] canonicalParameters() {
        final Class<?> us = this.getClass();
        final RecordComponent[] components = us.getRecordComponents();
        final Class<?>[] parameters = new Class[components.length];
        for (int i = 0; i < components.length; i++) parameters[i] = components[i].getType();
        return parameters;
    }

    @Override
    default RecordConstant constant() {
        return this;
    }

}
