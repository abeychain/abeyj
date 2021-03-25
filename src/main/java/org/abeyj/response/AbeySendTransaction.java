package org.abeyj.response;

import org.abeyj.protocol.core.Response;

/**
 * eth_sendTransaction.
 */
public class AbeySendTransaction extends Response<String> {
    public String getAbeyTransactionHash() {
        return getResult();
    }
}
