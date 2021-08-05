package com.skillzag.auth.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class UserDTO {
	private String email;
    @Pattern(regexp = "b2b|b2c")
    private String role;
    private String password;
    private String firstname;
    private String lastname;
    private int statusCode;
    private String status;
    private String phoneNumber;
    private String institutionName;
    private String institutionID;
    private String address1;
    private String address2;
}
