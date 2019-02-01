package pl.jsql.api.utils

import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Klasa metod nieprzyporządkowanych
 * @author Dawid
 *
 */
class Utils {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile('^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$', Pattern.CASE_INSENSITIVE)

    static boolean isEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
        return matcher.find()
    }

    /**
     * Zwraca obecną datę w formacie JSON
     * @return
     */
    static String getCurrentDate() {
        Utils.getDate(new Date())
    }

    /**
     * Formatuje podaną datę do formatu JSON
     * @param date
     * @return
     */
    static String getDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX").format(date)
    }

    static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min

        return randomNum
    }

    static Boolean containsIgnoreCase(String str, String contain) {
        return str.toLowerCase().contains(contain.toLowerCase())
    }

}
