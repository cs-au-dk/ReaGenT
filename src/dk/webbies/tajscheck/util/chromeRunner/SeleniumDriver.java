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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


/**
 * Created by Erik Krogh Kristensen on 10-11-2015.
 */
public class SeleniumDriver {
    public static String executeScript(File dir, String script, int timeout) throws IOException, HttpException {
        return executeScript(dir, script, timeout, 10 * 1000);
    }
    private static String executeScript(File dir, String script, int timeout, int pageLoadTimeout) throws IOException, HttpException {
        setDriverPath();

        ChromeDriver driver = new ChromeDriver(buldCapabilities());

        driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(pageLoadTimeout, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(pageLoadTimeout, TimeUnit.SECONDS);


        ServerSocket socket = new ServerSocket(0);

        int port = socket.getLocalPort();

        if (timeout > 0) {
            socket.setSoTimeout(timeout);
        }

        SimpleMessageReceivingHTTPServer server = startServer(dir, script, socket);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                if (!socket.isBound() || socket.isClosed()) {
                    System.out.println();
                }
                driver.get("http://127.0.0.1:" + port);
                break;
            } catch (org.openqa.selenium.TimeoutException e) {
                System.err.println("Selenium driver had a timeout loading the index page, trying again!");
                try {
                    driver.quit();
                    socket.close();
                } catch (Exception ignored) { }
                return executeScript(dir, script, timeout, pageLoadTimeout + 10); // continue, try again
            }
        }

        String message = String.join("\n", server.getMessages());

        driver.quit();

        return message;
    }

    private static SimpleMessageReceivingHTTPServer startServer(File dir, String script, ServerSocket socket) {
        Map<String, String> customContents = new HashMap<>();
        customContents.put("test.js", script);
        try {
            String htmlDriver = IOUtils.toString(SeleniumDriver.class.getResourceAsStream("driver.html"));
            customContents.put("/", htmlDriver);
            customContents.put("/index.html", htmlDriver);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SimpleMessageReceivingHTTPServer server = new SimpleMessageReceivingHTTPServer(dir, customContents, socket);

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
