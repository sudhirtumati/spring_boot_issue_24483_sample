package com.app.utilities.appreferential.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/applications")
@Slf4j
public class ApplicationController {

	@PostMapping(value = "/actions/upload",
			consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, "multipart/form-data;charset=UTF-8" })
	@RolesAllowed("ADMIN")
	@ResponseStatus(HttpStatus.OK)
	public void uploadApplications(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a non-empty file");
		}
		else {
			log.info("File uploaded successfully");
		}
	}

}
