package com.skillzag.auth.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class UserDTO {
	private String username;
	private String email;
    @Pattern(regexp = "b2b")
    private String role;
    private String password;
    private String firstname;
    private String lastname;
    private int statusCode;
    private String status;
    private String mobile;

}
