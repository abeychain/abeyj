package org.abeyj.sample.erc20;

import org.abeyj.common.AddressConstant;
import org.abeyj.common.Constant;
import org.abeyj.sample.AbeyjTestNet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.abeyj.abi.FunctionEncoder;
import org.abeyj.abi.FunctionReturnDecoder;
import org.abeyj.abi.TypeReference;
import org.abeyj.abi.datatypes.*;
import org.abeyj.abi.datatypes.generated.Uint256;
import org.abeyj.abi.datatypes.generated.Uint8;
import org.abeyj.crypto.Credentials;
import org.abeyj.crypto.RawTransaction;
import org.abeyj.crypto.TransactionEncoder;
import org.abeyj.protocol.core.DefaultBlockParameterName;
import org.abeyj.protocol.core.methods.request.Transaction;
import org.abeyj.protocol.core.methods.response.AbeyCall;
import org.abeyj.protocol.core.methods.response.AbeyGetTransactionCount;
import org.abeyj.protocol.core.methods.response.AbeySendTransaction;
import org.abeyj.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * based on TERC-20 token
 */
public class TokenClientUsage extends AbeyjTestNet {
    private static final Logger logger = LoggerFactory.getLogger(TokenClientUsage.class);

    /**
     * query TERC-20 token balance
     */
    public BigInteger getTokenBalance(String fromAddress, String contractAddress) {

        String methodName = "balanceOf";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address address = new Address(fromAddress);
        inputParameters.add(address);

        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

        BigInteger balanceValue = BigInteger.ZERO;
        try {
            AbeyCall abeyCall = abeyj.abeyCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(abeyCall.getValue(), function.getOutputParameters());
            if (abeyCall.getError() != null) {
                logger.error("getTokenBalance error={}", abeyCall.getError().getMessage());
            }
            if (results.size() == 0) {
                logger.error("contractAddress =[{}] is not exist", contractAddress);
                return balanceValue;
            }
            String resultVal = results.get(0).getValue().toString();
            balanceValue = new BigInteger(resultVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balanceValue;
    }

    /**
     * query TERC-20 token name
     *
     * @param contractAddress
     * @return
     */
    public String getTokenName(String contractAddress) {
        String methodName = "name";
        String name = null;
        String fromAddr = AddressConstant.EMPTY_ADDRESS;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);

        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);
        try {
            AbeyCall abeyCall = abeyj.abeyCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(abeyCall.getValue(), function.getOutputParameters());
            name = results.get(0).getValue().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * query TERC-20 token symbol
     *
     * @param contractAddress
     * @return
     */
    public String getTokenSymbol(String contractAddress) {
        String methodName = "symbol";
        String symbol = null;
        String fromAddr = AddressConstant.EMPTY_ADDRESS;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

        try {
            AbeyCall abeyCall = abeyj.abeyCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(abeyCall.getValue(), function.getOutputParameters());
            symbol = results.get(0).getValue().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return symbol;
    }

    /**
     * query TERC-20 token decimals
     *
     * @param contractAddress
     * @return
     */
    public int getTokenDecimals(String contractAddress) {
        String methodName = "decimals";
        String fromAddr = AddressConstant.EMPTY_ADDRESS;
        int decimal = 0;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Uint8> typeReference = new TypeReference<Uint8>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);

        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);
        try {
            AbeyCall abeyCall = abeyj.abeyCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(abeyCall.getValue(), function.getOutputParameters());
            decimal = Integer.parseInt(results.get(0).getValue().toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return decimal;
    }

    /**
     * query TERC-20 token total supply
     *
     * @param contractAddress
     * @return
     */
    public BigInteger getTokenTotalSupply(String contractAddress) {
        String methodName = "totalSupply";
        String fromAddr = AddressConstant.EMPTY_ADDRESS;
        BigInteger totalSupply = BigInteger.ZERO;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);

        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);
        try {
            AbeyCall abeyCall = abeyj.abeyCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(abeyCall.getValue(), function.getOutputParameters());
            totalSupply = (BigInteger) results.get(0).getValue();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return totalSupply;
    }

    /**
     * TERC-20 token transfer
     *
     * @param contractAddress
     * @param toAddress
     * @param from_privateKey
     */
    public void sendTokenTransaction(String contractAddress, String toAddress, String from_privateKey) {
        try {
            Credentials from_credentials = Credentials.create(from_privateKey);
            String from_address = from_credentials.getAddress();
            BigInteger amount = Constant.DEFAULT_VALUE;
            String methodName = "transfer";
            List<Type> inputParameters = new ArrayList<>();
            List<TypeReference<?>> outputParameters = new ArrayList<>();

            Address tAddress = new Address(toAddress);

            Uint256 value = new Uint256(amount);
            inputParameters.add(tAddress);
            inputParameters.add(value);

            TypeReference<Bool> typeReference = new TypeReference<Bool>() {
            };
            outputParameters.add(typeReference);
            Function function = new Function(methodName, inputParameters, outputParameters);
            String data = FunctionEncoder.encode(function);

            AbeyGetTransactionCount abeyGetTransactionCount = abeyj
                    .abeyGetTransactionCount(from_address, DefaultBlockParameterName.PENDING).sendAsync().get();
            BigInteger nonce = abeyGetTransactionCount.getTransactionCount();

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce, Constant.DEFAULT_GASPRICE,
                    Constant.DEFAULT_CONTRACT_GASLIMIT, contractAddress, data);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, from_credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            AbeySendTransaction abeySendTransaction = abeyj.abeySendRawTransaction(hexValue).sendAsync().get();
            String txHash = abeySendTransaction.getTransactionHash();
            if (abeySendTransaction.getError() != null) {
                logger.error("sendTokenTransaction error" + abeySendTransaction.getError());
            }
            logger.info("txHash={}", txHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
