package com.file.controller;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.file.dto.FileViewDTO;
import com.file.service.FileStoreService;
import com.file.utils.AppConstants;
import com.file.utils.ServiceControllerUtils;
import com.file.utils.ServiceResponse;
import com.mongodb.client.gridfs.model.GridFSFile;

@RestController
@RequestMapping("api/file")
public class FileStoreController {

	@Autowired
	private FileStoreService fileStoreService;

	@Autowired
	private GridFsOperations operations;

	@Autowired
	private ServiceControllerUtils scutils;

	@GetMapping("/download/{fileName}")		
	ResponseEntity<InputStreamResource> getFile(@PathVariable("fileName") String fileName ) throws Exception {

		GridFsResource fileResource = fileStoreService.getDownloadFile(fileName);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
				.contentType(MediaType.valueOf(fileResource.getContentType()))
				.body(fileResource);
	}

	@GetMapping("/view/{fileName}")		
	ResponseEntity<byte[]> viewFiles(@PathVariable("fileName") String fileName) throws Exception {
		GridFsResource fileResource = fileStoreService.getDownloadFile(fileName);

		return ResponseEntity
				.ok()
				.contentType(MediaType.valueOf(fileResource.getContentType()))
				//.contentLength(fileResource.length)
				//.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
				.body(IOUtils.toByteArray(fileResource.getInputStream()));
	}

	@GetMapping("/fileView/{fileName}")		
	ResponseEntity<?> fileView(@PathVariable("fileName") String fileName) throws Exception {
		ResponseEntity<?> resp= null;
		ServiceResponse restResponse=new ServiceResponse();
		try {
			GridFsResource fileResource = fileStoreService.getDownloadFile(fileName);
			FileViewDTO fileViewDTO=new FileViewDTO();
			if(fileResource!=null) {
				byte[] fileView=IOUtils.toByteArray(fileResource.getInputStream());
				fileViewDTO.setFileView(fileView);		
				restResponse.addDataObject("FileView", fileViewDTO);
				restResponse = scutils.prepareMobileResponseSuccessStatus(restResponse, "File is Found");
				resp = new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.OK);
			}else {
				restResponse.addDataObject("FileView", fileViewDTO);
				restResponse = scutils.prepareMobileResponseInvalidData(restResponse, "File is not available");
				resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.OK);
			}	
		}
		catch(Exception e) {
			if(e.toString().contains("E11000")) {
				restResponse = scutils.prepareMobileResponseErrorStatus(restResponse, AppConstants.ERRORCODE, "Duplicate Data");
				resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.OK);
			}
			else {
				restResponse = scutils.prepareMobileResponseErrorStatus(restResponse, AppConstants.ERRORCODE, e.getMessage());
				resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			e.printStackTrace();
		}
		return resp;
	}


	@DeleteMapping("/multipledelete/{fileNames}")
	public String deleteMultipleFiles(@PathVariable("fileNames") List<String> fileNames){
		fileStoreService.deleteMultipleFile(fileNames);
		return "delete";
	}

	@DeleteMapping("/singledelete/{fileName}")
	public ResponseEntity<?> deleteSingleFiles(@PathVariable("fileName") String fileName){
		ResponseEntity<?> resp= null;
		ServiceResponse restResponse=new ServiceResponse();
		try {
			if (fileName != null) {
				GridFSFile gridFSFile = operations.findOne(new Query(Criteria.where("fileName").is(fileName)));
				if (gridFSFile == null) {
					restResponse = scutils.prepareMobileResponseInvalidData(restResponse, "File is not available");
					resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.OK);
					return resp;
				}
				if (gridFSFile != null) {
					this.fileStoreService.deleteSingleFile(fileName);
				}
				restResponse.addDataObject("file", gridFSFile.getFilename());
				restResponse = scutils.prepareMobileResponseSuccessStatus(restResponse,
						" file deleted  successfully");
				resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.OK);
				return resp;
			}
		}
		catch(Exception e) {
			restResponse = scutils.prepareMobileResponseErrorStatus(restResponse, AppConstants.ERRORCODE, e.getMessage());
			resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
		return resp;
	}

	/*@PostMapping("/singlefileupload")
		public String uploadSingleFile(@RequestParam("file") MultipartFile file){
			String fileName = null;
			try {
				fileName = fileStoreService.singleUploadFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return fileName;
		}*/

	@PostMapping("/singlefileupload")
	ResponseEntity<?> create(@RequestParam("file") MultipartFile file) {
		ResponseEntity<?> resp= null;
		ServiceResponse restResponse=new ServiceResponse();
		String fileName = null;
		try {
			fileName = fileStoreService.singleUploadFile(file);
			restResponse.addDataObject("FileName", fileName);
			restResponse = scutils.prepareMobileResponseSuccessStatus(restResponse, "File is saved");
			resp = new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.OK);
		}
		catch(Exception e) {
			if(e.toString().contains("E11000")) {
				restResponse = scutils.prepareMobileResponseErrorStatus(restResponse, AppConstants.ERRORCODE, "Duplicate Data");
				resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.OK);
			}
			else {
				restResponse = scutils.prepareMobileResponseErrorStatus(restResponse, AppConstants.ERRORCODE, e.getMessage());
				resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			e.printStackTrace();
		}
		return resp;
	}

	@PostMapping("/multiplefileuploadfiles")
	ResponseEntity<?> multipleAttachments(@RequestParam("file") MultipartFile[] file) {
		ResponseEntity<?> resp= null;
		ServiceResponse restResponse=new ServiceResponse();
		List<String> fileNames = null;
		try {
			fileNames = fileStoreService.multipleUploadFile(file);
			restResponse.addDataObject("FileNames", fileNames);
			restResponse = scutils.prepareMobileResponseSuccessStatus(restResponse, "Files are saved");
			resp = new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.OK);
		}
		catch(Exception e) {
			if(e.toString().contains("E11000")) {
				restResponse = scutils.prepareMobileResponseErrorStatus(restResponse, AppConstants.ERRORCODE, "Duplicate Data");
				resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.OK);
			}
			else {
				restResponse = scutils.prepareMobileResponseErrorStatus(restResponse, AppConstants.ERRORCODE, e.getMessage());
				resp=new ResponseEntity<ServiceResponse>(restResponse,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			e.printStackTrace();
		}
		return resp;
	}

}

