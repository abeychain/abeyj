package org.abeyj.common;

import org.abeyj.utils.Convert;

import java.math.BigInteger;

public class Constant {

    public static String RPC_MAINNET_URL = "http://18.138.171.105:8545";
    public static int CHAINID_MAINNET = 179;

    public static String RPC_TESTNET_URL = "http://18.138.171.105:8545";
    public static int CHAINID_TESTNET = 178;

    public static BigInteger DEFAULT_GASPRICE = Convert.toWei("2", Convert.Unit.GWEI).toBigInteger();

    public static BigInteger DEFAULT_GASLIMIT = Convert.toWei("21000", Convert.Unit.WEI).toBigInteger();

    public static BigInteger DEFAULT_CONTRACT_GASLIMIT = Convert.toWei("200000", Convert.Unit.WEI).toBigInteger();

    public static BigInteger DEFAULT_VALUE = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();

    public static BigInteger DEFAULT_FEE = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();


    public static String BALANCE_CHANGE_BY_SNAIL_NUMBER = "abey_getBalanceChangeBySnailNumber";

    public static String BLOCK_BYNUMBER = "abey_getBlockByNumber";

    public static String BLOCK_BYHASH = "abey_getBlockByHash";

    public static String CURRENT_BLOCK_NUMBER = "abey_blockNumber";

    public static String STAKING_ACCOUNT = "impawn_getStakingAccount";

    public static String ALL_STAKING_ACCOUNT = "impawn_getAllStakingAccount";

    public static String CHAIN_REWARD_CONTENT = "abey_getChainRewardContent";

    public static String SNAIL_REWARD_CONTENT = "abey_getSnailRewardContent";

    public static String FAST_BLOCK_OF_REWARD = "abey_getRewardBlock";

    public static String SNAIL_HASH_BY_NUMBER = "abey_getSnailHashByNumber";

    public static String SNAIL_BLOCK_NUMBER = "abey_snailBlockNumber";

    public static String SNAIL_BLOCK_BY_HASH = "abey_getSnailBlockByHash";

    public static String STATE_CHANGE_BY_FAST_NUMBER = "abey_getStateChangeByFastNumber";

    public static String COMMITTEE_BY_NUMBER = "abey_getCommittee";

    public static String CURRENT_COMMITTEE_NUMBER = "abey_committeeNumber";

    public static String SEND_ABEY_RAW_TRANSACTION = "abey_sendAbeyRawTransaction";

}
