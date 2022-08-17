package com.john.utils;

import java.io.IOException;

public class EncryptedFileReader implements FileReader {

	@Override
	public String readFile(String path) throws IOException {
		throw new UnsupportedOperationException("Reading encrypted files is not yet supported");
	}

}
