package com.skillzag.auth.dto;

import java.util.List;
import lombok.Data;

@Data
public class Contract {
    private String id;
    private Long createdTimestamp;
    private String username;

    private Boolean enabled;

    private Boolean totp;

    private Boolean emailVerified;

    private String firstName;

    private String lastName;

    private String email;

    private Attributes attributes;

    private List<Object> disableableCredentialTypes = null;

    private List<Object> requiredActions = null;

    private Integer notBefore;

    private Access access;

}