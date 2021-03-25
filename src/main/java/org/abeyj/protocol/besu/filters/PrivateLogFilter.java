/*
 * Copyright 2020 Web3 Labs Ltd.
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
package org.abeyj.protocol.besu.filters;

import org.abeyj.protocol.besu.Besu;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.filters.Callback;
import org.abeyj.protocol.core.filters.LogFilter;
import org.abeyj.protocol.core.methods.response.AbeyFilter;
import org.abeyj.protocol.core.methods.response.AbeyLog;
import org.abeyj.protocol.core.methods.response.AbeyUninstallFilter;
import org.abeyj.protocol.core.methods.response.Log;
import org.abeyj.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public class PrivateLogFilter extends LogFilter {

    private final String privacyGroupId;

    public PrivateLogFilter(
            Besu abeyj,
            Callback<Log> callback,
            String privacyGroupId,
            org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter) {
        super(abeyj, callback, abeyFilter);
        this.privacyGroupId = privacyGroupId;
    }

    @Override
    protected AbeyFilter sendRequest() throws IOException {
        return ((Besu) abeyj).privNewFilter(privacyGroupId, abeyFilter).send();
    }

    @Override
    protected AbeyUninstallFilter uninstallFilter(BigInteger filterId) throws IOException {
        return ((Besu) abeyj)
                .privUninstallFilter(privacyGroupId, Numeric.toHexStringWithPrefix(filterId))
                .send();
    }

    @Override
    protected Optional<Request<?, AbeyLog>> getFilterLogs(BigInteger filterId) {
        return Optional.of(
                ((Besu) abeyj)
                        .privGetFilterLogs(
                                privacyGroupId, Numeric.toHexStringWithPrefix(filterId)));
    }
}
