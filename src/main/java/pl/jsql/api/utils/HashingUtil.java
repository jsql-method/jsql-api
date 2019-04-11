package pl.jsql.api.utils;

import org.apache.commons.codec.digest.DigestUtils;
import pl.jsql.api.dto.response.OptionsResponse;

/**
 * Usługa zabezpieczająca hasła użytkowników
 *
 * @author Dawid
 */
public class HashingUtil {

    public static String encode(OptionsResponse options, String query) {

        switch (options.encodingAlgorithm) {
            case MD2:
                return DigestUtils.md2Hex(query);
            case MD5:
                return DigestUtils.md5Hex(query);
            case SHA1:
                return DigestUtils.sha1Hex(query);
            case SHA256:
                return DigestUtils.sha256Hex(query);
            case SHA384:
                return DigestUtils.sha384Hex(query);
            case SHA512:
                return DigestUtils.sha512Hex(query);
        }

        return null;

    }

    public static String encode(String name) {
        return DigestUtils.sha256Hex(name);
    }

}
