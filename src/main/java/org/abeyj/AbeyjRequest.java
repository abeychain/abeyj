package org.abeyj;

import org.abeyj.abi.datatypes.Address;
import org.abeyj.common.AddressConstant;
import org.abeyj.common.Constant;
import org.abeyj.crypto.Credentials;
import org.abeyj.crypto.RawTransaction;
import org.abeyj.crypto.TransactionEncoder;
import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.methods.response.AbeyGetTransactionCount;
import org.abeyj.response.*;
import org.abeyj.response.AbeySnailBlockNumber;
import org.abeyj.response.Reward.ChainRewardContent;
import org.abeyj.response.Reward.RewardInfo;
import org.abeyj.response.Reward.SARewardInfos;
import org.abeyj.response.committee.CommitteeInfo;
import org.abeyj.response.fast.FastBlock;
import org.abeyj.response.snail.*;
import org.abeyj.response.staking.AllStakingAccount;
import org.abeyj.response.staking.StakingAccountInfo;
import org.abeyj.utils.Numeric;
import org.apache.commons.lang3.StringUtils;
import org.abeyj.protocol.AbeyjService;
import org.abeyj.protocol.core.DefaultBlockParameter;
import org.abeyj.protocol.core.DefaultBlockParameterName;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.methods.response.AbeyGetBalance;
import org.abeyj.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbeyjRequest {
    public AbeyjService abeyjService = null;

    private AbeyjRequest() {

    }

    public AbeyjRequest(String rpcUrl) {
        abeyjService = new HttpService(rpcUrl);
    }

    public AbeyjRequest(HttpService httpService) {
        this.abeyjService = httpService;
    }


    /**
     * query lock balance
     */
    public BigInteger getLockBalance(String address, DefaultBlockParameter defaultBlockParameter) {
        BigInteger balanceValue = BigInteger.ZERO;
        if (StringUtils.isBlank(address)) {
            return balanceValue;
        }
        if (defaultBlockParameter == null) {
            defaultBlockParameter = DefaultBlockParameterName.LATEST;
        }
        try {
            AbeyGetBalance abeyGetBalance = new Request<>("abey_getLockBalance",
                    Arrays.asList(address, defaultBlockParameter.getValue()),
                    abeyjService,
                    AbeyGetBalance.class).send();
            balanceValue = abeyGetBalance.getBalance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balanceValue;
    }


    /**
     * get FastBlock by fastNumber
     *
     * @param fastBlockNumber
     * @param returnFullTransactionObjects
     * @return
     */
    public FastBlock getFastBlockByNumber(BigInteger fastBlockNumber, boolean returnFullTransactionObjects) {
        FastBlock fastBlock = null;
        try {
            AbeyFastBlock abeyFastBlock = new Request<>(
                    Constant.BLOCK_BYNUMBER,
                    Arrays.asList(DefaultBlockParameter.valueOf(fastBlockNumber).getValue(), returnFullTransactionObjects),
                    abeyjService,
                    AbeyFastBlock.class).send();
            fastBlock = abeyFastBlock.getFastBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fastBlock;
    }

    /**
     * get FastBlock by hash
     *
     * @param fastHash
     * @param returnFullTransactionObjects
     * @return
     */
    public FastBlock getFastBlockByHash(String fastHash, boolean returnFullTransactionObjects) {
        FastBlock fastBlock = null;
        try {
            AbeyFastBlock abeyFastBlock = new Request<>(
                    Constant.BLOCK_BYHASH,
                    Arrays.asList(fastHash, returnFullTransactionObjects),
                    abeyjService,
                    AbeyFastBlock.class).send();
            fastBlock = abeyFastBlock.getFastBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fastBlock;
    }

    /**
     * get current fastNumber on the chain
     *
     * @return
     */
    public BigInteger getCurrentFastNumber() {
        BigInteger fastNumber = null;
        try {
            AbeyFastBlockNumber abeyFastBlockNumber = new Request<>(
                    Constant.CURRENT_BLOCK_NUMBER,
                    Arrays.asList(),
                    abeyjService,
                    AbeyFastBlockNumber.class).send();
            fastNumber = abeyFastBlockNumber.getBlockNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fastNumber;
    }

    /**
     * get snail reward address and balance of the address
     *
     * @param snailBlockNumber
     * @return
     */
    public Map<String, String> getSnailBalanceChange(BigInteger snailBlockNumber) {
        Map<String, String> addrWithBalance = new HashMap<String, String>();
        try {
            AbeyBalanceChange abeyBalanceChange = new Request<>(
                    Constant.BALANCE_CHANGE_BY_SNAIL_NUMBER,
                    Arrays.asList(DefaultBlockParameter.valueOf(snailBlockNumber).getValue()),
                    abeyjService,
                    AbeyBalanceChange.class).send();
            BalanceChange balanceChange = abeyBalanceChange.getBalanceChange();
            if (balanceChange != null) {
                addrWithBalance = balanceChange.getAddrWithBalance();
                /*for (Map.Entry<String, String> entry : addrWithBalance.entrySet()) {
                    System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addrWithBalance;
    }

    /**
     * get snail reward content by snail number
     * inclued blockminer、fruitminer、committeReward、foundationReward
     * <p>
     * <p>
     * attention:getSnailRewardContent get by rpc of "abey_getChainRewardContent"
     *
     * @param snailNumber
     * @return
     */
    public ChainRewardContent getSnailRewardContent(BigInteger snailNumber) {
        ChainRewardContent chainRewardContent = null;
        if (snailNumber == null) {
            return null;
        }
        DefaultBlockParameter blockParameter = DefaultBlockParameter.valueOf(snailNumber);
        try {
            AbeyChainRewardContent abeyChainRewardContent = new Request<>(
                    Constant.CHAIN_REWARD_CONTENT,
                    Arrays.asList(blockParameter.getValue(), AddressConstant.EMPTY_ADDRESS),
                    abeyjService,
                    AbeyChainRewardContent.class).send();
            chainRewardContent = abeyChainRewardContent.getChainRewardContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chainRewardContent;
    }

    public SnailRewardContenet getSnailRewardContent_Old(BigInteger snailNumber) {
        SnailRewardContenet snailRewardContenet = null;
        if (snailNumber == null) {
            return null;
        }
        DefaultBlockParameter blockParameter = DefaultBlockParameter.valueOf(snailNumber);
        try {
            AbeySnailRewardContent abeySnailRewardContent = new Request<>(
                    Constant.SNAIL_REWARD_CONTENT,
                    Arrays.asList(blockParameter.getValue()),
                    abeyjService,
                    AbeySnailRewardContent.class).send();
            snailRewardContenet = abeySnailRewardContent.getSnailRewardContenet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return snailRewardContenet;
    }


    /**
     * get gather addresses snail reward by snailNumber
     *
     * @param snailNumber
     * @return
     */
    public Map<String, BigInteger> getAddressesSnailReward(BigInteger snailNumber) {
        //慢链奖励中涉及所有的地址
        Map<String, BigInteger> snailRewardWithAddr = new HashMap<String, BigInteger>();
        ChainRewardContent chainRewardContent = getSnailRewardContent(snailNumber);
        if (chainRewardContent == null) {
            return snailRewardWithAddr;
        }

        RewardInfo minerRewardInfo = chainRewardContent.getBlockminer();
        gatherAddressBalance(snailRewardWithAddr, minerRewardInfo);

        RewardInfo developerReward = chainRewardContent.getDeveloperReward();
        gatherAddressBalance(snailRewardWithAddr, developerReward);

        List<RewardInfo> fruitRewardInfos = chainRewardContent.getFruitminer();
        if (fruitRewardInfos != null && fruitRewardInfos.size() != 0) {
            for (RewardInfo fruitRewardInfo : fruitRewardInfos) {
                gatherAddressBalance(snailRewardWithAddr, fruitRewardInfo);
            }
        }

        List<SARewardInfos> saRewardInfosList = chainRewardContent.getCommitteeReward();
        if (saRewardInfosList != null && saRewardInfosList.size() > 0) {
            for (SARewardInfos saRewardInfo : saRewardInfosList) {
                if (saRewardInfo != null && saRewardInfo.getItems() != null
                        && saRewardInfo.getItems().size() != 0) {
                    for (RewardInfo committeeRewardInfo : saRewardInfo.getItems()) {
                        gatherAddressBalance(snailRewardWithAddr, committeeRewardInfo);
                    }
                }
            }
        }
        return snailRewardWithAddr;
    }

    private Map<String, BigInteger> gatherAddressBalance(
            Map<String, BigInteger> snailRewardWithAddr, RewardInfo rewardInfo) {
        if (rewardInfo == null) {
            return snailRewardWithAddr;
        }
        String address = rewardInfo.getAddress();
        if (snailRewardWithAddr.get(address) != null) {
            BigInteger balance = snailRewardWithAddr.get(address);
            balance = balance.add(rewardInfo.getAmount());
            snailRewardWithAddr.put(address, balance);
        } else {
            snailRewardWithAddr.put(address, rewardInfo.getAmount());
        }
        return snailRewardWithAddr;
    }


    /**
     * get snail Block by snailNumber
     *
     * @param snailNumber
     * @param inclFruit   whether include fruits info
     * @return
     */
    public SnailBlock getSnailBlockByNumber(BigInteger snailNumber, boolean inclFruit) {
        SnailBlock snailBlock = null;
        try {
            AbeySnailBlock abeySnailBlock = new Request<>(
                    "abey_getSnailBlockByNumber",
                    Arrays.asList(DefaultBlockParameter.valueOf(snailNumber).getValue(), inclFruit),
                    abeyjService,
                    AbeySnailBlock.class).send();
            snailBlock = abeySnailBlock.getSnailBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return snailBlock;
    }

    /**
     * get snailHash by snailNumber
     *
     * @param snailNumber
     * @return if return null, donnot have generate the snailNumber
     */
    public String getSnailHashByNumber(BigInteger snailNumber) {
        String snailHash = null;
        try {
            AbeySnailHash abeySnailHash = new Request<>(
                    Constant.SNAIL_HASH_BY_NUMBER,
                    Arrays.asList(DefaultBlockParameter.valueOf(snailNumber).getValue()),
                    abeyjService,
                    AbeySnailHash.class).send();
            snailHash = abeySnailHash.getSnailHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return snailHash;
    }

    /**
     * get snailBlock by snailHash
     *
     * @param snailHash
     * @param inclFruit whether include fruits info
     * @return
     */
    public SnailBlock getSnailBlockByHash(String snailHash, boolean inclFruit) {
        SnailBlock snailBlock = null;
        try {
            AbeySnailBlock abeySnailBlock = new Request<>(
                    Constant.SNAIL_BLOCK_BY_HASH,
                    Arrays.asList(snailHash, inclFruit),
                    abeyjService,
                    AbeySnailBlock.class).send();
            snailBlock = abeySnailBlock.getSnailBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return snailBlock;
    }

    /**
     * get current snail block number
     *
     * @return
     */
    public BigInteger getCurrentSnailNumber() {
        BigInteger snailNumber = null;
        try {
            AbeySnailBlockNumber abeySnailBlockNumber = new Request<>(
                    Constant.SNAIL_BLOCK_NUMBER,
                    Arrays.asList(),
                    abeyjService,
                    AbeySnailBlockNumber.class).send();
            snailNumber = abeySnailBlockNumber.getSnailBlockNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return snailNumber;
    }


    /**
     * get committeeInfo by committeeNumber
     *
     * @param committeeNumber
     * @return
     */
    public CommitteeInfo getCommitteeByNumber(BigInteger committeeNumber) {
        CommitteeInfo committeeInfo = null;
        try {
            AbeyCommittee abeyCommittee = new Request<>(
                    Constant.COMMITTEE_BY_NUMBER,
                    Arrays.asList(DefaultBlockParameter.valueOf(committeeNumber).getValue()),
                    abeyjService,
                    AbeyCommittee.class).send();
            committeeInfo = abeyCommittee.getCommittee();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return committeeInfo;
    }

    /**
     * get current committee number
     *
     * @return
     */
    public Integer getCurrentCommitteeNumber() {
        Integer currentCommitteeNumber = null;
        try {
            AbeyCommitteeNumber abeyCommitteeNumber = new Request<>(
                    Constant.CURRENT_COMMITTEE_NUMBER,
                    Arrays.asList(),
                    abeyjService,
                    AbeyCommitteeNumber.class).send();
            currentCommitteeNumber = abeyCommitteeNumber.getCommitteeNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentCommitteeNumber;
    }

    /**
     * send abey raw transaction
     *
     * @param signedTransactionData
     * @return
     */
    public AbeySendTransaction abeySendRawTransaction(String signedTransactionData) {
        AbeySendTransaction abeySendTrueTransaction = null;
        try {
            abeySendTrueTransaction = new Request<>(
                    Constant.SEND_ABEY_RAW_TRANSACTION,
                    Arrays.asList(signedTransactionData),
                    abeyjService,
                    AbeySendTransaction.class).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return abeySendTrueTransaction;
    }

    /**
     * get balance with address which has changed by fast number
     *
     * @param fastNumber
     * @return
     */
    public FastBalanceChange getStateChangeByFastNumber(BigInteger fastNumber) {
        FastBalanceChange fastBalanceChange = null;
        try {
            AbeyFastBalanceChange abeyFastBalanceChange = new Request<>(
                    Constant.STATE_CHANGE_BY_FAST_NUMBER,
                    Arrays.asList(DefaultBlockParameter.valueOf(fastNumber).getValue()),
                    abeyjService,
                    AbeyFastBalanceChange.class).send();
            fastBalanceChange = abeyFastBalanceChange.getFastBalanceChange();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fastBalanceChange;
    }


    /**
     * get staking info by account
     *
     * @param account
     * @return
     */
    public StakingAccountInfo getStakingAccountInfo(String account) {
        StakingAccountInfo stakingAccountInfo = null;
        try {
            AbeyStakingAccountInfo abeyStakingAccountInfo = new Request<>(
                    Constant.STAKING_ACCOUNT,
                    Arrays.asList(
                            DefaultBlockParameterName.LATEST,
                            account),
                    abeyjService,
                    AbeyStakingAccountInfo.class).send();
            stakingAccountInfo = abeyStakingAccountInfo.getStakingAccountInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stakingAccountInfo;
    }

    /**
     * get all staking account infos
     *
     * @return
     */
    public AllStakingAccount getAllStakingAccount() {
        AllStakingAccount allStakingAccount = null;
        try {
            AbeyAllStakingAccountInfo abeyAllStakingAccountInfo = new Request<>(
                    Constant.ALL_STAKING_ACCOUNT,
                    Arrays.asList(DefaultBlockParameterName.LATEST),
                    abeyjService,
                    AbeyAllStakingAccountInfo.class).send();
            allStakingAccount = abeyAllStakingAccountInfo.getAllStakingAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allStakingAccount;
    }

    /**
     * get the proceeds of all the delegate addresses under a pledge node(stakingAddress) in snailNumber block
     *
     * @param snailNumber
     * @param stakingAddress
     * @return
     */
    public ChainRewardContent getChainRewardContent(BigInteger snailNumber, String stakingAddress) {
        ChainRewardContent chainRewardContent = null;

        if (snailNumber == null || StringUtils.isBlank(stakingAddress)) {
            return null;
        }
        DefaultBlockParameter blockParameter = DefaultBlockParameter.valueOf(snailNumber);
        try {
            AbeyChainRewardContent abeyChainRewardContent = new Request<>(
                    Constant.CHAIN_REWARD_CONTENT,
                    Arrays.asList(blockParameter.getValue(), stakingAddress),
                    abeyjService,
                    AbeyChainRewardContent.class).send();
            chainRewardContent = abeyChainRewardContent.getChainRewardContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chainRewardContent;
    }


    public FastBlock getFastBockOfReward(BigInteger snailRewardNumber) {
        DefaultBlockParameter blockParameter = DefaultBlockParameter.valueOf(snailRewardNumber);
        FastBlock fastBlock = null;
        try {
            AbeyFastBlock abeyFastBlock = new Request<>(
                    Constant.FAST_BLOCK_OF_REWARD,
                    Arrays.asList(blockParameter.getValue()),
                    abeyjService,
                    AbeyFastBlock.class).send();
            fastBlock = abeyFastBlock.getFastBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fastBlock;
    }


    public static void main(String[] args) {
        Abeyj abeyj = Abeyj.build(new HttpService("http://18.138.171.105:8545"));
        Credentials credentials = Credentials.create("229ca04fb83ec698296037c7d2b04a731905df53b96c260555cbeed9e4c64036");
        String from_address = credentials.getAddress();
        BigInteger nonce = BigInteger.ZERO;
        try {
            AbeyGetTransactionCount ethGetTransactionCount = abeyj.abeyGetTransactionCount(from_address, DefaultBlockParameterName.PENDING).send();
            nonce = ethGetTransactionCount.getTransactionCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, new BigInteger("150000000000"),
                new BigInteger("60000"), "ABEYDhCp7be7fXUrpBTcAcZDiwYPZXT26yqSK",new BigInteger("10000000"));
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, 178, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        org.abeyj.protocol.core.methods.response.AbeySendTransaction abeySendTransaction = null;
        try {
            abeySendTransaction = abeyj.abeySendRawTransaction(hexValue).sendAsync().get();
            String transactionHash = abeySendTransaction.getTransactionHash();
            System.out.println(transactionHash);
        }catch (Exception e){
            e.printStackTrace();
        }


        Address address = new Address("ABEYDhCp7be7fXUrpBTcAcZDiwYPZXT26yqSK");

    }

}
