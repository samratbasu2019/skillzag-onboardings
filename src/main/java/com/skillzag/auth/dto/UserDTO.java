package com.skillzag.auth.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserDTO {
    @NotEmpty(message = "email can not be empty")
    @NotNull
    @Email
	private String email;
    @Pattern(regexp = "b2b|b2c|b2badmin|platformadmin")
    @NotEmpty(message = "role can not be empty")
    @NotNull
    private String role;
    @NotEmpty(message = "password can not be empty")
    @NotNull
    private String password;
    @NotEmpty(message = "firstname can not be empty")
    @NotNull
    private String firstname;
    @NotEmpty(message = "lastname can not be empty")
    @NotNull
    private String lastname;
    private int statusCode;
    private String status;
    private String phoneNumber;
    @NotEmpty(message = "institution name can not be empty")
    @NotNull
    private String institutionName;
    @NotEmpty(message = "institution code can not be empty")
    @NotNull
    private String institutionID;
    private String address1;
    private String address2;
    private Long validFrom;
    private Long validTo;
    private String subscriptionType;
    private Long subscriptionStartDate;
    private Long subscriptionEndDate;
}
