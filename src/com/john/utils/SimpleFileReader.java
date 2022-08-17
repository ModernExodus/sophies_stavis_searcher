package com.john.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/** 
 * A utility for reading basic text content from files using a custom buffer size. The class takes care of
 * opening and closing the input streams, so no need to worry about memory leakage. If no buffer size
 * is provided, it will default to 50 bytes.
 */
public class SimpleFileReader implements FileReader {
	private static final Logger log = Logger.getLogger(SimpleFileReader.class.getCanonicalName());
	private static final int DEFAULT_BUFFER_SIZE = 50;
	
	private final int bufferSize;
	
	public SimpleFileReader() {
		bufferSize = DEFAULT_BUFFER_SIZE;
		logInitialization(bufferSize);
	}
	
	public SimpleFileReader(int bufferSize) {
		this.bufferSize = bufferSize;
		logInitialization(bufferSize);
	}
	
	/** 
	 * Reads basic text content from a file given a path to the file and returns the content read
	 * as a String. The operation of reading a file is blocking, and is not guaranteed to be thread-safe.
	 */
	public String readFile(String path) throws IOException {
		log.fine(String.format("Attempting to read file at %s", path));
		try (FileInputStream fis = new FileInputStream(new File(path))) {
			byte[] buffer = new byte[bufferSize];
			int numBytesRead = 0;
			StringBuilder sb = new StringBuilder();
			while ((numBytesRead = fis.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, numBytesRead, UTF_8));
			}
			log.fine(String.format("Successfully read file at %s", path));
			return sb.toString();
		} catch (IOException e) {
			log.severe("Failed to read file due to exception: " + e.getMessage());
			throw e;
		}
	}
	
	private void logInitialization(int bufferSize) {
		log.fine(String.format("Simple File Reader initialized with buffer size of %d bytes", bufferSize));
	}
}
