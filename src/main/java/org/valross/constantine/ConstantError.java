package org.valross.constantine;

import java.lang.constant.Constable;

/// An error related to the validation,
/// disassembly, or re-creation of a constant value.
public abstract class ConstantError extends Error {

    protected Class<? extends Constable> cause;

    public ConstantError(Class<? extends Constable> cause) {
        this();
        this.cause = cause;
    }

    public ConstantError() {
        super();
    }

    public ConstantError(String message) {
        super(message);
    }

    public ConstantError(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstantError(Throwable cause) {
        super(cause);
    }

    public Class<? extends Constable> getCauser() {
        return cause;
    }

}

class ConstantConstructionError extends ConstantError {

    public ConstantConstructionError(Class<? extends Constable> cause) {
        super(cause);
    }

    public ConstantConstructionError() {
        super();
    }

    public ConstantConstructionError(String message) {
        super(message);
    }

    public ConstantConstructionError(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstantConstructionError(Throwable cause) {
        super(cause);
        if (cause instanceof ConstantError error)
            this.cause = error.cause;
    }

}

class ConstantDeconstructionError extends ConstantError {

    Class<?> target;

    public ConstantDeconstructionError(Class<? extends Constable> cause, Class<?> target) {
        super(cause);
        this.target = target;
    }

    public ConstantDeconstructionError() {
        super();
    }

    public ConstantDeconstructionError(String message) {
        super(message);
    }

    public ConstantDeconstructionError(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstantDeconstructionError(Throwable cause) {
        super(cause);
        if (cause instanceof ConstantDeconstructionError error)
            this.target = error.target;
    }

    public Class<?> getTarget() {
        return target;
    }

}

