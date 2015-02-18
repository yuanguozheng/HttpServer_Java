package server;

import model.Headers;
import model.Request;
import model.ResponseBody;
import model.ResponseHeader;
import model.Status;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

public class HttpServer {

	public static final int SERVER_TIMEOUT = 5000;

	public static final String MIME_PLAINTEXT = "text/plain";

	public static final String MIME_HTML = "text/html";

	public static final String MIME_DEFAULT_BINARY = "application/octet-stream";

	public static String sDefaultDoc = "index.html";

	private File mHomePath;
	private int mPort;
	private ServerSocket mSocket;
	private Thread mThread;

	private static final HashMap<String, String> MIME_TYPES = new HashMap<String, String>() {

		private static final long serialVersionUID = 1L;

		{
			put("css", "text/css");
			put("htm", "text/html");
			put("html", "text/html");
			put("xml", "text/xml");
			put("java", "text/x-java-source, text/java");
			put("md", "text/plain");
			put("txt", "text/plain");
			put("asc", "text/plain");
			put("gif", "image/gif");
			put("jpg", "image/jpeg");
			put("jpeg", "image/jpeg");
			put("png", "image/png");
			put("mp3", "audio/mpeg");
			put("m3u", "audio/mpeg-url");
			put("mp4", "video/mp4");
			put("ogv", "video/ogg");
			put("flv", "video/x-flv");
			put("mov", "video/quicktime");
			put("swf", "application/x-shockwave-flash");
			put("js", "application/javascript");
			put("pdf", "application/pdf");
			put("doc", "application/msword");
			put("ogg", "application/x-ogg");
			put("zip", "application/octet-stream");
			put("exe", "application/octet-stream");
			put("class", "application/octet-stream");
		}
	};

	public HttpServer(int port, File homePath) {
		mPort = port;
		mHomePath = homePath;
	}

	public HttpServer(int port, File homePath, String defaultDoc) {
		mPort = port;
		mHomePath = homePath;
		sDefaultDoc = defaultDoc;
	}

	public void start() {
		try {
			mSocket = new ServerSocket(mPort);
			System.out
					.println(String.format("Server is listening %s. ", mPort));
		} catch (IOException e) {
			System.out.println("The port is using, please try other port!");
			return;
		}
		if (!isHomePathAvaliable()) {
			return;
		}
		mThread = new Thread(new Runnable() {
			public void run() {
				while (!mSocket.isClosed()) {
					try {
						Socket socket = mSocket.accept();
						InputStream inputStream = socket.getInputStream();
						OutputStream outputStream = socket.getOutputStream();
						PrintWriter responseWriter = new PrintWriter(
								new BufferedWriter(new OutputStreamWriter(
										outputStream)), true);
						Request request = getHeader(inputStream);
						File resource = null;
						if (request.getHeaders().getQueryString().equals("/")) {
							resource = new File(mHomePath, sDefaultDoc);
						} else {
							resource = new File(mHomePath, request.getHeaders()
									.getQueryString());
						}
						ResponseHeader responseHeader = new ResponseHeader();
						responseHeader.setDate(new Date());
						if (!resource.exists()) {
							serveNotFound(responseWriter, responseHeader);
						} else {
							serveResouce(outputStream, resource, responseHeader);
						}
						responseWriter.flush();
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		mThread.start();
	}

	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	private boolean isHomePathAvaliable() {
		if (!mHomePath.exists()) {
			System.out.println("Home path is not existed!");
			return false;
		}
		if (!mHomePath.isDirectory()) {
			System.out.println("Home path is not correct!");
			return false;
		}
		if (!mHomePath.canRead()) {
			System.out.println("Cannot read home path!");
			return false;
		}
		return true;
	}

	private void serveResouce(OutputStream outputStream, File resource,
			ResponseHeader responseHeader) throws FileNotFoundException,
			IOException {
		responseHeader.setStatus(Status.OK);
		responseHeader.setContentLength(resource.length());
		String ext = getFileExtension(resource);
		String mime = MIME_TYPES.get(ext);
		responseHeader.setContentType(mime);
		FileInputStream fis = new FileInputStream(resource);
		outputStream.write(responseHeader.toString().getBytes());
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len = fis.read(buf)) != -1) {
			outputStream.write(buf, 0, len);
		}
		fis.close();
	}

	private void serveNotFound(PrintWriter responseWriter,
			ResponseHeader responseHeader) {
		responseHeader.setStatus(Status.NOT_FOUND);
		responseHeader.setContentLength(ResponseBody.NOT_FOUND.length());
		responseHeader.setContentType(MIME_HTML);
		responseWriter.write(responseHeader.toString());
		responseWriter.write(ResponseBody.NOT_FOUND);
	}

	private Request getHeader(InputStream inputStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		Request request = new Request();
		Headers headers = new Headers();
		StringBuilder postLineBuffer = new StringBuilder();
		try {
			String requestMsg = getRequestMsg(reader, postLineBuffer);
			String[] info = requestMsg.split("\r\n");
			processHashMap(headers, info);
			request.setHeaders(headers);
			int bodyIndex = requestMsg.indexOf("\r\n\r\n") + 4;
			String body = requestMsg.substring(bodyIndex).trim();
			request.setBody(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return request;
	}

	private void processHashMap(Headers headers, String[] info) {
		for (int i = 0; i < info.length; i++) {
			String item = info[i];
			if (item.equals("")) {
				break;
			}
			if (i == 0) {
				String[] baseInfo = item.split(" ");
				headers.setMethod(baseInfo[0]);
				headers.setQueryString(baseInfo[1]);
				headers.setVersion(baseInfo[2]);
				System.out.println(item);
			} else {
				String[] kvInfo = item.split(": ");
				headers.addHeader(kvInfo[0], kvInfo[1]);
			}
		}
	}

	private String getRequestMsg(BufferedReader reader,
			StringBuilder postLineBuffer) throws IOException {
		char buf[] = new char[256];
		while (true) {
			int len = reader.read(buf);
			postLineBuffer.append(buf, 0, len);
			if (!reader.ready()) {
				break;
			}
		}
		String requestMsg = postLineBuffer.toString();
		return requestMsg;
	}
}
