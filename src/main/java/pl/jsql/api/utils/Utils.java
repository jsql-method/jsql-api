package pl.jsql.api.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Klasa metod nieprzyporządkowanych
 *
 * @author Dawid
 */
public class Utils {

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
