package com.john.utils.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public final class HttpClientHelper {
	private static final Logger log = Logger.getLogger(HttpClientHelper.class.getCanonicalName());
	
	public static Optional<HttpResponse<String>> GET(String url, Map<String, String> queryParams, HttpHeader ...headers) {
		HttpClient client = HttpClient.newHttpClient();
		try {
			URI uri = constructURI(url, queryParams);
			HttpRequest request = constructRequest(uri, HttpMethod.GET, null, headers);
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			return Optional.of(response);
		} catch (URISyntaxException | IOException | InterruptedException e) {
			log.severe(e.getMessage());
			return Optional.empty();
		}
	}
	
	public static Optional<HttpResponse<String>> POST(String url, String body, HttpHeader ...headers) {
		HttpClient client = HttpClient.newHttpClient();
		try {
			URI uri = constructURI(url, Collections.emptyMap());
			HttpRequest request = constructRequest(uri, HttpMethod.POST, body, headers);
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			return Optional.of(response);
		} catch (URISyntaxException | IOException | InterruptedException e) {
			log.severe(e.getMessage());
			return Optional.empty();
		}
	}
	
	private static URI constructURI(String url, Map<String, String> queryParams) throws URISyntaxException {
		StringBuilder urlAccumulator = new StringBuilder(url);
		if (!queryParams.isEmpty()) {
			urlAccumulator.append('?');
			Set<Entry<String, String>> params = queryParams.entrySet();
			for (var param : params) {
				urlAccumulator.append(String.format("%s=%s&", param.getKey(), param.getValue()));
			}
			urlAccumulator.deleteCharAt(urlAccumulator.length() - 1);
		}
		return new URI(urlAccumulator.toString());
	}
	
	private static HttpRequest constructRequest(URI uri, HttpMethod method, String body, HttpHeader ...headers) {
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri);
		for (HttpHeader header : headers) {
			requestBuilder.header(header.getName(), header.getValue());
		}
		switch (method) {
		case GET:
			requestBuilder.GET();
			break;
		case POST:
			requestBuilder.POST(BodyPublishers.ofString(body == null ? "" : body));
			break;
		}
		return requestBuilder.build();
	}
	
}
