package com.file.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.file.service.FileService;

@RestController
@RequestMapping("/api/file/service")
public class FileController {

	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping("/docx-to-pdf")
	public ResponseEntity<?> convert(@RequestParam("file") MultipartFile file) {
		try {

			byte[] convertedFile = fileService.convertToPdf(file);

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf")
					.contentType(MediaType.APPLICATION_PDF).body(convertedFile);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Conversion failed: " + e.getMessage());
		}
	}
}
