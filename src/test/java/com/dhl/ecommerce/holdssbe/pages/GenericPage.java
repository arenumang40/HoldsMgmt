package com.dhl.ecommerce.holdssbe.pages;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import java.util.List;
import static org.testng.FileAssert.fail;

@Slf4j
public class GenericPage extends ApplicationPage{
    private static final String VALUE_FIELD = "Value";

    public GenericPage(final WebDriver driver, final ConfigProvider configProvider) {
        super(driver,configProvider);
    }

    private WebElement generateElementByXpath(final String xpathExp) {
        WebElement element = null;
        try{
            element = driver.findElement(By.xpath(xpathExp));
        }catch(NoSuchElementException e){
            log.error("Element with XPath '{}' was not found. Exception: {}", xpathExp, e.getMessage());
        }
        return  element;
    }

    private List<WebElement> generateListOfElementFromXpath(final String xpathExp){
        List<WebElement> elementList = List.of();
        try{
            elementList = driver.findElements(By.xpath(xpathExp));
        }catch(NoSuchElementException e){
            fail("Not able to generate elements , Getting Exception : " + e.getMessage());
        }
        return elementList;
    }

    public WebElement getElementByRegText(final String text){
       return generateElementByXpath(String.format("//*[text()='%s']", text.trim()));
    }

    public List<WebElement> getElementsByRegText(final String text){
        return generateListOfElementFromXpath(String.format("//*[text()='%s']", text.trim()));
    }

    public WebElement getElementByPlaceholderText(final String text){
        return generateElementByXpath(String.format("//*[@placeholder='%s']", text.trim()));
    }

    public WebElement getElementByDataTestId(final String text){
        return generateElementByXpath(String.format("//*[contains(@data-testid,'%s')]", text.trim()));
    }

    public WebElement getElementByDivRoleToolTip(){
        return generateElementByXpath("//div[@role='tooltip']");
    }

    public WebElement getElementForFilterValue(){
        return getElementByPlaceholderText("Filter value");
    }

    public WebElement getElementForFilterField(final String fromField){
        return generateElementByXpath(getXpathForFilterField(fromField));
    }

    private String getXpathForFilterField(final String fromField) {
        return VALUE_FIELD.equals(fromField)
                ? "(//*[@role='combobox'])[4]"
                : String.format("//*[@role='combobox' and text()='%s']", fromField);
    }

    public WebElement getElementForFilterValuesToSelect(final String values){
        final WebElement newDiv = getElementByDivRoleToolTip();
        return newDiv.findElement(By.xpath("//*[@role='option' and text()='" + values + "']"));
    }

    public WebElement getCreateHoldElement(final String text){
        return generateElementByXpath(String.format("//*[@name='%s']", text.trim()));
    }

    public List<WebElement> getElementForAttachmentThumbnail(final String fileName){
       return generateListOfElementFromXpath(String.format("//*[contains(@data-testid,attachment-%s)]", fileName.trim()));
    }
}