package com.dhl.ecommerce.holdssbe.model;

import browserstack.shaded.com.google.gson.annotations.SerializedName;
import com.dhl.ecommerce.holdssbe.Constants;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Getter
@Setter
public class CaseClosedDTO {
    @SerializedName("Status")
    private String Status;
    @SerializedName("Receipt_Date__c")
    private String Receipt_Date__c;
    @SerializedName("NA_Mail_Terminal__c")
    private String NA_Mail_Terminal__c;

    @SerializedName("NA_Case_Category__c")
    private final String NA_Case_Category__c;
    @SerializedName("Reason")
    private final String Reason;
    @SerializedName("NA_Case_Detail__c")
    private final String NA_Case_Detail__c;
    @SerializedName("NA_Product_Group__c")
    private final String NA_Product_Group__c;
    @SerializedName("Product_Type__c")
    private final String Product_Type__c;
    @SerializedName("NA_Responsible_Party_NA__c")
    private final String NA_Responsible_Party_NA__c;
    @SerializedName("CS_Group__c")
    private final String CS_Group__c;
    @SerializedName("NA_Root_Cause__c")
    private final String NA_Root_Cause__c;
    @SerializedName("Resolution_NA__c")
    private final String Resolution_NA__c;
    @SerializedName("Number_of_Claims__c")
    private final String Number_of_Claims__c;
    @SerializedName("NA_BOL_Number__c")
    private String NA_BOL_Number__c;

    public CaseClosedDTO() {
        Random random = new Random();

        this.Status="Closed";
        this.NA_Mail_Terminal__c="DGM Phoenix - PHX";
        this.Receipt_Date__c= ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        this.NA_Case_Category__c= Constants.CASE_CATEGORIES.get(random.nextInt(Constants.CASE_CATEGORIES.size()));
        this.Reason=Constants.CASE_REASON.get(random.nextInt(Constants.CASE_REASON.size()));
        this.NA_Case_Detail__c=Constants.CASE_DETAIL.get(random.nextInt(Constants.CASE_DETAIL.size()));
        this.NA_Product_Group__c=Constants.PRODUCT_GROUP.get(random.nextInt(Constants.PRODUCT_GROUP.size()));
        this.Product_Type__c=Constants.PRODUCT_TYPE.get(random.nextInt(Constants.PRODUCT_TYPE.size()));
        this.NA_Responsible_Party_NA__c=Constants.RESPONSIBLE_PARTY.get(random.nextInt(Constants.RESPONSIBLE_PARTY.size()));
        this.CS_Group__c="Holds";
        this.NA_Root_Cause__c=Constants.ROOT_CAUSE.get(random.nextInt(Constants.ROOT_CAUSE.size()));
        this.Resolution_NA__c=Constants.RESOLUTIONS.get(random.nextInt(Constants.RESOLUTIONS.size()));
        this.Number_of_Claims__c= String.valueOf(random.nextInt(999));
        this.NA_BOL_Number__c=String.valueOf(random.nextInt(99));
    }
}