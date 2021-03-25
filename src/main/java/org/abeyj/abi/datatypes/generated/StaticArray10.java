package org.abeyj.abi.datatypes.generated;

import org.abeyj.abi.datatypes.StaticArray;
import org.abeyj.abi.datatypes.Type;

import java.util.List;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.abeyj.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/abeyj/abeyj/tree/master/codegen">codegen module</a> to update.
 */
public class StaticArray10<T extends Type> extends StaticArray<T> {
    @Deprecated
    public StaticArray10(List<T> values) {
        super(10, values);
    }

    @Deprecated
    @SafeVarargs
    public StaticArray10(T... values) {
        super(10, values);
    }

    public StaticArray10(Class<T> type, List<T> values) {
        super(type, 10, values);
    }

    @SafeVarargs
    public StaticArray10(Class<T> type, T... values) {
        super(type, 10, values);
    }
}
