package blockchain;

import java.io.Serializable;
import java.security.MessageDigest;

public class Hash implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String hash;

    public Hash(String text) {
        this.hash = hash(text);
    }

    private String hash(String input) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(input.getBytes("UTF-8"));
            var hexString = new StringBuilder();
            for (var elem : hash) {
                var hex = Integer.toHexString(0xff & elem);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validate(int proofLength, String input) {
        var proof = hash.substring(0, proofLength).replaceAll("0", "");
        if (!"".equals(proof)) {
            return false;
        }
        return hash.equals(hash(input));
    }

    @Override
    public String toString() {
        return hash;
    }
}