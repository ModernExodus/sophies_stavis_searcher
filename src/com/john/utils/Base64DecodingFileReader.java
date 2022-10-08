package com.john.utils;

import java.io.IOException;
import java.util.Base64;

public class Base64DecodingFileReader implements FileReader {
	private final FileReader fileReader;

	public Base64DecodingFileReader(FileReader fileReader) {
		this.fileReader = fileReader;
	}
	
	@Override
	public byte[] readFile() throws IOException {
		return Base64.getDecoder().decode(fileReader.readFile());
	}

}
