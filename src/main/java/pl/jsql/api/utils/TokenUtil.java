package pl.jsql.api.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.rmi.CORBA.Util;
import java.util.UUID;

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

    public static String generateToken(String identity, Integer length) {
        return RandomStringUtils.randomAlphanumeric(length) + identity;
    }

    public static String mixString(String s1, String s2, String s3){

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < s1.length(); i++){

            for(int k = i; k < s2.length(); k++){
                stringBuilder.append(s2.charAt(k));
            }

            stringBuilder.append(s1.charAt(i));

            for(int k = i; k < s3.length(); k++){
                stringBuilder.append(s3.charAt(k));
            }

        }

        return stringBuilder.toString();

    }

    public static String generateMixToken(String part1, String part2, Integer length) {
        return mixString(part1, part2, RandomStringUtils.randomAlphanumeric(length)).substring(0, length);
    }

    public static String generateMixToken(String part1, String part2, Integer minL, Integer maxL) {
        return generateMixToken(part1, part2, Utils.randInt(minL, maxL));
    }


    public static String generateToken(String identity, Integer minLenght, Integer maxLenght) {
        return RandomStringUtils.randomAlphanumeric(Utils.randInt(minLenght, maxLenght)) + String.valueOf(identity);
    }

    public static String generateToken(Integer identity, Integer minLenght, Integer maxLenght) {
        return RandomStringUtils.randomAlphanumeric(Utils.randInt(minLenght, maxLenght)) + String.valueOf(identity);
    }

    public static String generateToken(String name) {
        return HashingUtil.encode(name+UUID.randomUUID());
    }

    public static String hash(String str){
        return DigestUtils.sha256Hex(str + System.currentTimeMillis());
    }

    public static String randomSalt(){
        return hash(String.valueOf(UUID.randomUUID())).substring(0, 10);
    }

}
