package org.abeyj.sample;

import org.abeyj.response.Reward.ChainRewardContent;
import org.abeyj.response.fast.FastBlock;
import org.abeyj.response.snail.SnailRewardContenet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Map;

public class RewardUsage extends AbeyjTestNet {
    private static final Logger logger = LoggerFactory.getLogger(RewardUsage.class);

    /**
     * get snail reward address and the balance of the address
     */
    public void getSnailBalanceChange() {
        BigInteger snailNumber = new BigInteger("2");
        Map<String, String> addrWithBalance = abeyjRequest.getSnailBalanceChange(snailNumber);
        logger.info("addrWithBalance=[{}]", addrWithBalance);
    }


    /**
     * get snailReward content  by snailNumber
     * call abey_getChainRewardContent by empty address
     */
    public void getSnailRewardContent() {
        BigInteger snailNumber = new BigInteger("55000");
        ChainRewardContent snailChainRewardContent = abeyjRequest.getSnailRewardContent(snailNumber);
        System.out.println("snailChainRewardContent=" + snailChainRewardContent.toString());
    }

    public void getSnailRewardContent_Old() {
        BigInteger snailNumber = new BigInteger("55000");
        SnailRewardContenet snailRewardContenet = abeyjRequest.getSnailRewardContent_Old(snailNumber);
        System.out.println("snailRewardContenet=" + snailRewardContenet.toString());
    }


    public void getAddressesSnailReward() {
        BigInteger snailNumber = new BigInteger("2");
        Map<String, BigInteger> addressSnailReward = abeyjRequest.getAddressesSnailReward(snailNumber);
        logger.info("addressSnailReward=[{}]", addressSnailReward);
    }

    public void getFastBockOfReward() {
        BigInteger snailNumber = new BigInteger("1");
        FastBlock fastBlock = abeyjRequest.getFastBockOfReward(snailNumber);
        logger.info("fastBlock=[{}]", fastBlock);
        System.out.println("fastBlock="+fastBlock);
    }


}
