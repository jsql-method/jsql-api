package pl.jsql.api.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Usługa obsługująca tokeny zabezpieczające
 * @author Dawid
 *
 */
public class  TokenUtil {

    /**
     * Generuje token autoryzacyjny włączając podane informacje
     */
    public static String generateToken(Integer identity, Integer length) {
        return RandomStringUtils.randomAlphanumeric(length) + String.valueOf(identity);
    }

    public static String generateToken(Integer identity, Integer minLenght, Integer maxLenght) {
        return RandomStringUtils.randomAlphanumeric(Utils.randInt(minLenght, maxLenght)) + String.valueOf(identity);
    }

    public static String generateToken(String name) {
        return RandomStringUtils.randomAlphanumeric(20)+HashingUtil.encode(name)+RandomStringUtils.randomAlphanumeric(20);
    }

    public static String hash(String str){
        return DigestUtils.md5Hex(str + System.currentTimeMillis());
    }

}
