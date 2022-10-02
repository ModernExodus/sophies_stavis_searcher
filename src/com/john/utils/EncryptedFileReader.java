package com.john.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/** 
 * A high-level decorator for other <code>FileReader</code> implementations that decrypts file contents
 * using the AES/CBC/PKCS5Padding algorithm. It will use the wrapped <code>FileReader</code> to get the
 * contents of a file, and then decrypts those contents using the provided Base64-encoded private key and
 * initialization vector. It assumes all Strings are UTF-8 encoded and will return the String as such.
 * A new instance should be created for each file to read, as this will nullify the private key and
 * initialization vector after invocation of the <code>readFile</code> method.
 */
public class EncryptedFileReader implements FileReader {
	private static final Logger log = Logger.getLogger(EncryptedFileReader.class.getCanonicalName());
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	
	private final FileReader fileReader;
	private Key pk;
	private IvParameterSpec iv;
	
	public EncryptedFileReader(FileReader fileReader, byte[] pk, byte[] iv) {
		this.fileReader = fileReader;
		this.pk = new SecretKeySpec(Base64.getDecoder().decode(pk), "AES");
		this.iv = new IvParameterSpec(iv);
	}

	@Override
	public String readFile() throws IOException {
		if (pk == null || iv == null) {
			throw new IOException(
					"No available private key or initialization vector. Are you perhaps trying to invoke this method more than once on the same instance?");
		}
		try {			
			byte[] decrypted = decrypt(fileReader.readFile().getBytes(UTF_8));
			return new String(decrypted, UTF_8);
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new IOException(e);
		} finally {
			pk = null;
			iv = null;
		}
	}
	
	private byte[] decrypt(byte[] encrypted) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, pk, iv);
		return cipher.doFinal(encrypted);
	}

}
