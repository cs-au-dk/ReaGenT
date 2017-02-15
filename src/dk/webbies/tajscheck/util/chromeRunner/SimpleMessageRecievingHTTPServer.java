package dk.webbies.tajscheck.util.chromeRunner;

import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import org.apache.http.HttpException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by erik1 on 15-02-2017.
 */
public class SimpleMessageRecievingHTTPServer {
    private final File dir;
    private final Map<String, String> customContents;
    private final ServerSocket serverSocket;
    private List<String> messages = new ArrayList<>();

    SimpleMessageRecievingHTTPServer(File dir, Map<String, String> customContents, ServerSocket serverSocket) {
        this.dir = dir;
        this.customContents = customContents;
        this.serverSocket = serverSocket;
    }

    private boolean readMessage() throws IOException, HttpException {
        try (Socket socket = serverSocket.accept()) {
            Pair<String, String> request = readHttpRequest(socket.getInputStream());

            if (request.getRight() != null) {
                Date today = new Date();
                String httpResponse = "" +
                        "HTTP/1.0 200 OK\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +
                        "\r\n" + today;
                socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));

                if (request.getRight().equals("close")) {
                    return true;
                } else {
                    messages.add(request.getRight());
                    return false;
                }
            }

            String content = getContentFromPath(request.getLeft());
            if (content == null) {
                errorReport(new PrintStream(socket.getOutputStream()), socket, "404", "Not Found",
                        "The requested URL was not found " +
                                "on this server.");
                return false;
            }

            String contentType = URLConnection.guessContentTypeFromName(request.getLeft());

            StringBuilder response = new StringBuilder();

            response.append("HTTP/1.0 200 OK\r\n");
            if (contentType != null) {
                response.append("Content-Type: ").append(contentType).append("\r\n");
            }
            response.append("Date: ").append(new Date()).append("\r\n")
                    .append("Server: IXWT FileServer 1.0\r\n\r\n");

            socket.getOutputStream().write(response.toString().getBytes());

            socket.getOutputStream().write(content.getBytes());

            return false;
        }
    }

    private String getContentFromPath(String path) throws IOException {
        if (customContents.containsKey(path)) {
            return customContents.get(path);
        }
        if (path.startsWith("/")) {
            String subPath = path.substring(1, path.length());
            if (customContents.containsKey(subPath)) {
                return customContents.get(subPath);
            }
        }
        if (new File(dir.getPath() + path).exists()) {
            return Util.readFile(dir.getPath() + path);
        }
        return null;
    }

    private static Pair<String, String> readHttpRequest(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        int contentLength = -1;

        String firstLine = reader.readLine();

        String protocol = firstLine.split(" ")[0];
        String path = firstLine.split(" ")[1];

        if (protocol.equals("POST")) {
            while (!(line = reader.readLine()).equals("")) {
                if (line.startsWith("Content-Length: ")) {
                    String number = line.substring("Content-Length: ".length(), line.length());
                    contentLength = Integer.parseInt(number);
                }
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < contentLength; i++) {
                int read = reader.read();
                if (read == -1) {
                    throw new RuntimeException();
                }
                builder.append((char) read);
            }

            return new Pair<>(path, builder.toString());
        } else {
            assert protocol.equals("GET");
            return new Pair<>(path, null);
        }
    }

    CountDownLatch latch = new CountDownLatch(1);

    public List<String> getMessages() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this.messages;
    }

    public void start() {
        try {
            while (true) {
                try {
                    boolean shouldClose = readMessage();
                    if (shouldClose) {
                        break;
                    }
                } catch (IOException | HttpException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } finally {
            latch.countDown();
            try {
                serverSocket.close();
            } catch (IOException ignore) {
            }
        }
    }

    private void errorReport(PrintStream pout, Socket con, String code, String title, String msg) {
        pout.print(
                "HTTP/1.0 " + code + " " + title + "\r\n" +
                        "\r\n" +
                        "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n" +
                        "<HTML><HEAD><TITLE>" + code + " " + title + "</TITLE>\r\n" +
                        "</HEAD><BODY>\r\n" +
                        "<H1>" + title + "</H1>\r\n" + msg + "<P>\r\n" +
                        "<HR><ADDRESS>IXWT FileServer 1.0 at " +
                        con.getLocalAddress().getHostName() +
                        " Port " + con.getLocalPort() + "</ADDRESS>\r\n" +
                        "</BODY></HTML>\r\n");
    }
}
