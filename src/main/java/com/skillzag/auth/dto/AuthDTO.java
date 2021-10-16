package com.skillzag.auth.dto;

import lombok.Data;

@Data
public class AuthDTO {
	private String email;
    private String password;
    private Boolean hasTermsChecked;
}
