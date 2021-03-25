package org.abeyj.protocol.core.filters;

import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.methods.response.AbeyFilter;
import org.abeyj.protocol.core.methods.response.AbeyLog;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/** Handler for working with block filter requests. */
public class BlockFilter extends Filter<String> {

    public BlockFilter(Abeyj abeyj, Callback<String> callback) {
        super(abeyj, callback);
    }

    @Override
    protected AbeyFilter sendRequest() throws IOException {
        return abeyj.abeyNewBlockFilter().send();
    }

    @Override
    protected void process(List<AbeyLog.LogResult> logResults) {
        for (AbeyLog.LogResult logResult : logResults) {
            if (logResult instanceof AbeyLog.Hash) {
                String blockHash = ((AbeyLog.Hash) logResult).get();
                callback.onEvent(blockHash);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + ", required Hash");
            }
        }
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
