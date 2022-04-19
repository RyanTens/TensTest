package com.tens.test;

import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TestCheck {
    public String checkMD(String str) {
        DigestInputStream dis = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void toMD5(String text) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(text.getBytes());
            byte[] b = md5.digest();
            int i;
            StringBuffer sb = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }
            System.out.println("32位: " + sb.toString());// 32位的加密
            System.out.println("16位: " + sb.toString().substring(8, 24));// 16位的加密，其实就是32位加密后的截取
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TestCheck().toMD5("TENS");
    }
}
