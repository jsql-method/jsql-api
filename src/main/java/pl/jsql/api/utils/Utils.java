package pl.jsql.api.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Klasa metod nieprzyporządkowanych
 *
 * @author Dawid
 */
public class Utils {

    public static String readInputStreamToString(HttpURLConnection connection, boolean error) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(error ? connection.getErrorStream() : connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static boolean isAnonime(String str){

        if(str == null){
            return true;
        }

        return str.contains("**");
    }

    public static String anonimize(String str){

        String str2 = "";

        if(str.length() > 4){
            str2 += str.substring(0, 1);
        }

        for(String s : str.split("")){
            str2 += "*";
        }


        if(str.length() > 4){
            str2 += str.substring(str.length() - 1, str.length());
        }

        return str2;

    }

    /**
     * Zwraca obecną datę w formacie JSON
     */
    public static String getCurrentDate() {
        return Utils.getDate(new Date());
    }

    /**
     * Formatuje podaną datę do formatu JSON
     */
    public static String getDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
    }

    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;

    }

    public static Boolean containsIgnoreCase(String str, String contain) {
        return str.toLowerCase().contains(contain.toLowerCase());
    }

}
