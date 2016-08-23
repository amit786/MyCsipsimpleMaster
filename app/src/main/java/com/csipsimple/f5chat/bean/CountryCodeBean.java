package com.csipsimple.f5chat.bean;

import java.io.Serializable;

/**
 * Created by HP on 23-05-2016.
 */
public class CountryCodeBean implements Serializable {

    String countryName,countryCode;

//HashMap <String,String>
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
