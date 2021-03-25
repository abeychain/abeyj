package org.abeyj.abi.datatypes.generated;

import org.abeyj.abi.datatypes.Uint;

import java.math.BigInteger;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.abeyj.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/abeyj/abeyj/tree/master/codegen">codegen module</a> to update.
 */
public class Uint144 extends Uint {
    public static final Uint144 DEFAULT = new Uint144(BigInteger.ZERO);

    public Uint144(BigInteger value) {
        super(144, value);
    }

    public Uint144(long value) {
        this(BigInteger.valueOf(value));
    }
}
