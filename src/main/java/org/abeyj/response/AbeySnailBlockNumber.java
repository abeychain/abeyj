package org.abeyj.response;

import org.abeyj.protocol.core.Response;
import org.abeyj.utils.Numeric;

import java.math.BigInteger;

/**
 * eth_True_blockNumber.
 */
public class AbeySnailBlockNumber extends Response<String> {
    public BigInteger getSnailBlockNumber() {
        return Numeric.decodeQuantity(getResult());
    }
}
