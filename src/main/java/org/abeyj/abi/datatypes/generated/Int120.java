package org.abeyj.abi.datatypes.generated;

import org.abeyj.abi.datatypes.Int;

import java.math.BigInteger;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.abeyj.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/abeyj/abeyj/tree/master/codegen">codegen module</a> to update.
 */
public class Int120 extends Int {
    public static final Int120 DEFAULT = new Int120(BigInteger.ZERO);

    public Int120(BigInteger value) {
        super(120, value);
    }

    public Int120(long value) {
        this(BigInteger.valueOf(value));
    }
}
