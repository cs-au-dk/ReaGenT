package dk.webbies.tajscheck.util.selenium;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverCommandExecutor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;


/**
 * Created by Erik Krogh Kristensen on 10-11-2015.
 */
public class SeleniumDriver {
    private static String getEmptyPageUrl(String scriptPath, int port) {
        try {
            scriptPath = URLEncoder.encode(scriptPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        String workingDir = System.getProperty("user.dir");
        if (isWindows) {
            return "file:///" + workingDir + "\\lib\\selenium\\driver.html?script=" + scriptPath + "&port=" + port;
        } else {
            return "file:///" + workingDir + "/lib/selenium/driver.html?script=" + scriptPath + "&port=" + port;
        }
    }

    // TODO: Create a small HTTP server, use that to serve the script, also use it to serve files from the benchmark folder.
    public static String executeScript(String script, int timeout) throws IOException, HttpException {
        setDriverPath();

        ChromeDriver driver = new ChromeDriver(buldCapabilities());

        File scriptFile = null;
        String tmpFileSuffix = "tmpFileSeleniumDriverThing.js";
        try {
            scriptFile = File.createTempFile("script-", tmpFileSuffix);
            FileWriter out = new FileWriter(scriptFile);
            IOUtils.write(script.getBytes(), out);
            out.close();
        } catch (IOException e) {
            if (scriptFile != null && scriptFile.exists()) {
                scriptFile.delete();
            }
            throw new RuntimeException(e);
        }

        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();

//        System.out.println("Listening for result at port: " + port);

        driver.get(getEmptyPageUrl(scriptFile.getAbsolutePath(), port));

        if (timeout > 0) {
            socket.setSoTimeout(timeout);
        }

        StringBuilder message = new StringBuilder();
        try {
            while(true) {
                String partMessage = getResponse(socket);
                if (partMessage.equals("close")) {
                    driver.close();
                    break;
                }

                message.append(partMessage).append("\n");
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Had a timeout, continuing");
        } catch (ConnectionClosedException e) {
            System.err.println("Socket closed (did you close the window?), continuing");
        }

        socket.close();

        driver.quit();

        scriptFile.delete();

//        System.out.println("Message recieved, length: " + message.length());

        return message.toString();
    }

    @SuppressWarnings("deprecation")
    private static String getResponse(ServerSocket serverSocket) throws IOException, HttpException {
        try (Socket socket = serverSocket.accept()) {
            Date today = new Date();
            String httpResponse = "" +
                    "HTTP/1.0 200 OK\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "\r\n" + today;

            String input = readHttpRequest(socket.getInputStream());

            socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));

            return input;
        }
    }

    private static String readHttpRequest(InputStream in) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        int contentLength = -1;

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

        return builder.toString();
    }


    private static void setDriverPath() {
        String operatingSystem = System.getProperty("os.name");
        if (operatingSystem.contains("Windows")) {
            System.setProperty("webdriver.chrome.driver", "./lib/selenium/chromedriver.exe");
        } else if (operatingSystem.contains("Linux")) {
            System.setProperty("webdriver.chrome.driver", "./lib/selenium/chromedriverLinux64");
        } else if (operatingSystem.contains("Mac")) {
            System.setProperty("webdriver.chrome.driver", "./lib/selenium/chromedriverMac");
        } else {
            throw new RuntimeException("Unknown operating system: " + operatingSystem);
        }
    }

    private static DesiredCapabilities buldCapabilities() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("window-size=400,400");

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, Level.ALL);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        return capabilities;
    }
}
