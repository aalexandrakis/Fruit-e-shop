package com.aalexandrakis.fruit_e_shop;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Created by aalexandrakis on 16/12/2014.
 */
public class Commons {
    public static String URL = "http://fruitshop-aalexandrakis.rhcloud.com/rest/services";
    public static String URL_COMPLETE_ORDER = "http://fruitshop-aalexandrakis.rhcloud.com/completeOrder";
//    public static String URL = "http://192.168.173.1:9090/fruitShopWicket/rest/services";
//    public static String URL_COMPLETE_ORDER = "http://192.168.173.1:9090/fruitShopWicket/completeOrder";

    public static String encryptPassword(String password) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
