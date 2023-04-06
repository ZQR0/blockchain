package com.blockchain.javablockchain.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
* @author Yaroslav
* In this file we will contain methods, that allow us to use hash-algorithms
*/
@Component
@Slf4j
public class KeyUtils {

    private static final String ALGO = "SHA-256";

    public String calculateHash(@NonNull String text) {
        final MessageDigest digest;

        try {
            digest = MessageDigest.getInstance(ALGO);
        } catch (NoSuchAlgorithmException ex) {
            log.info(String.format("Algorithm %s not found", ALGO));
            return "HASH_ERROR";
        }

        final byte[] BYTES = digest.digest(text.getBytes());
        StringBuilder toHEXString = new StringBuilder();

        for (byte b : BYTES) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1) toHEXString.append('0');

            toHEXString.append(hex);
        }

        return toHEXString.toString();
    }
}
