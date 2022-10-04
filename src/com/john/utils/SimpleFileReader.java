package com.john.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/** 
 * A utility for reading basic text content from files using a custom buffer size. The class takes care of
 * opening and closing the input streams, so no need to worry about memory leakage. If no buffer size
 * is provided, it will default to 50 bytes.
 */
public class SimpleFileReader implements FileReader {
	private static final Logger log = Logger.getLogger(SimpleFileReader.class.getCanonicalName());
	private static final int DEFAULT_BUFFER_SIZE = 50;
	
	private final String path;
	private final int bufferSize;
	
	public SimpleFileReader(String path) {
		this(path, DEFAULT_BUFFER_SIZE);
	}
	
	public SimpleFileReader(String path, int bufferSize) {
		this.path = path;
		this.bufferSize = bufferSize;
		log.fine(String.format("Simple File Reader initialized with buffer size of %d bytes", bufferSize));
	}
	
	/** 
	 * Reads basic text content from a file given a path to the file and returns the content read
	 * as a String. The operation of reading a file is blocking, and is not guaranteed to be thread-safe.
	 */
	public byte[] readFile() throws IOException {
		log.fine(String.format("Attempting to read file at %s", path));
		try (FileInputStream fis = new FileInputStream(new File(path))) {
			List<Byte> result = new ArrayList<>();
			byte[] buffer = new byte[bufferSize];
			int read = 0;
			while ((read = fis.read(buffer)) != -1) {
				for (int i = 0; i < read; i++) {
					result.add(buffer[i]);
				}
			}
			log.fine(String.format("Successfully read file at %s", path));
			byte[] unboxedResult = new byte[result.size()];
			for (int i = 0; i < result.size(); i++) {
				unboxedResult[i] = result.get(i);
			}
			return unboxedResult;
		} catch (IOException e) {
			log.severe("Failed to read file due to exception: " + e.getMessage());
			throw e;
		}
	}
}
