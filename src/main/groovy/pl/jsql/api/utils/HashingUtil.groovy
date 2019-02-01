package pl.jsql.api.utils

import java.security.MessageDigest

/**
 * Usługa zabezpieczająca hasła użytkowników
 * @author Dawid
 *
 */
class HashingUtil {

    static String encode(def options, String query) {

        return MessageDigest.getInstance(options.encodingAlgorithm).digest(query.getBytes("UTF-8")).encodeBase64().toString()
    }

    static String encode(String name) {

        return MessageDigest.getInstance("MD5").digest(name.getBytes("UTF-8")).encodeBase64().toString()
    }

}
