package pl.jsql.api.utils;

import pl.jsql.api.model.hashing.Options;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Usługa zabezpieczająca hasła użytkowników
 * @author Dawid
 *
 */
public class  HashingUtil {

    public static String encode(Options options, String query) {

        try {
            return MessageDigest.getInstance(options.encodingAlgorithm.toString()).digest(query.getBytes("UTF-8")).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String encode(String name) {

        try {
            return MessageDigest.getInstance("SHA256").digest(name.getBytes("UTF-8")).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;

    }

}
