package org.valross.constantine;


import org.jetbrains.annotations.Contract;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.util.Optional;

/**
 * A constantive type is a type which has the potential to be represented (accurately) as a constant.
 * This might mean that a type is mutable (e.g. array list) but can be converted to an immutable form (record).
 * All constant types are constantive types by definition, since they can be represented as a constant (themselves).
 */
public interface Constantive extends Constable {

    @Contract(pure = true)
    Constant constant();

    default @Override
    Optional<? extends ConstantDesc> describeConstable() {
        final Constant constant = this.constant();
        if (constant == null) return Optional.empty();
        return constant.describeConstable();
    }

}
