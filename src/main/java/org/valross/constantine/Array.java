package org.valross.constantine;

import org.jetbrains.annotations.NotNull;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DynamicConstantDesc;
import java.util.*;

import static java.lang.constant.ConstantDescs.DEFAULT_NAME;

/**
 * A constant (frozen) array of effective constants.
 */
public record Array(Constable... serial) implements RecordConstant, Constant, Collection<Constable>, Cloneable {

    public Array(Constable... serial) {
        this.serial = Arrays.copyOf(serial, serial.length);
    }

    public Array(Collection<? extends Constable> values) {
        this(values.toArray(new Constable[0]));
    }

    @Override
    public Constable[] serial() {
        final Constable[] copy = new Constable[serial.length];
        System.arraycopy(serial, 0, copy, 0, serial.length);
        return copy;
    }

    @Override
    public Class<?>[] canonicalParameters() {
        return new Class[] {Constable[].class};
    }

    @Override
    public Array constant() {
        return this;
    }

    @Override
    public Optional<? extends ConstantDesc> describeConstable() {
        final Constable[] constables = serial;
        final ConstantDesc[] arguments = new ConstantDesc[constables.length];
        for (int i = 0; i < arguments.length; i++) {
            final Constable constable = constables[i];
            if (constable instanceof ConstantDesc self) arguments[i] = self;
            else arguments[i] = constables[i].describeConstable().orElse(null);
        }
        return Optional.of(DynamicConstantDesc.ofNamed(BOOTSTRAP_ARRAY, DEFAULT_NAME, ARRAY_DESC, arguments));
    }

    @Override
    public int size() {
        return serial.length;
    }

    @Override
    public boolean isEmpty() {
        return serial.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        return Set.of(serial).contains(o);
    }

    @Override
    public @NotNull Iterator<Constable> iterator() {
        return Arrays.stream(this.serial()).iterator();
    }

    @Override
    public Constable @NotNull [] toArray() {
        return this.serial();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Type> Type @NotNull [] toArray(Type... array) {
        if (array.length < serial.length) return this.toArray((Class<Type>) array.getClass().componentType());
        System.arraycopy(serial, 0, array, 0, serial.length);
        return array;
    }

    @Override
    public boolean add(Constable constable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return Set.of(serial).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Constable> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Array clone() {
        return new Array(serial);
    }

    @SuppressWarnings("unchecked")
    public <Type> Type[] toArray(Class<Type> type) {
        final Type[] array = (Type[]) java.lang.reflect.Array.newInstance(type, serial.length);
        System.arraycopy(serial, 0, array, 0, serial.length);
        return array;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Array that = (Array) o;
        return Arrays.equals(serial, that.serial);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(serial);
    }

}
