package night.app.services;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Password {
    private static String checkLength(String text) {
        if (text.length() < 12) return "Password should be at least 12 characters.";
        if (text.length() > 20) return "Password should be at most 20 characters.";

        return "true";
    }

    private static String checkCharTypeCombination(String text) {
        // require entire string is matched, not based on substring
        if (!text.matches(".*[A-Z].*"))
            return "Password should has at least one uppercase letter.";
        if (!text.matches(".*\\d.*"))
            return "Password should has at least one number.";
        if (!text.matches(".*[#?!@$%^&*-].*"))
            return "Password should has at least one symbol.";
        if (!text.matches(".*[a-z].*"))
            return "Password should has at least one lowercase letter.";
        return "true";
    }

    public static String validate(String text) {
        String isPass = checkLength(text);
        if (!isPass.equals("true")) return isPass;

        isPass = checkCharTypeCombination(text);
        if (!isPass.equals("true")) return isPass;

        return "Pass";
    }

    public static String hash(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedByte = md.digest(text.getBytes(StandardCharsets.UTF_8));

        BigInteger number = new BigInteger(1, hashedByte);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 64) hexString.insert(0, '0');
        return hexString.toString();
    }
}
