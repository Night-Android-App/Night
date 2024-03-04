package night.app.services;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Password {
    private static HashMap<String, String> checkLength(String text) {
        HashMap<String, String> returnObj = new HashMap<>();

        if (text.length() < 8 || text.length() > 12) {
            returnObj.put("error", "Password should be between 8 to 12 characters.");
            return returnObj;
        }

        returnObj.put("error", null);
        return returnObj;
    }

    private static HashMap<String, String> checkCharTypeCombination(String text) {
        HashMap<String, String> returnObj = new HashMap<>();

        // require entire string is matched, not based on substring
        if (!text.matches(".*[A-Z].*")) {
            returnObj.put("error", "Password should has at least one uppercase letter.");
        }
        else if (!text.matches(".*\\d.*")) {
            returnObj.put("error", "Password should has at least one number.");
        }
        else if (!text.matches(".*[a-z].*")) {
            returnObj.put("error", "Password should has at least one lowercase letter.");
        }
        else {
            returnObj.put("error", null);
        }

        return returnObj;
    }

    public static String validate(String text) {
        HashMap<String, String>  result;

        result = checkLength(text);
        if (result.get("error") != null) return result.get("error");

        result = checkCharTypeCombination(text);
        if (result.get("error") != null) return result.get("error");

        return null;
    }

    public static String hash(String text)  {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedByte = md.digest(text.getBytes(StandardCharsets.UTF_8));

            BigInteger number = new BigInteger(1, hashedByte);
            StringBuilder hexString = new StringBuilder(number.toString(16));

            while (hexString.length() < 64) hexString.insert(0, '0');
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
