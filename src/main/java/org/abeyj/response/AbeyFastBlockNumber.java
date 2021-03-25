package org.abeyj.response;

import org.abeyj.protocol.core.Response;
import org.abeyj.utils.Numeric;

import java.math.BigInteger;

public class AbeyFastBlockNumber extends Response<String> {
    public BigInteger getBlockNumber() {
        return Numeric.decodeQuantity(getResult());
    }
}