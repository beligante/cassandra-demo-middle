package almeida.rochapaulo.demo.data.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.base.Objects;

/**
 * 
 * @author rochapaulo
 *
 */
public class PasswordHash {

    private static PasswordHash instance;

    public static PasswordHash instance() {
        if (instance == null) {
            instance = new PasswordHash();
        }
        return instance;
    }

    private PasswordHash() {
        super();
    }

    public String create(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(source.getBytes("UTF-8"));
            byte[] digest = md.digest();

            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean validate(String raw, String hashed) {
        return Objects.equal(create(raw), hashed);
    }

}
