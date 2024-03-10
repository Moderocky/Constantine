package org.valross.constantine;

public abstract class ConstantError extends Error {

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

}

class ConstantConstructionError extends ConstantError {

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
    }

}

class ConstantDeconstructionError extends ConstantError {

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
    }

}

