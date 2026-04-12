package com.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	byte[] convertToPdf(MultipartFile file);

}
