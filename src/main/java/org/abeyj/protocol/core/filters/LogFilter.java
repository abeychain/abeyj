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
import org.abeyj.protocol.core.methods.response.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/** Log filter handler. */
public class LogFilter extends Filter<Log> {

    protected final org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter;

    public LogFilter(
            Abeyj abeyj,
            Callback<Log> callback,
            org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter) {
        super(abeyj, callback);
        this.abeyFilter = abeyFilter;
    }

    @Override
    protected AbeyFilter sendRequest() throws IOException {
        return abeyj.abeyNewFilter(abeyFilter).send();
    }

    @Override
    protected void process(List<AbeyLog.LogResult> logResults) {
        for (AbeyLog.LogResult logResult : logResults) {
            if (logResult instanceof AbeyLog.LogObject) {
                Log log = ((AbeyLog.LogObject) logResult).get();
                callback.onEvent(log);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + " required LogObject");
            }
        }
    }

    @Override
    protected Optional<Request<?, AbeyLog>> getFilterLogs(BigInteger filterId) {
        return Optional.of(abeyj.abeyGetFilterLogs(filterId));
    }
}
