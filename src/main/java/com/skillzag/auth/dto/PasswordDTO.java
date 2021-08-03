package com.skillzag.auth.dto;

import lombok.Data;

@Data
public class PasswordDTO {
  private String type;
  private boolean temporary;
  private String value;

}