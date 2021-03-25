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
import org.abeyj.protocol.core.methods.response.AbeyLog.LogResult;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Handler hashes for working with block filter requests */
public class BlocksFilter extends Filter<List<String>> {

    public BlocksFilter(Abeyj abeyj, Callback<List<String>> callback) {
        super(abeyj, callback);
    }

    @Override
    protected AbeyFilter sendRequest() throws IOException {
        return abeyj.abeyNewBlockFilter().send();
    }

    @Override
    protected void process(List<LogResult> logResults) {
        List<String> blockHashes = new ArrayList<>(logResults.size());

        for (AbeyLog.LogResult logResult : logResults) {
            if (!(logResult instanceof AbeyLog.Hash)) {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + ", required Hash");
            }

            blockHashes.add(((AbeyLog.Hash) logResult).get());
        }

        callback.onEvent(blockHashes);
    }

    /**
     * Since the block filter does not support historic filters, the filterId is ignored and an
     * empty optional is returned.
     *
     * @param filterId Id of the filter for which the historic log should be retrieved
     * @return Optional.empty()
     */
    @Override
    protected Optional<Request<?, AbeyLog>> getFilterLogs(BigInteger filterId) {
        return Optional.empty();
    }
}
