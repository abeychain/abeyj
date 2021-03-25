package org.abeyj.address;


import static org.abeyj.crypto.Hash.sha3;


public class AddressHelper {

    private static String base58Top = "ABEY";
    private static String byteTop = "43E552";
    private static byte byteTop1 = (byte) 0x43;
    private static byte byteTop2 = (byte) 0xE5;
    private static byte byteTop3 = (byte) 0x52;
    static int ADDRESS_SIZE = 23;

    public static String getHexAddress(String base58Address) {
        if (base58Address.startsWith(base58Top)) {
            String hexTop = toHexString(base58Address);
            return "0x" + hexTop.substring(6);
        }
        return null;
    }

    public static String getBase58Address(String hexAddress) {
        return addressHexToBase58Top(hexAddress);
    }


    public static String changeAddressToHex(String address) {
        if (address.startsWith(base58Top)) {
            return toHexString(address).substring(6);
        }
        return address;
    }

    public static String toHexString(String base58Address) {
        try {
            return ByteArray.toHexString(decodeFromBase58Check(base58Address));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对地址执行Base58编码
     *
     * @param addressBytes 地址字节数组
     * @return 地址
     */
    public static String addressBytesEncode58Check(byte[] addressBytes) {
        byte[] hash0 = sha3(addressBytes);
        byte[] hash1 = sha3(hash0);
        byte[] inputCheck = new byte[addressBytes.length + 4];
        System.arraycopy(addressBytes, 0, inputCheck, 0, addressBytes.length);
        System.arraycopy(hash1, 0, inputCheck, addressBytes.length, 4);
        return Base58.encode(inputCheck);
    }

    /**
     * 地址Base58解码成字节数组
     *
     * @param addressBase58 地址Base58格式
     * @return 字节数组
     */
    public static byte[] decodeFromBase58Check(String addressBase58) throws Exception {
        byte[] address = decode58Check(addressBase58);
        if (addressValid(address)) {
            return address;
        } else {
            throw new Exception("Invalid address");
        }
    }

    public static String addressHexToBase58(String addressHex) {
        byte[] toBytes = ByteArray.fromHexString(addressHex);
        return addressBytesEncode58Check(toBytes);
    }

    public static String addressHexToBase58Top(String addressHex) {
        if (addressHex.length() > 40) {
            addressHex = addressHex.substring(addressHex.length() - 40);
        }
        byte[] toBytes = ByteArray.fromHexString(byteTop + addressHex);
        return addressBytesEncode58Check(toBytes);
    }

    /**
     * Base58解码
     *
     * @param input Base58字符串
     * @return 字节数组
     */
    private static byte[] decode58Check(String input) throws Exception {
        byte[] decodeCheck = Base58.decode(input);
        if (decodeCheck.length <= 4) {
            throw new Exception("invalid input");
        }
        byte[] decodeData = new byte[decodeCheck.length - 4];
        System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
        byte[] hash0 = sha3(decodeData);
        byte[] hash1 = sha3(hash0);
        if (hash1[0] == decodeCheck[decodeData.length]
                && hash1[1] == decodeCheck[decodeData.length + 1]
                && hash1[2] == decodeCheck[decodeData.length + 2]
                && hash1[3] == decodeCheck[decodeData.length + 3]) {
            return decodeData;
        } else {
            throw new Exception("invalid input");
        }
    }

    /**
     * 地址校验
     *
     * @param address 地址Base58 解码后的字节数组
     * @return 校验结果
     */
    private static boolean addressValid(byte[] address) {
        if (address.length != ADDRESS_SIZE) {
            return false;
        }
        byte prefixByte1 = address[0];
        byte prefixByte2 = address[1];
        byte prefixByte3 = address[2];

        if (prefixByte1 == byteTop1
                && prefixByte2 == byteTop2
                && prefixByte3 == byteTop3) {
            return true;
        }
        return false;
    }


    public static void main(String[] args) {
        String a = getBase58Address("0x46498c274686be5e3c01b9268ea4604da5142265");
        System.out.println(a);
        a = getHexAddress(a);
        System.out.println(a);
    }
}
