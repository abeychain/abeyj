package org.abeyj.abi.datatypes.generated;

import org.abeyj.abi.datatypes.Uint;

import java.math.BigInteger;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.abeyj.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/abeyj/abeyj/tree/master/codegen">codegen module</a> to update.
 */
public class Uint96 extends Uint {
    public static final Uint96 DEFAULT = new Uint96(BigInteger.ZERO);

    public Uint96(BigInteger value) {
        super(96, value);
    }

    public Uint96(long value) {
        this(BigInteger.valueOf(value));
    }
}
