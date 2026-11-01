package com.file.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileStoreService {

	GridFsResource getDownloadFile(String fileName);

	String deleteMultipleFile(List<String> fileNames);

	String deleteSingleFile(String fileName);

	String singleUploadFile(MultipartFile file)throws IOException;

	List<String> multipleUploadFile(MultipartFile[] file)throws IOException;

	byte[] viewFile(String fileName);


}
