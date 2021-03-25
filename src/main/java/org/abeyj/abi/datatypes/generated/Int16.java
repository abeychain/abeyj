package org.abeyj.abi.datatypes.generated;

import org.abeyj.abi.datatypes.Int;

import java.math.BigInteger;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.abeyj.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/abeyj/abeyj/tree/master/codegen">codegen module</a> to update.
 */
public class Int16 extends Int {
    public static final Int16 DEFAULT = new Int16(BigInteger.ZERO);

    public Int16(BigInteger value) {
        super(16, value);
    }

    public Int16(long value) {
        this(BigInteger.valueOf(value));
    }
}
