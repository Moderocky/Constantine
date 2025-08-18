package org.valross.constantine;

import org.junit.Test;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

public class ConstantTest {

    @Test
    public void isConstant() {
        assert Constant.isConstant(Day.class);
        assert Constant.isConstant(String.class);
        assert Constant.isConstant(byte.class);
        assert !Constant.isConstant(Object.class);
        assert !Constant.isConstant(ConstantTest.class);
    }

    @Test
    public void describe() {
        assert !Constant.describe(Day.class).isPrimitive();
    }

    @Test
    public void bootstrap() throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Constant constant = new Day("Tuesday");
        final Optional<? extends ConstantDesc> optional = constant.describeConstable();
        assert optional.isPresent();
        final ConstantDesc desc = optional.get();
        final Object remade = desc.resolveConstantDesc(lookup);
        assert remade != null;
        assert remade instanceof Constant;
        assert remade instanceof Day day && day.equals(constant);
    }

    @Test
    public void bootstrapArray() throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Constant constant = new Array(new Day("Tuesday"), new Day("Wednesday"));
        final Optional<? extends ConstantDesc> optional = constant.describeConstable();
        assert optional.isPresent();
        final ConstantDesc desc = optional.get();
        final Object remade = desc.resolveConstantDesc(lookup);
        assert remade != null;
        assert remade instanceof Constant;
        assert remade instanceof Array array && array.equals(constant);
    }

    @Test
    public void serial() throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Constant constant = new Weekday("Tuesday");
        final Optional<? extends ConstantDesc> optional = constant.describeConstable();
        assert optional.isPresent();
        final ConstantDesc desc = optional.get();
        final Object remade = desc.resolveConstantDesc(lookup);
        assert remade != null;
        assert remade instanceof Constant;
        assert remade instanceof Weekday day && day.equals(constant);
    }

    @Test
    public void canonicalParameters() {
        final Constant constant = new Day("Tuesday");
        final Class<?>[] parameters = constant.canonicalParameters();
        assert parameters.length == 1;
        assert parameters[0] == String.class;
    }

    @Test
    public void describeConstable() {
        final Constant constant = new Day("Tuesday");
        final Optional<? extends ConstantDesc> optional = constant.describeConstable();
        assert optional.isPresent();
        final ConstantDesc desc = optional.get();
        assert desc instanceof DynamicConstantDesc<?> dynamic && dynamic.constantType()
            .descriptorString()
            .equals(Day.class.descriptorString());
        assert new Blob("hello", 5).describeConstable().isPresent();
    }

    @Test
    public void constant() throws Throwable {
        final Constant constant = Constant.fromConstable("Hello there");
        final Optional<? extends ConstantDesc> optional = constant.describeConstable();
        assert optional.isPresent();
        final ConstantDesc desc = optional.get();
        final Object remade = desc.resolveConstantDesc(MethodHandles.lookup());
        assert remade != null;
        assert remade instanceof Constant c && c.equals(constant);
    }

    @Test
    public void types() throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Constant constant = new All("A", 1, true, 'c', 4.0F, 5.0, 6L, (short) 7, (byte) 8);
        final Optional<? extends ConstantDesc> optional = constant.describeConstable();
        assert optional.isPresent();
        final ConstantDesc desc = optional.get();
        final Object remade = desc.resolveConstantDesc(lookup);
        assert remade != null;
        assert remade instanceof Constant;
        assert remade instanceof All all && all.equals(constant);
    }

    @Test
    public void arrays() throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Constant constant = new Arrays(new String[] {"a", "b"}, true);
        final Optional<? extends ConstantDesc> optional = constant.describeConstable();
        assert optional.isPresent();
        final ConstantDesc desc = optional.get();
        final Object remade = desc.resolveConstantDesc(lookup);
        assert remade != null;
        assert remade instanceof Constant;
        assert remade instanceof Arrays arrays && arrays.equals(constant);
    }

    record Day(String name) implements Constant {

        @Override
        public Constable[] serial() {
            return new Constable[] {name};
        }

        @Override
        public Class<?>[] canonicalParameters() {
            return new Class[] {String.class};
        }

    }

    record Weekday(String name) implements RecordConstant {

        @Override
        public Constable[] serial() {
            return new Constable[] {name};
        }

        @Override
        public Class<?>[] canonicalParameters() {
            return new Class[] {String.class};
        }

    }

    public record Blob(String name, int number) implements RecordConstant {

    }

    public record All(String a, int b, boolean c, char d, float e, double f, long g, short h,
                      byte i) implements RecordConstant {

    }

    public record Arrays(String[] strings, boolean b) implements RecordConstant {

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Arrays arrays)) return false;
            return b == arrays.b && Objects.deepEquals(strings, arrays.strings);
        }

        @Override
        public int hashCode() {
            return Objects.hash(java.util.Arrays.hashCode(strings), b);
        }

    }

}