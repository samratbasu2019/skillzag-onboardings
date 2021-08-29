package com.skillzag.auth.dto;

import java.util.List;

import lombok.Data;

@Data
public class Attributes {


    private List<String> phoneNumber = null;

    private List<String> role = null;

    private List<String> institutionID = null;

    private List<String> institutionName = null;

}