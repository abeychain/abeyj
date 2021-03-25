package org.abeyj.response.snail;

import org.abeyj.protocol.core.Response;
import org.abeyj.utils.Numeric;

import java.math.BigInteger;

/**
 * eth_True_blockNumber.
 */
public class AbeySnailBlockNumber extends Response<String> {
    public BigInteger getAbeyBlockNumber() {
        return Numeric.decodeQuantity(getResult());
    }
}
