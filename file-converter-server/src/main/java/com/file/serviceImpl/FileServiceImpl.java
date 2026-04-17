package com.file.serviceImpl;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.file.service.FileService;

@Service
public class FileServiceImpl implements FileService {

	public byte[] convertToPdf(MultipartFile file) {
		try {
			String tempDir = System.getProperty("java.io.tmpdir");
			String inputFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
			File inputFile = new File(tempDir, inputFileName);
			file.transferTo(inputFile);

			// Output PDF path
			String outputFileName = inputFileName.replaceAll("\\.[^.]+$", ".pdf");
			File outputFile = new File(tempDir, outputFileName);

			String sofficePath = "C:\\Program Files\\LibreOffice\\program\\soffice.exe";

			ProcessBuilder processBuilder = new ProcessBuilder(
					sofficePath,
					"--headless",
					"--convert-to", "pdf",
					"--outdir", tempDir,
					inputFile.getAbsolutePath()
					);

			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			process.waitFor();

			// Read PDF
			byte[] pdfBytes = Files.readAllBytes(outputFile.toPath());

			// Cleanup
			inputFile.delete();
			outputFile.delete();

			return pdfBytes;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Conversion failed", e);
		}
	}

}
