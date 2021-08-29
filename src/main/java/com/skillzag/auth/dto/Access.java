package com.skillzag.auth.dto;
import lombok.Data;

@Data
public class Access {


    private Boolean manageGroupMembership;

    private Boolean view;

    private Boolean mapRoles;

    private Boolean impersonate;

    private Boolean manage;

}