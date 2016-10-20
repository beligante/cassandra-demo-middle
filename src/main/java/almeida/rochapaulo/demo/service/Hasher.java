package almeida.rochapaulo.demo.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {

	private static Hasher instance;

	public static Hasher instance() {
		if (instance == null) {
			instance = new Hasher();
		}
		return instance;
	}

	private Hasher() {
		super();
	}

	public String hash(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(source.getBytes("UTF-8"));
			byte[] digest = md.digest();

			return String.format("%064x", new java.math.BigInteger(1, digest));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

}
