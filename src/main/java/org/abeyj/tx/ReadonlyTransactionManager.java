/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.abeyj.tx;

import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.DefaultBlockParameter;
import org.abeyj.protocol.core.methods.request.Transaction;
import org.abeyj.protocol.core.methods.response.AbeyCall;
import org.abeyj.protocol.core.methods.response.AbeyGetCode;
import org.abeyj.protocol.core.methods.response.AbeySendTransaction;

import java.io.IOException;
import java.math.BigInteger;

/** Transaction manager implementation for read-only operations on smart contracts. */
public class ReadonlyTransactionManager extends TransactionManager {
    private final Abeyj abeyj;
    private String fromAddress;

    public ReadonlyTransactionManager(Abeyj abeyj, String fromAddress) {
        super(abeyj, fromAddress);
        this.abeyj = abeyj;
        this.fromAddress = fromAddress;
    }

    @Override
    public AbeySendTransaction sendTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            boolean constructor)
            throws IOException {
        throw new UnsupportedOperationException(
                "Only read operations are supported by this transaction manager");
    }

    @Override
    public AbeySendTransaction sendTransactionEIP1559(
            BigInteger gasPremium,
            BigInteger feeCap,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            boolean constructor)
            throws IOException {
        throw new UnsupportedOperationException(
                "Only read operations are supported by this transaction manager");
    }

    @Override
    public String sendCall(String to, String data, DefaultBlockParameter defaultBlockParameter)
            throws IOException {
        AbeyCall abeyCall =
                abeyj.abeyCall(
                                Transaction.createEthCallTransaction(fromAddress, to, data),
                                defaultBlockParameter)
                        .send();

        assertCallNotReverted(abeyCall);
        return abeyCall.getValue();
    }

    @Override
    public AbeyGetCode getCode(
            final String contractAddress, final DefaultBlockParameter defaultBlockParameter)
            throws IOException {
        return abeyj.abeyGetCode(contractAddress, defaultBlockParameter).send();
    }
}
