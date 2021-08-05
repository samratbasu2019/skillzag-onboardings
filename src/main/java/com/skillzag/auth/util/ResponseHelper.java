package com.skillzag.auth.util;

import com.skillzag.auth.dto.ResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ResponseHelper {
	private static ResponseDTO resAPI = new ResponseDTO();
	
	public static ResponseDTO populateRresponse(String message, Integer status) {
		resAPI.setStatus(status);
		resAPI.setError(message);
		return resAPI;
		
	}

}
