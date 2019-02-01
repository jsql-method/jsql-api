package pl.jsql.api.utils

import org.apache.commons.lang3.RandomStringUtils

/**
 * Usługa obsługująca tokeny zabezpieczające
 * @author Dawid
 *
 */
class TokenUtil {

    /**
     * Generuje token autoryzacyjny włączając podane informacje
     * @param identity
     * @param passHash
     * @return
     */
    static String generateToken(def identity, def length) {
        return RandomStringUtils.randomAlphanumeric(length as Integer) + String.valueOf(identity)
    }

    static String generateToken(def identity, def minLenght, def maxLenght) {
        return RandomStringUtils.randomAlphanumeric(Utils.randInt(minLenght as Integer,maxLenght as Integer)) + String.valueOf(identity)
    }

    static String generateToken(String name) {
        return RandomStringUtils.randomAlphanumeric(20)+HashingUtil.encode(name)+RandomStringUtils.randomAlphanumeric(20)
    }

}
