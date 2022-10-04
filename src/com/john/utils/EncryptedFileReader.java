package com.john.utils;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/** 
 * A high-level decorator for other <code>FileReader</code> implementations that decrypts file contents
 * using the AES/ECB/PKCS5Padding algorithm. It will use the wrapped <code>FileReader</code> to get the
 * contents of a file, and then decrypts those contents using the provided Base64-encoded private key.
 * It assumes all Strings are UTF-8 encoded and will return the String as such.
 * A new instance should be created for each file to read, as this will nullify the private key 
 * after the invocation of the <code>readFile</code> method.
 */
public class EncryptedFileReader implements FileReader {
	private static final Logger log = Logger.getLogger(EncryptedFileReader.class.getCanonicalName());
	private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
	
	private final FileReader fileReader;
	private Key pk;
	
	public EncryptedFileReader(FileReader fileReader, byte[] pk) {
		this.fileReader = fileReader;
		this.pk = new SecretKeySpec(Base64.getDecoder().decode(pk), "AES");
	}

	@Override
	public byte[] readFile() throws IOException {
		if (pk == null) {
			throw new IOException(
					"No available private key. Are you perhaps trying to invoke this method more than once on the same instance?");
		}
		try {
			return decrypt(fileReader.readFile());
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new IOException(e);
		} finally {
			pk = null;
		}
	}
	
	private byte[] decrypt(byte[] encrypted) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, pk);
		return cipher.doFinal(encrypted);
	}

}
