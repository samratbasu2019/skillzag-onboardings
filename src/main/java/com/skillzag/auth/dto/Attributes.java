package com.skillzag.auth.dto;

import java.util.List;

import lombok.Data;

@Data
public class Attributes {


    private List<String> phoneNumber = null;

    private List<String> role = null;

    private List<String> institutionID = null;

    private List<String> institutionName = null;
    private List<String> address1;
    private List<String> address2;
    private List<Long> validFrom;
    private List<Long> validTo;
    private List<String> subscriptionType;
    private List<Long> subscriptionStartDate;
    private List<Long> subscriptionEndDate;
    private List<String> imagePath;

}