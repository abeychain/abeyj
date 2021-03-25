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
package org.abeyj.protocol.core.filters;

import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.methods.response.AbeyFilter;
import org.abeyj.protocol.core.methods.response.AbeyLog;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/** Handler for working with transaction filter requests. */
public class PendingTransactionFilter extends Filter<String> {

    public PendingTransactionFilter(Abeyj abeyj, Callback<String> callback) {
        super(abeyj, callback);
    }

    @Override
    protected AbeyFilter sendRequest() throws IOException {
        return abeyj.abeyNewPendingTransactionFilter().send();
    }

    @Override
    protected void process(List<AbeyLog.LogResult> logResults) {
        for (AbeyLog.LogResult logResult : logResults) {
            if (logResult instanceof AbeyLog.Hash) {
                String transactionHash = ((AbeyLog.Hash) logResult).get();
                callback.onEvent(transactionHash);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + ", required Hash");
            }
        }
    }

    /**
     * Since the pending transaction filter does not support historic filters, the filterId is
     * ignored and an empty optional is returned
     *
     * @param filterId Id of the filter for which the historic log should be retrieved
     * @return Optional.empty()
     */
    @Override
    protected Optional<Request<?, AbeyLog>> getFilterLogs(BigInteger filterId) {
        return Optional.empty();
    }
}
