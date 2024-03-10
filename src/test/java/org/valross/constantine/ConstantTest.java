package org.valross.constantine;

import org.junit.Test;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.invoke.MethodHandles;
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
    public void bootstrapArray() {
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
    }

    @Test
    public void describeConstable() {
    }

    @Test
    public void constant() {
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


}