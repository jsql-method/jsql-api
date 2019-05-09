package pl.jsql.api.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import javax.rmi.CORBA.Util;
import java.util.Date;
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

    public static String mixString(String s1, String s2, Integer length){

        String timestamp = new Date().getTime()+"";
        StringBuilder stringBuilder = new StringBuilder();

        int c = 0;
        for(int i = 0; i < s1.length(); i++){

            for(int k = i; k < s2.length(); k++){
                stringBuilder.append(s2.charAt(k));

                if(c < timestamp.length()){
                    stringBuilder.append(timestamp.charAt(c));
                    c++;
                }

            }


            stringBuilder.append(s1.charAt(i));

        }

        String finalToken = stringBuilder.toString();

        System.out.println("length : "+length);
        System.out.println("finalToken.length() : "+finalToken.length());

        if(finalToken.length() > length){
            finalToken = finalToken.substring(0, length);
        }else{
            System.out.println("length-finalToken.length() : "+(length-finalToken.length()));
            finalToken = finalToken+TokenUtil.getAlphaNumericString(length-finalToken.length());
        }

        System.out.println("finalToken.length() po : "+finalToken.length());


        return finalToken;

    }

    public static String generateMixToken(String part1, String part2, Integer length) {
        return mixString(part1, part2, length);
    }

    public static String generateMixToken(String part1, String part2, Integer minL, Integer maxL) {
        return generateMixToken(part1, part2, Utils.randInt(minL, maxL));
    }

    static String getAlphaNumericString(int n)
    {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
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
