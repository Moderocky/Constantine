package org.valross.constantine;

import org.jetbrains.annotations.Contract;

import java.lang.constant.ConstantDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;

/**
 * A canonical constant is one that has a universal set of unique, interned references.
 * I.e. like {@link String}s, there can be a unique object for each value.
 * Ideally, such constants will also have some factory method for producing them.
 * <br/>
 * <br/>
 * A canonical constant class <b>MUST</b> declare a static factory method: i.e. a method for obtaining an instance
 * (ideally the interned instance) using its constituent parts.
 *
 * @param <Type> The extending class (self-referential)
 * @see #factoryMethodName()
 * @see #intern()
 */
public interface Canonical<Type extends Constant & Canonical<Type>> extends Constant {

    private static boolean hasCanonicalFactory(Class<?> type, String name, Class<?>... parameters) {
        try {
            MethodHandles.lookup().findStatic(type, name, MethodType.methodType(type, parameters));
            return true;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            return false;
        }
    }

    /**
     * Returns the interned canonical representation of this object.
     * <br/>
     * <br/>
     * An interned canonical representation is a constant instance representing a value
     * such that it is the only interned canonical instance representing that value.
     * Interning different instances representing the same value must return the same interned instance.
     * Interned instances may be identical to non-interned instances for the same value.
     * <p>
     * For any {@code this instanceof Canonical && that instanceof Canonical && this.equals(that)}
     * it must be true that:
     * <ol>
     *     <li>{@code this.equals(this.intern())}</li>
     *     <li>{@code this.equals(that.intern())}</li>
     *     <li>{@code this.intern() == this.intern()}</li>
     *     <li>{@code this.intern() == that.intern()}</li>
     * </ol>
     * <p>
     * An object may be its interned reference: {@code this.intern() == this.intern().intern()}
     *
     * @return an object that has the same contents as this, but is
     * guaranteed to be in the universal canonical set
     * @see String#intern()
     */
    Type intern();

    @Contract(pure = true)
    default boolean validate() {
        return Constant.isConstant(this.getClass()) &&
            hasCanonicalFactory(this.getClass(), this.factoryMethodName(), this.canonicalParameters());
    }

    @Override
    default Optional<? extends ConstantDesc> describeConstable() {
        assert this.validate(); // test only, make sure this is actually what it pretends to be
        final ConstantDesc[] arguments = Utilities.getArguments(this);
        return Optional.of(DynamicConstantDesc.ofNamed(BOOTSTRAP_CANON, this.factoryMethodName(),
            Constant.describe(this.getClass()),
            arguments));
    }

    /**
     * The name of the <b>PUBLIC, STATIC</b> method in the declaring class which produces an instance of the object.
     * This is typically {@code public static T valueOf(...)}.
     * <p>
     * A factory method <i>can</i> simply return a new instance, but it is advised to return
     * the interned canonical reference where possible.
     *
     * @return The name of the factory method
     * @see Boolean#valueOf(boolean)
     * @see Integer#valueOf(int)
     * @see String#valueOf(char[])
     */
    default String factoryMethodName() {
        return "valueOf";
    }

}
