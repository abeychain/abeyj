package org.abeyj.sample;

import org.abeyj.common.AddressConstant;
import org.abeyj.response.Reward.ChainRewardContent;
import org.abeyj.response.Reward.RewardInfo;
import org.abeyj.response.Reward.SARewardInfos;
import org.abeyj.response.staking.AllStakingAccount;
import org.abeyj.response.staking.StakingAccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

public class StakingUsage extends AbeyjTestNet {
    private static final Logger logger = LoggerFactory.getLogger(StakingUsage.class);

    /**
     * useage:
     * get the proceeds of all the delegate addresses under a pledge node
     */
    public void getChainRewardContentByAddress() {
        BigInteger snailNumber = new BigInteger("39748");
        String stakingAddress = "0x01504af31ce7c2e922a4d44f4c46349ff6bc589c";

        ChainRewardContent chainRewardContent = abeyjRequest.getChainRewardContent(
                snailNumber, stakingAddress);
        logger.info("chainRewardContent=[{}]", chainRewardContent);

        BigInteger receviedSnailNumber = chainRewardContent.getNumber();
        BigInteger timeStamp = chainRewardContent.getTime();
        logger.info("snailNumber=[{}],receviedSnailNumber=[{}]", receviedSnailNumber, timeStamp);

        //gets the income of all the entrusted nodes under the pledge node
        List<RewardInfo> rewardInfos = chainRewardContent.getStakingReward();
        for (RewardInfo rewardInfo : rewardInfos) {
            logger.info(rewardInfo.toString());
        }
    }

    public void getChainRewardContent() {
        BigInteger snailNumber = new BigInteger("39748");
        String stakingAddress = AddressConstant.EMPTY_ADDRESS;
        ChainRewardContent chainRewardContent = abeyjRequest.getChainRewardContent(
                snailNumber, stakingAddress);
        if (chainRewardContent == null) {
            logger.error("chainRewardContent is null");
        }
        logger.info("chainRewardContent=[{}]", chainRewardContent);

        BigInteger receviedSnailNumber = chainRewardContent.getNumber();
        BigInteger timeStamp = chainRewardContent.getTime();
        logger.info("snailNumber=[{}],receviedSnailNumber=[{}]", receviedSnailNumber, timeStamp);

        //get all pledge nodes reward
        List<SARewardInfos> saRewardInfoList = chainRewardContent.getCommitteeReward();
        if (saRewardInfoList != null) {
            for (SARewardInfos saRewardInfo : saRewardInfoList) {
                List<RewardInfo> rewardInfoList = saRewardInfo.getItems();
                for (RewardInfo rewardInfo : rewardInfoList) {
                    logger.info("rewardInfo={}", rewardInfo);
                }
            }
        }

        //get snail block miner reward
        RewardInfo minerRewardInfo = chainRewardContent.getBlockminer();
        //get fruits miner reward
        List<RewardInfo> fruitRewardInfos = chainRewardContent.getFruitminer();
        //get foundation reward
        RewardInfo developerRewardInfo = chainRewardContent.getDeveloperReward();
    }


    /**
     * get pledge infos of all nodes and addresses
     */
    public void getAllStakingAccount() {
        BigInteger snailNumber = new BigInteger("39748");
        AllStakingAccount allStakingAccount = abeyjRequest.getAllStakingAccount();
        logger.info("allStakingAccount=[{}]", allStakingAccount);
    }

    /**
     * get pledge infos of all nodes and addresses
     */
    public void getStakingAccountInfo() {
        String stakingAddress = "0x01504af31ce7c2e922a4d44f4c46349ff6bc589c";
        StakingAccountInfo stakingAccountInfo = abeyjRequest.getStakingAccountInfo(
                stakingAddress);
        logger.info("stakingAccountInfo=[{}]", stakingAccountInfo);
    }
}
