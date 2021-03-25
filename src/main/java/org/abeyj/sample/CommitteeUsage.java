package org.abeyj.sample;

import org.abeyj.response.committee.CommitteeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class CommitteeUsage extends AbeyjTestNet {
    private static final Logger logger = LoggerFactory.getLogger(CommitteeUsage.class);

    public void getCommitteeByNumber() {
        BigInteger committeeNumber = new BigInteger("100");
        CommitteeInfo committeeInfo = abeyjRequest.getCommitteeByNumber(committeeNumber);
        logger.info("committeeInfo=[{}]", committeeInfo);
    }

    public void testGetCurrentCommitteeNumber() {
        Integer currentCommitteeNumber = abeyjRequest.getCurrentCommitteeNumber();
        logger.info("current committee number=[{}]", currentCommitteeNumber);
    }

}
