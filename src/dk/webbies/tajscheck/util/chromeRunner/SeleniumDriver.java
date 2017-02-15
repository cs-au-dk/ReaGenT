package dk.webbies.tajscheck.util.chromeRunner;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


/**
 * Created by Erik Krogh Kristensen on 10-11-2015.
 */
public class SeleniumDriver {
    // TODO: Create a small HTTP server, use that to serve the script, also use it to serve files from the benchmark folder.
    public static String executeScript(File dir, String script, int timeout) throws IOException, HttpException {
        setDriverPath();

        ChromeDriver driver = new ChromeDriver(buldCapabilities());

        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();

//        System.out.println("Listening for result at port: " + port);

        if (timeout > 0) {
            socket.setSoTimeout(timeout);
        }

        SimpleMessageRecievingHTTPServer server = startServer(dir, script, socket);

        driver.get("http://127.0.0.1:" + port);

        String message = String.join("\n", server.getMessages());

        driver.quit();

        return message;
    }

    private static SimpleMessageRecievingHTTPServer startServer(File dir, String script, ServerSocket socket) {
        Map<String, String> customContents = new HashMap<>();
        customContents.put("test.js", script);
        try {
            String htmlDriver = IOUtils.toString(SeleniumDriver.class.getResourceAsStream("driver.html"));
            customContents.put("/", htmlDriver);
            customContents.put("/index.html", htmlDriver);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SimpleMessageRecievingHTTPServer server = new SimpleMessageRecievingHTTPServer(dir, customContents, socket);

        new Thread(server::start).start();

        return server;
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
