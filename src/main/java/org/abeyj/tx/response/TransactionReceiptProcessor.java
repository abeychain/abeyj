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
package org.abeyj.tx.response;

import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.methods.response.AbeyGetTransactionReceipt;
import org.abeyj.protocol.core.methods.response.TransactionReceipt;
import org.abeyj.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.util.Optional;

/** Abstraction for managing how we wait for transaction receipts to be generated on the network. */
public abstract class TransactionReceiptProcessor {

    private final Abeyj abeyj;

    public TransactionReceiptProcessor(Abeyj abeyj) {
        this.abeyj = abeyj;
    }

    public abstract TransactionReceipt waitForTransactionReceipt(String transactionHash)
            throws IOException, TransactionException;

    Optional<? extends TransactionReceipt> sendTransactionReceiptRequest(String transactionHash)
            throws IOException, TransactionException {
        AbeyGetTransactionReceipt transactionReceipt =
                abeyj.abeyGetTransactionReceipt(transactionHash).send();
        if (transactionReceipt.hasError()) {
            throw new TransactionException(
                    "Error processing request: " + transactionReceipt.getError().getMessage());
        }

        return transactionReceipt.getTransactionReceipt();
    }
}
