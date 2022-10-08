package com.john.utils;

import java.io.IOException;

import com.saltweaver.salting.api.SaltingStrategy;

/**
 * A high-level decorator for other <code>FileReader</code> implementations
 * that assumes the read contents are salted using a given <code>SaltingStrategy</code>. 
 * After reading the contents of the file, the salt will be removed and the unsalted contents
 * returned. Just like other <code>FileReader</code> decorators, all Strings are
 * assumed to be UTF-8 encoded.
 */
public class SaltedFileReader implements FileReader {
	private final FileReader fileReader;
	private final SaltingStrategy saltingStrategy;
	
	public SaltedFileReader(FileReader fileReader, SaltingStrategy saltingStrategy) {
		this.fileReader = fileReader;
		this.saltingStrategy = saltingStrategy;
	}

	@Override
	public byte[] readFile() throws IOException {
		return saltingStrategy.removeSalt(fileReader.readFile());
	}

}
