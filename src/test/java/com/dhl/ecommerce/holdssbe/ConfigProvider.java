package com.dhl.ecommerce.holdssbe;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Locale;

public class ConfigProvider {
    private final Dotenv dotenv;
    public ConfigProvider() {
        dotenv = Dotenv.load();
    }

    public String getProperty(final String name) {
        String value = System.getProperty(name);
        if (value == null) {
            value = dotenv.get(name, System.getenv(name));
        }
        return value;
    }

    public Integer getIntegerProperty(final String name) {
        return Integer.parseInt(getProperty(name));
    }

    public String getFileDirectory(final String directoryType) {
        final String propertyKey = directoryType.toLowerCase(Locale.ROOT).contains("download") ?
                Constants.FILE_DOWNLOAD_PATH :
                directoryType.toLowerCase(Locale.ROOT).contains("upload") ?
                        Constants.FILE_UPLOAD_PATH : null;

        return propertyKey != null ? System.getProperty("user.dir") + getProperty(propertyKey) : "";
    }

    public String getFileName(final String fileExtension) {
        return getProperty(Constants.DOWNLOAD_FILE_NAME).replace("'", "") + "." + fileExtension;
    }




}