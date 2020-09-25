package com.gok.auth.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class UserDTO {
	private String username;
	private String email;
    @Pattern(regexp = "ROLE_NURSE|ROLE_OPERATOR|ROLE_GRIEVANCE_CELL|ROLE_TMD_USER|ROLE_AMBULANCE_FOLLOW_UP|ROLE_FOLLOW_UP|ROLE_FEVERCLINIC_FOLLOW_UP|ROLE_DOCTOR|ROLE_AMBULANCE_OPERATOR|ROLE_CAMPAIGN_MANAGER_UPLOAD|ROLE_CAMPAIGN_OWNER|ROLE_CAMPAIGN_OUTBOUND_CALLER|ROLE_CAMPAIGN_MANAGER_DOWNLOAD")
    private String role;
    private String password;
    private String firstname;
    private String lastname;
    private int statusCode;
    private String status;
    private String mobile;

}
