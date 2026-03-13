package com.dhl.ecommerce.holdssbe.utilities;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.testng.FileAssert.fail;


@Slf4j
public class FileUtilities {

    protected WebDriver driver;
    protected ConfigProvider configProvider;
    protected WebDriverWait wait;

    public FileUtilities(final WebDriver driver, final ConfigProvider configProvider) {
        this.driver = driver;
        this.configProvider = configProvider;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(Integer.parseInt(configProvider.getProperty(Constants.SELENIUM_IMPLICIT_WAIT))));
    }

    public List<Map<String, String>> getCSVFileContent(final String fileDirectory, final String fileName) {
        List<Map<String, String>> records = new ArrayList<>();
        final String fullName = fileDirectory + fileName;

        if(isLocalFileExist(fileDirectory,fileName)){
            records = extractRecordsFromCSV(fullName);
        }else if(isRemoteFileExist(fileName)){
            performBSDownloadOperations(fileName);
            records = extractRecordsFromCSV(fileName);
        }else {
            fail("File " + fileName + "is not exist.");
        }
        return records;
    }

    public boolean isLocalFileExist(final String downloadDirectory, final String fileName) {
        boolean isDownloaded = false;
        final File dir = new File(downloadDirectory);
        final File[] dirContents = dir.listFiles();

        if (dirContents != null) {
            for (final File file : dirContents) {
                if (file.getName().contains(fileName)) {
                    isDownloaded = true;
                    break;
                }
            }
        }
        return isDownloaded;
    }

    private boolean isRemoteFileExist(String fileName){
        final JavascriptExecutor jse = (JavascriptExecutor) driver;
        return  (boolean) jse.executeScript("browserstack_executor: {\"action\": \"fileExists\", \"arguments\": {\"fileName\": \"" + fileName + "\"}}");
    }

    private List<Map<String, String>> extractRecordsFromCSV(final String fileName){
        final List<Map<String, String>> records = new ArrayList<>();
        String line;
        final String csvSeparator = ","; // Adjust if your CSV uses a different separator

        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8)) {
            // Read the header line
            final String headerLine = bufferedReader.readLine();
            final String[] headers = headerLine.split(csvSeparator);

            // Read each subsequent line
            line = bufferedReader.readLine();
            while (line != null) {
                final String[] values = line.split(csvSeparator);
                final Map<String, String> row = new ConcurrentHashMap<>();

                // Map header to corresponding value
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i], values.length > i ? values[i] : null);
                }
                records.add(row);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            log.error("Error reading CSV file: {}", e.getMessage());
        }
        // Example: Print the content of List<Map<String, String>>
        records.forEach(row -> log.info("CSV Content : '{}'", row));
        return records;
    }

    private void performBSDownloadOperations(final String fileName){
//        System.out.println("BS Last File"+jse.executeScript("browserstack_executor: {\"action\": \"fileExists\"}"));
//        System.out.println("BS File Property"+jse.executeScript("browserstack_executor: {\"action\": \"getFileProperties\", \"arguments\": {\"fileName\": \""+fileName+"\"}}"));
        final JavascriptExecutor jse = (JavascriptExecutor) driver;
        final String base64EncodedFile = (String) jse.executeScript("browserstack_executor: {\"action\": \"getFileContent\", \"arguments\": {\"fileName\": \""+fileName+"\"}}");
        final byte[] data = Base64.getDecoder().decode(base64EncodedFile);

        try (OutputStream stream = Files.newOutputStream(Paths.get(fileName))){
            stream.write(data);
        } catch (IOException e) {
            log.error("Not able to perform BS Download Operation", e);
        }
    }

    public List<String> getCaseFromPDF(final String fileDirectory, final String fileName) {
        final String pdfContent = getPDFFileContent(fileDirectory, fileName);
        final String[] lines = pdfContent.split("\n");
        final List<String> caseInPDF = new ArrayList<>();
        for (final String line : lines) {
            final String firstEightChars = line.length() >= 8 ? line.substring(0, 8) : "";
            if (firstEightChars.matches("\\d{8}")) {
                caseInPDF.add(firstEightChars.trim());
            }
        }
        return caseInPDF;
    }

    public String getPDFFileContent(final String fileDirectory, final String fileName) {
        String fileContent = "";
        final String fullName = fileDirectory + fileName;

        try {
            Thread.sleep(configProvider.getIntegerProperty(Constants.SELENIUM_LONG_WAIT) *2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(isLocalFileExist(fileDirectory,fileName)){
            fileContent = extractRecordsFromPDF(fullName);
        }else if(isRemoteFileExist(fileName)){
            performBSDownloadOperations(fileName);
            fileContent = extractRecordsFromPDF(fileName);
        }else {
            fail("File " + fileName + "is not exist.");
        }
        return fileContent;
    }

    private String extractRecordsFromPDF(String fileName){
        String fileContent = "";
        try (PDDocument document = PDDocument.load(new File(fileName))) {  // try-with-resources
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            fileContent = pdfStripper.getText(document);
        } catch (IOException e) {
            log.error("Error reading the PDF file: '{}'", e.getMessage());
        }
        return fileContent;
    }

    public void uploadFiles(final List<String> fileNames) {
        ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
        final WebElement inputFile = driver.findElement(By.xpath("//input[@type='file']"));
        for (final String fileName : fileNames) {
            final String filePath = configProvider.getFileDirectory("upload")+ fileName;
            inputFile.sendKeys(filePath);
        }
    }

}