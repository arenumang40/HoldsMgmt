package com.dhl.ecommerce.holdssbe;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public final class SharedContext {
    private static SharedContext instance;

    //during Hold Creation
    private String selectedPickup;
    private String selectedHoldType;
    private Map<String, String> selectedFieldsValues;
    private String newCaseNumber;

    // From Hold Dashboard
    private String clickedCaseNumber;
    private String clickedCaseID;

    private SharedContext() {}

    // Static inner class responsible for holding the Singleton instance.
    private static class SingletonHelper {
        private static final SharedContext INSTANCE = new SharedContext();
    }

    // Public method to provide access to the Singleton instance
    public static SharedContext getInstance() {
        return SingletonHelper.INSTANCE;
    }

}