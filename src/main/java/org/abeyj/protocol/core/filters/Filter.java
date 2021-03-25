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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.Response;
import org.abeyj.protocol.core.Response.Error;
import org.abeyj.protocol.core.RpcErrors;
import org.abeyj.protocol.core.methods.response.AbeyFilter;
import org.abeyj.protocol.core.methods.response.AbeyLog;
import org.abeyj.protocol.core.methods.response.AbeyUninstallFilter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/** Class for creating managed filter requests with callbacks. */
public abstract class Filter<T> {

    private static final Logger log = LoggerFactory.getLogger(Filter.class);

    protected final Abeyj abeyj;
    protected Callback<T> callback;

    private volatile BigInteger filterId;

    protected ScheduledFuture<?> schedule;

    private ScheduledExecutorService scheduledExecutorService;

    private long blockTime;

    public Filter(Abeyj abeyj, Callback<T> callback) {
        this.abeyj = abeyj;
        this.callback = callback;
    }

    public void run(ScheduledExecutorService scheduledExecutorService, long blockTime) {
        try {
            AbeyFilter abeyFilter = sendRequest();
            if (abeyFilter.hasError()) {
                throwException(abeyFilter.getError());
            }

            filterId = abeyFilter.getFilterId();
            this.scheduledExecutorService = scheduledExecutorService;
            this.blockTime = blockTime;
            // this runs in the caller thread as if any exceptions are encountered, we shouldn't
            // proceed with creating the scheduled task below
            getInitialFilterLogs();

            /*
            We want the filter to be resilient against client issues. On numerous occasions
            users have reported socket timeout exceptions when connected over HTTP to Geth and
            Parity clients. For examples, refer to
            https://github.com/abeyj/abeyj/issues/144 and
            https://github.com/ethereum/go-ethereum/issues/15243.

            Hence we consume errors and log them as errors, allowing our polling for changes to
            resume. The downside of this approach is that users will not be notified of
            downstream connection issues. But given the intermittent nature of the connection
            issues, this seems like a reasonable compromise.

            The alternative approach would be to have another thread that blocks waiting on
            schedule.get(), catching any Exceptions thrown, and passing them back up to the
            caller. However, the user would then be required to recreate subscriptions manually
            which isn't ideal given the aforementioned issues.
            */
            schedule =
                    scheduledExecutorService.scheduleAtFixedRate(
                            () -> {
                                try {
                                    this.pollFilter(abeyFilter);
                                } catch (Throwable e) {
                                    // All exceptions must be caught, otherwise our job terminates
                                    // without
                                    // any notification
                                    log.error("Error sending request", e);
                                }
                            },
                            0,
                            blockTime,
                            TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            throwException(e);
        }
    }

    private void getInitialFilterLogs() {
        try {
            Optional<Request<?, AbeyLog>> maybeRequest = this.getFilterLogs(this.filterId);
            AbeyLog abeyLog = null;
            if (maybeRequest.isPresent()) {
                abeyLog = maybeRequest.get().send();
            } else {
                abeyLog = new AbeyLog();
                abeyLog.setResult(Collections.emptyList());
            }

            process(abeyLog.getLogs());

        } catch (IOException e) {
            throwException(e);
        }
    }

    private void pollFilter(AbeyFilter abeyFilter) {
        AbeyLog abeyLog = null;
        try {
            abeyLog = abeyj.abeyGetFilterChanges(filterId).send();
        } catch (IOException e) {
            throwException(e);
        }
        if (abeyLog.hasError()) {
            Error error = abeyLog.getError();
            switch (error.getCode()) {
                case RpcErrors.FILTER_NOT_FOUND:
                    reinstallFilter();
                    break;
                default:
                    throwException(error);
                    break;
            }
        } else {
            process(abeyLog.getLogs());
        }
    }

    protected abstract AbeyFilter sendRequest() throws IOException;

    protected abstract void process(List<AbeyLog.LogResult> logResults);

    private void reinstallFilter() {
        log.warn("The filter has not been found. Filter id: " + filterId);
        schedule.cancel(true);
        this.run(scheduledExecutorService, blockTime);
    }

    public void cancel() {
        schedule.cancel(false);

        try {
            AbeyUninstallFilter abeyUninstallFilter = uninstallFilter(filterId);
            if (abeyUninstallFilter.hasError()) {
                throwException(abeyUninstallFilter.getError());
            }

            if (!abeyUninstallFilter.isUninstalled()) {
                throw new FilterException("Filter with id '" + filterId + "' failed to uninstall");
            }
        } catch (IOException e) {
            throwException(e);
        }
    }

    protected AbeyUninstallFilter uninstallFilter(BigInteger filterId) throws IOException {
        return abeyj.abeyUninstallFilter(filterId).send();
    }

    /**
     * Retrieves historic filters for the filter with the given id. Getting historic logs is not
     * supported by all filters. If not the method should return an empty EthLog object
     *
     * @param filterId Id of the filter for which the historic log should be retrieved
     * @return Historic logs, or an empty optional if the filter cannot retrieve historic logs
     */
    protected abstract Optional<Request<?, AbeyLog>> getFilterLogs(BigInteger filterId);

    void throwException(Response.Error error) {
        throw new FilterException(
                "Invalid request: " + (error == null ? "Unknown Error" : error.getMessage()));
    }

    void throwException(Throwable cause) {
        throw new FilterException("Error sending request", cause);
    }
}
