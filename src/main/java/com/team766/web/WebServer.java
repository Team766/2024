package com.team766.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/*
 * Creates an HTTP Server on the robot that can change the 
 * settings of it.  It is initially just used to change robot
 * values.  Hopefully this will be later updated to support 
 * graphing and even small changes in the robot's actual code.
 * 
 * Can be reached at:
 * 	roboRio-766.local:5800/values
 */

public class WebServer {
	
	public interface Handler {
		String endpoint();
		default boolean showInMenu() { return true; }
		String title();
		String handle(Map<String, Object> params);
	}

	private HttpServer server;
	private LinkedHashMap<String, Handler> pages = new LinkedHashMap<String, Handler>();

	public WebServer() {
		addHandler(new Handler() {
			@Override
			public String endpoint() {
				return "/";
			}

			@Override
			public String title() {
				return "Menu";
			}

			@Override
			public boolean showInMenu() {
				return false;
			}

			@Override
			public String handle(Map<String, Object> params) {
				return "";
			}
		});
	}
	
	public void addHandler(Handler handler) {
		pages.put(handler.endpoint(), handler);
	}

	public void start(){
		try {
			server = HttpServer.create(new InetSocketAddress(5800), 0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (Map.Entry<String, Handler> page : pages.entrySet()) {
			HttpHandler httpHandler = new HttpHandler() {
				@Override
				public void handle(HttpExchange exchange) throws IOException {
					Map<String, Object> params = parseParams(exchange);
					String response = "<!DOCTYPE html><html><body>";
					response += buildPageHeader();
					try {
						response += page.getValue().handle(params);
					} catch (Exception ex) {
						ex.printStackTrace();
						throw ex;
					}
					response += "</body></html>";
					exchange.sendResponseHeaders(200, response.getBytes().length);
					try (OutputStream os = exchange.getResponseBody()) {
						os.write(response.getBytes());
					}
				}
			};
			server.createContext(page.getKey(), httpHandler);
		}
		addStaticFilesHandler();
		server.start();
	}
	
	protected String buildPageHeader() {
		String result = "";
		result += "<style>\n";
		result += "textarea {\n";
		result += "  background: url(/static/line_numbers.svg);\n";
		result += "  background-attachment: local;\n";
		result += "  background-repeat: no-repeat;\n";
		result += "  padding-left: 45px;\n";
		result += "  border-color:#ccc;\n";
		result += "  line-height: 1.15em;\n";
		result += "  font-size: 14px;\n";
		result += "  font-family: monospace;\n";
		result += "}\n";
		result += "</style>\n";
		result += "<p>\n";
		for (Map.Entry<String, Handler> page : pages.entrySet()) {
			if (page.getValue().showInMenu()) 
			result += String.format("<a href=\"%s\">%s</a><br>\n", page.getKey(), page.getValue().title());
		}
		result += "</p>\n";
		return result;
	}
	
	private void addStaticFilesHandler() {
		final String endpoint = "/static";
		final String resourceFilePrefix = "static";
		HttpHandler httpHandler = new HttpHandler() {
			final Map<String, String> mimeTypes = Map.of(
				".html", "text/html",
				".js", "text/javascript",
				".svg", "image/svg+xml"
			);
			private String getFileExtension(String fileName) {
				int lastIndexOf = fileName.lastIndexOf(".");
				if (lastIndexOf == -1) {
					return ""; // empty extension
				}
				return fileName.substring(lastIndexOf);
			}

			@Override
			public void handle(HttpExchange exchange) throws IOException {
				try {
				String path = exchange.getRequestURI().getPath();
				if (!path.startsWith(endpoint)) {
					throw new IllegalArgumentException("Unexpected path prefix: " + path + "; expected: " + endpoint);
				}
				path = resourceFilePrefix + path.substring(endpoint.length());
				final var fileExtension = getFileExtension(path);

				var resource = getClass().getResource(path);
				if (resource == null) {
					final byte[] errorMessage = ("Resource not found: " + path).getBytes();
					exchange.sendResponseHeaders(404, errorMessage.length);
					try (OutputStream os = exchange.getResponseBody()) {
						os.write(errorMessage);
					}
					return;
				}
				var connection = resource.openConnection();
				try (var is = connection.getInputStream()) {
					final String mimeType = mimeTypes.containsKey(fileExtension) ?
						mimeTypes.get(fileExtension) :
						connection.getContentType();
					exchange.getResponseHeaders().set("Content-Type", mimeType);
					exchange.sendResponseHeaders(200, connection.getContentLengthLong());
					try (OutputStream os = exchange.getResponseBody()) {
						is.transferTo(os);
					}
				}
				
				} catch (Exception ex) {
					ex.printStackTrace();
					throw ex;
				}
			}
		};
		server.createContext(endpoint, httpHandler);
	}
	
	public Map<String, Object> parseParams(HttpExchange exchange) throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parseGetParameters(exchange, parameters);
		parsePostParameters(exchange, parameters);
		return parameters;
	}

	private void parseGetParameters(HttpExchange exchange, Map<String, Object> parameters)
		throws UnsupportedEncodingException {
		URI requestedUri = exchange.getRequestURI();
		String query = requestedUri.getRawQuery();
		parseQuery(query, parameters);
	}

	private void parsePostParameters(HttpExchange exchange, Map<String, Object> parameters)
		throws IOException {

		if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
			InputStreamReader isr =
				new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			parseQuery(query, parameters);
		}
	}

	private void parseQuery(String query, Map<String, Object> parameters)
		throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if(obj instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<String> values = (List<String>)obj;
						
						values.add(value);
					} else if(obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String)obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}
