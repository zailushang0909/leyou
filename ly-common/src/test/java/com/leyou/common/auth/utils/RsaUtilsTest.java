package com.leyou.common.auth.utils;

import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;


public class RsaUtilsTest {
    private String privateFilePath = "E:/rsa/id_rsa";
    private String publicFilePath = "E:/rsa/id_rsa.pub";

    @Test
    public void getPublicKey() throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey(publicFilePath);
        System.out.println(publicKey.toString());
    }

    @Test
    public void getPrivateKey() throws Exception {
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilePath);
        System.out.println(privateKey.getFormat());
    }

    @Test
    public void generateKey() throws Exception {
        RsaUtils.generateKey(publicFilePath,privateFilePath,"张天赐",10);
    }
}