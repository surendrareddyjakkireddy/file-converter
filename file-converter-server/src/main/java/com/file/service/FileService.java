package com.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author suren
 *
 */
public interface FileService {

	byte[] convertToPdf(MultipartFile file);

}
