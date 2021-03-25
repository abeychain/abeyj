package org.abeyj.abi.datatypes.generated;

import org.abeyj.abi.datatypes.Int;

import java.math.BigInteger;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.abeyj.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/abeyj/abeyj/tree/master/codegen">codegen module</a> to update.
 */
public class Int88 extends Int {
    public static final Int88 DEFAULT = new Int88(BigInteger.ZERO);

    public Int88(BigInteger value) {
        super(88, value);
    }

    public Int88(long value) {
        this(BigInteger.valueOf(value));
    }
}
