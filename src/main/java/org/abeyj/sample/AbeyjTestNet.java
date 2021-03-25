package org.abeyj.sample;

import org.abeyj.AbeyjRequest;
import org.abeyj.common.AddressConstant;
import org.abeyj.common.Constant;
import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.http.HttpService;

public class AbeyjTestNet {

    public AbeyjRequest abeyjRequest = new AbeyjRequest(Constant.RPC_TESTNET_URL);

    public Abeyj abeyj = Abeyj.build(new HttpService(Constant.RPC_TESTNET_URL));

    public static int chainId = Constant.CHAINID_TESTNET;

    public static String fromPrivatekey = AddressConstant.fromPrivateKey;
    public static String fromAddress = AddressConstant.fromAddress;
    public static String toAddress = AddressConstant.toAddress;

    public static String paymentPrivateKey = AddressConstant.paymentPrivatekey;
    public static String paymentAddress = AddressConstant.paymentAddress;

}

