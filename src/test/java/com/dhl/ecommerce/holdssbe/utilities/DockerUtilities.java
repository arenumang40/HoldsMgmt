package com.dhl.ecommerce.holdssbe.utilities;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import java.io.IOException;

@Slf4j
public class DockerUtilities {

    protected WebDriver driver;
    protected ConfigProvider configProvider;

    public DockerUtilities(final WebDriver driver, final ConfigProvider configProvider) {
        this.driver = driver;
        this.configProvider =  configProvider;
    }

    // Tpo delete all the files present at Docker download path
    public void deleteAllFilesAtDocker() {
        final String sourcePath = configProvider.getProperty(Constants.SELENIUM_DOWNLOAD_PATH);
        final String seleniumContainer = configProvider.getProperty(Constants.SELENIUM_CONTAINER_ID);

        final String cmdToDeleteFiles = "wsl docker exec " + seleniumContainer + " sh -c 'rm -rf " + sourcePath + "*'";

        log.debug("Trying to delete the files from docker by using command '{}'.",cmdToDeleteFiles);
        performCommandAction(cmdToDeleteFiles);
    }

    // To perform Terminal or Docker Command Actions
    public void performCommandAction(final String command) {
        try {
            final Process process = Runtime.getRuntime().exec(command);
            final int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Command '{}' run successfully.",command);
            } else {
                log.error("Failed to execute Command '{}' , Getting Exit Code : '{}'.",command,exitCode);
            }
        } catch (InterruptedException e) {
            log.error("Interrupted command execution {}", e.getMessage());
        } catch (IOException e) {
            log.error("Error while executing command '{}' : {}", command, e.getMessage());
        }
    }

}
