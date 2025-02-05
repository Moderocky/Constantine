package org.valross.constantine;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.constant.ConstantDescs;
import java.lang.invoke.MethodType;

final class Utilities {

    static ConstantDesc[] getArguments(Constant constant) {
        final Constable[] constables;
        try {
            constables = constant.serial();
        } catch (Throwable e) {
            throw new ConstantDeconstructionError(e);
        }
        final ConstantDesc[] arguments = new ConstantDesc[constables.length + 1];
        for (int i = 0; i < constables.length; i++) {
            final Constable constable = constables[i];
            if (constable == null) arguments[i + 1] = ConstantDescs.NULL;
            else if (constable instanceof ConstantDesc self) arguments[i + 1] = self;
            else arguments[i + 1] = constable.describeConstable().orElse(null);
        }
        arguments[0] = MethodType.methodType(void.class, constant.canonicalParameters()).describeConstable().orElse(null);
        return arguments;
    }

}
