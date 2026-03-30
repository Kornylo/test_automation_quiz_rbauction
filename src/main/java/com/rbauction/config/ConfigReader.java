package com.rbauction.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final ConfigReader INSTANCE = new ConfigReader();
    private final Properties properties = new Properties();

    private ConfigReader() {
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load config.properties", e);
        }
    }

    public static ConfigReader getInstance() {
        return INSTANCE;
    }

    private String get(String systemKey, String fileKey) {
        String fromSystem = System.getProperty(systemKey);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        String sameKeySystem = System.getProperty(fileKey);
        if (sameKeySystem != null && !sameKeySystem.isBlank()) {
            return sameKeySystem;
        }
        String fromFile = properties.getProperty(fileKey);
        if (fromFile == null || fromFile.isBlank()) {
            throw new RuntimeException("Missing required config key: " + fileKey);
        }
        return fromFile;
    }

    public String getBaseUrl() {
        String env = System.getProperty("env");
        if (env != null && !env.isBlank()) {
            String envKey = "env." + env.toLowerCase();
            String envUrl = properties.getProperty(envKey);
            if (envUrl == null || envUrl.isBlank()) {
                throw new RuntimeException("Unknown environment: " + env
                        + ". Supported: PROD, STG, DEV (or add env." + env.toLowerCase() + " to config.properties)");
            }
            return envUrl;
        }
        return get("baseUrl", "base.url");
    }

    public String getBrowser() {
        return get("browser", "browser");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(get("headless", "headless"));
    }

    public long getPageLoadTimeout() {
        return Long.parseLong(get("timeout.page.load", "timeout.page.load"));
    }

    public long getExplicitWaitTimeout() {
        return Long.parseLong(get("timeout.explicit.wait", "timeout.explicit.wait"));
    }

    public long getDialogWaitTimeout() {
        return Long.parseLong(get("timeout.dialog.wait", "timeout.dialog.wait"));
    }

    public String getRemoteUrl() {
        return getOptional("remoteUrl", "remote.url");
    }

    public boolean isRemote() {
        String url = getRemoteUrl();
        return url != null && !url.isBlank();
    }

    public String getBrowserVersion() {
        return getOptional("browser.version", "browser.version");
    }

    public boolean isSelenoidVnc() {
        return Boolean.parseBoolean(getOrDefault("selenoid.enable.vnc", "true"));
    }

    public boolean isSelenoidVideo() {
        return Boolean.parseBoolean(getOrDefault("selenoid.enable.video", "false"));
    }

    private String getOptional(String systemKey, String fileKey) {
        String fromSystem = System.getProperty(systemKey);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        String fromFile = properties.getProperty(fileKey);
        if (fromFile == null || fromFile.isBlank()) {
            return null;
        }
        return fromFile;
    }

    private String getOrDefault(String fileKey, String defaultValue) {
        String fromSystem = System.getProperty(fileKey);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        String fromFile = properties.getProperty(fileKey);
        if (fromFile == null || fromFile.isBlank()) {
            return defaultValue;
        }
        return fromFile;
    }
}
