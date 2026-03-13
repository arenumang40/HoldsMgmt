package com.dhl.ecommerce.holdssbe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {

    public static final String APPLICATION_ENVIRONMENT = "APPLICATION_ENVIRONMENT";
    public static final String APPLICATION_BASE_URL = "APPLICATION_BASE_URL";
    public static final String APPLICATION_USERNAME = "APPLICATION_USERNAME";
    public static final String APPLICATION_PASSWORD = "APPLICATION_PASSWORD";
    public static final String SELENIUM_URL = "SELENIUM_URL";
    public static final String BROWSER_NAME = "BROWSER_NAME";
    public static final String SELENIUM_IMPLICIT_WAIT = "SELENIUM_IMPLICIT_WAIT";
    public static final String SELENIUM_SHORT_WAIT = "SELENIUM_SHORT_WAIT";
    public static final String RUN_DOCKER = "RUN_DOCKER"; //(true or false)

    public static final String MOBILE_RESOLUTION = "MOBILE_RESOLUTION"; //(width x height)

    public static final String BROWSERSTACK_URL = "BROWSERSTACK_URL";
    public static final String BROWSERSTACK_USERNAME = "BROWSERSTACK_USERNAME";
    public static final String BROWSERSTACK_ACCESS_KEY = "BROWSERSTACK_ACCESS_KEY";
    public static final String BROWSERSTACK_API_BASE_URL = "BROWSERSTACK_API_BASE_URL";
    public static final String BROWSERSTACK_DOWNLOAD_DIR = "BROWSERSTACK_DOWNLOAD_DIR";

    public static final String SELENIUM_LONG_WAIT = "SELENIUM_LONG_WAIT";

    public static final String API_ENVIRONMENT = "API_ENVIRONMENT";
    public static final String API_VERSION = "API_VERSION";
    public static final String APPLICATION_API_BASE_URL = "APPLICATION_API_BASE_URL";
    public static final String FILE_DOWNLOAD_PATH = "FILE_DOWNLOAD_PATH";
    public static final String FILE_UPLOAD_PATH = "FILE_UPLOAD_PATH";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String CLIENT_SECRET = "CLIENT_SECRET";
    public static final String SALESFORCE_USERNAME = "SALESFORCE_USERNAME";
    public static final String SALESFORCE_PASSWORD = "SALESFORCE_PASSWORD";
    public static final String SELENIUM_UPLOAD_PATH = "SELENIUM_UPLOAD_PATH"; //home/seluser/uploadedFiles/
    public static final String SELENIUM_DOWNLOAD_PATH = "SELENIUM_DOWNLOAD_PATH"; //home/seluser/Downloads/
    public static final String SELENIUM_CONTAINER_ID = "SELENIUM_CONTAINER_ID";  //selenium
    public static final String DOWNLOAD_FILE_NAME = "DOWNLOAD_FILE_NAME"; //'HOLDS MANAGEMENT.csv'

    public static final List<String> CASE_CATEGORIES = Arrays.asList(
            "Account",
            "Data",
            "Package/Shipment Quality",
            "Shipment Documentation"
    );

    public static final List<String> HOLD_STATUS = Arrays.asList(
            "Released",
            "Info Required",
            "Return to Customer",
            "Open"
    );

    public static final List<String> HOLD_TYPE = Arrays.asList(
            "NO DATA",
            "LATE ARRIVAL",
            "MIXED SERVICES",
            "BARCODE QUALITY",
            "CREDIT HOLD",
            "MISSING IAC",
            "MISSING DSM/BOL",
            "OVERSIZED",
            "DIMMING",
            "INCORRECT ACCOUNT",
            "DG HOLD",
            "SORT CODE ISSUE",
            "NO ACTIVE AGREEMENT",
            "NO SLOT FOUND",
            "TSA NOT APPROVED",
            "OTHER"
    );

    public static final List<String> CASE_REASON = Arrays.asList(
            "Customer Education - Recurring Holds",
            "Shipment Hold"
    );

    public static final List<String> CASE_DETAIL = Arrays.asList(
            "Credit Hold",
            "No Active Agreement",
            "Not DG Approval"
    );

    public static final List<String> PRODUCT_GROUP = Arrays.asList(
            "Domestic",
            "International"
    );

    public static final List<String> PRODUCT_TYPE = Arrays.asList(
            "SM Parcel Expedited",
            "SM Parcel Expedited Max",
            "SM Parcel Ground",
            "SM Parcel Plus Ground"
    );

    public static final List<String> RESPONSIBLE_PARTY = Arrays.asList(
            "Customer",
            "DHL"
    );

    public static final List<String> ROOT_CAUSE = Arrays.asList(
            "BOL/DSM Damaged/Destroyed",
            "BOL/DSM Lost in Transit",
            "BOL/DSM Not Provided by Customer",
            "BOL/DSM Not Signed by Customer",
            "BOL/DSM Provided by Customer Has Been Previously Used",
            "BOL/DSM Provided by Customer Has Incorrect/Incomplete Information",
            "Credit Hold",
            "Customer Commingled Services",
            "Customer Didn't Close Out",
            "Customer Didn't Provide Files",
            "Customer File Issue",
            "Customer Included Other Company Shipments",
            "Customer Not Approved for DG",
            "Customer Profile Not Set Up",
            "Deliver to DHLeC In Error by Transportation",
            "IAC Damaged/Destroyed",
            "IAC Lost in Transit",
            "IAC Not Provided by Customer",
            "IAC Provided by Customer Has Incorrect/Incomplete Information",
            "Internal IT issue",
            "Label Missing",
            "Label Missing/Incorrect Indicia",
            "Labels Cut Off",
            "Labels Damaged",
            "Labels Faded",
            "Labels Improperly Placed on Shipment",
            "Lines Through Labels",
            "No Active Agreement Found in NewOps",
            "Not DG Violation",
            "NQD",
            "Oversized",
            "Transportation Commingled Services"
    );

    public static final List<String> RESOLUTIONS = Arrays.asList(
            "BOL/DSM Provided by Customer",
            "Customer Closed Out",
            "Customer No Longer on Credit Hold – Shipment Processed",
            "Customer Notified of Issue",
            "Customer Provided File",
            "IAC Provided by Customer",
            "Internal IT Issue resolved – Shipment Processed",
            "Ops Manually Processed",
            "Ops Processed Without BOL/DSM",
            "Resolved By IT/Implementation",
            "Separated and Processed",
            "Shipment Discarded",
            "Shipment Processed",
            "Shipment Processed Manually",
            "Shipment Qualified as NQD - Processed",
            "Shipment Returned",
            "Solutions Closed Out"
    );

    public static final List<String> RISK_REASONS = Arrays.asList(
            "Billing / Finance",
            "Business Model Change",
            "Claims",
            "Integration / Technology",
            "Marketplace Demands",
            "Operations / Network",
            "Performance",
            "Pickup / Supplies",
            "Price",
            "Relationship",
            "RFP"
    );


    public static final Map<String, Integer> FIELD_MAX_LIMITS = new HashMap<>();
    static {
        FIELD_MAX_LIMITS.put("BOL/DSM Number",60_000);
        FIELD_MAX_LIMITS.put("Total Number of Pallets",10);
        FIELD_MAX_LIMITS.put("MAX PC CNT",7);
        FIELD_MAX_LIMITS.put("EXP PC CNT",10);
        FIELD_MAX_LIMITS.put("GND PC CNT",10);
        FIELD_MAX_LIMITS.put("INTL PC CNT",10);
        FIELD_MAX_LIMITS.put("Description", 32_000);
    }

    public static final Map<String, String> HOLDS_ELEMENT = new HashMap<>();
    static {
        HOLDS_ELEMENT.put("BOL/DSM Number","bolDsmNumber");
        HOLDS_ELEMENT.put("Total Number of Pallets","totalNumberOfPallets");
        HOLDS_ELEMENT.put("MAX PC CNT","max");
        HOLDS_ELEMENT.put("EXP PC CNT","exp");
        HOLDS_ELEMENT.put("GND PC CNT","gnd");
        HOLDS_ELEMENT.put("INTL PC CNT","intl");
        HOLDS_ELEMENT.put("Description", "description");
    }

    public static final Map<String, String> HOLDS_API_FIELDS = new HashMap<>();
    static {
        HOLDS_API_FIELDS.put("Status", "NA_Case_Detail__c");
        HOLDS_API_FIELDS.put("MAX","Qty_Of_Expedited_MAX_items__c");
        HOLDS_API_FIELDS.put("EXP","Qty_Of_Expedited_items__c");
        HOLDS_API_FIELDS.put("GND","Qty_Of_Ground_items__c");
        HOLDS_API_FIELDS.put("INTL","NA_Qty_Of_International_Items__c");
        HOLDS_API_FIELDS.put("PALLETS","NA_Total_Pallet_Count_On_Placard__c");

        HOLDS_API_FIELDS.put("Case Number", "CaseNumber");
        HOLDS_API_FIELDS.put("BOL/DSM Number","NA_BOL_Number__c");
        HOLDS_API_FIELDS.put("Hold Type", "NA_Hold_Type__c");
        HOLDS_API_FIELDS.put("Soldto Customer Name", "Account_Name_Form__c");
        HOLDS_API_FIELDS.put("Soldto Customer Number", "Account_Number__c");
        HOLDS_API_FIELDS.put("Pickup Customer Name","Name");
        HOLDS_API_FIELDS.put("Pickup Customer Number", "NA_Pickup_Account_Number__c");
        HOLDS_API_FIELDS.put("Created By", "NA_Creator_Name__c");
        HOLDS_API_FIELDS.put("Time On Hold","Case_Age_in_Hours__c");
        HOLDS_API_FIELDS.put("Description", "Description");

        HOLDS_API_FIELDS.put("Create Date", "CreatedDate");
        HOLDS_API_FIELDS.put("MAX PC CNT","Qty_Of_Expedited_MAX_items__c");
        HOLDS_API_FIELDS.put("EXP PC CNT","Qty_Of_Expedited_items__c");
        HOLDS_API_FIELDS.put("GND PC CNT","Qty_Of_Ground_items__c");
        HOLDS_API_FIELDS.put("INTL PC CNT","NA_Qty_Of_International_Items__c");
        HOLDS_API_FIELDS.put("Total Number of Pallets","NA_Total_Pallet_Count_On_Placard__c");
        HOLDS_API_FIELDS.put("Service Count", "Number_of_Claims__c");
    }

    public static final Map<String, String> HOLDS_DASHBOARD_ELEMENT = new HashMap<>();
    static {
        HOLDS_DASHBOARD_ELEMENT.put("BOL/DSM Number","BOL/DSM Number");
        HOLDS_DASHBOARD_ELEMENT.put("MAX PC CNT","MAX");
        HOLDS_DASHBOARD_ELEMENT.put("EXP PC CNT","EXP");
        HOLDS_DASHBOARD_ELEMENT.put("GND PC CNT","GND");
        HOLDS_DASHBOARD_ELEMENT.put("INTL PC CNT","INTL");
        HOLDS_DASHBOARD_ELEMENT.put("Total Number of Pallets","Pallets");
    }

    public static final String[] PLACARD_FIELDS_SEQ = {
            "DESCRIPTION",
            "MAX EXP GND INTL",
            "SALESFORCE CASE NUMBER",
            "BOL/DSM NUMBER",
            "CUSTOMER NUMBER / PICKUP NUMBER",
            "PICKUP CUSTOMER NAME",
            "NUMBER OF PALLETS",
            "CREATED DATE",
            "PLACED ON HOLD BY"
    };

    public static final Map<String, String> HOLDS_DETAIL_STS_FONT_COLOR = new HashMap<>();
    static {
        HOLDS_DETAIL_STS_FONT_COLOR.put("RELEASED", "rgba(46, 125, 50, 1)");
        HOLDS_DETAIL_STS_FONT_COLOR.put("INFO REQUIRED","rgba(237, 108, 2, 1)");
        HOLDS_DETAIL_STS_FONT_COLOR.put("RETURN TO CUSTOMER", "rgba(211, 47, 47, 1)");
        HOLDS_DETAIL_STS_FONT_COLOR.put("OPEN", "rgba(0, 0, 0, 0.87)");
    }

    public static final Map<String, String> HOLDS_DETAIL_STS_BG_COLOR = new HashMap<>();
    static {
        HOLDS_DETAIL_STS_BG_COLOR.put("RELEASED", "rgba(76, 175, 80, 0.1)");
        HOLDS_DETAIL_STS_BG_COLOR.put("INFO REQUIRED","rgba(255, 152, 0, 0.1)");
        HOLDS_DETAIL_STS_BG_COLOR.put("RETURN TO CUSTOMER", "rgba(239, 83, 80, 0.1)");
        HOLDS_DETAIL_STS_BG_COLOR.put("OPEN", "rgba(0, 0, 0, 0)");
    }




}