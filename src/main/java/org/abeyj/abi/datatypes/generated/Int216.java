package org.abeyj.abi.datatypes.generated;

import org.abeyj.abi.datatypes.Int;

import java.math.BigInteger;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.abeyj.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/abeyj/abeyj/tree/master/codegen">codegen module</a> to update.
 */
public class Int216 extends Int {
    public static final Int216 DEFAULT = new Int216(BigInteger.ZERO);

    public Int216(BigInteger value) {
        super(216, value);
    }

    public Int216(long value) {
        this(BigInteger.valueOf(value));
    }
}
