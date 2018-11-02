package com.s3bucket.S3Bucket.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.s3bucket.S3Bucket.util.S3Constants;

/**
 * The Class S3Config.
 */
@Service
public class S3Config {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(S3Config.class);

	/**
	 * S 3 authentication.
	 *
	 * @return the amazon S 3
	 */
	public AmazonS3 s3Authentication() {
		AmazonS3 s3client = AmazonS3Client.builder().withRegion(S3Constants.AWS_BUCKET_REGION)
				.withPathStyleAccessEnabled(true).build();
		LOGGER.info("s3Client {}", s3client);
		return s3client;

		// Or

		/*
		 * AWSCredentials credentials = new
		 * BasicAWSCredentials(S3Constants.AWS_ACCESS_KEY,
		 * S3Constants.AWS_SECRET_KEY); AmazonS3 s3client =
		 * AmazonS3ClientBuilder .standard() .withCredentials(new
		 * AWSStaticCredentialsProvider(credentials))
		 * .withRegion(Regions.US_EAST_2) .build(); return s3client;
		 */

	}

	/**
	 * Generate pre signed url.
	 *
	 * @param fileName the file name
	 * @return the string
	 */
	public String generatePreSignedUrl(String fileName) {
		AmazonS3 s3client = s3Authentication();
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
				S3Constants.AWS_BUCKET_NAME, fileName);
		generatePresignedUrlRequest.setMethod(HttpMethod.GET);
		java.util.Date expiration = new java.util.Date();
		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.DATE, 7);
		Date expirationDate = calender.getTime();
		expiration.setTime(expirationDate.getTime());
		generatePresignedUrlRequest.setExpiration(expiration);
		URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
		return url.toString();
	}

	/**
	 * Upload image on S 3 bucket.
	 *
	 * @param multipartFile the multipart file
	 * @return the string
	 */
	public String uploadImageOnS3Bucket(MultipartFile multipartFile) {
		if (multipartFile != null) {
			String originalFileName = multipartFile.getOriginalFilename();
			int dot = originalFileName.lastIndexOf(".");
			String extension = (dot == -1) ? "" : originalFileName.substring(dot + 1);
			String updatedFileName = UUID.randomUUID().toString() + "." + extension;
			try {
				InputStream is = multipartFile.getInputStream();
				ObjectMetadata meta = new ObjectMetadata();
				meta.setContentLength(is.available());
				AmazonS3 s3Client = s3Authentication();
				s3Client.putObject(new PutObjectRequest(S3Constants.AWS_BUCKET_NAME, updatedFileName, is, meta)
						.withCannedAcl(CannedAccessControlList.Private));
				is.close();
				LOGGER.info("The file is successfully uploaded. And file name is {}", updatedFileName);
				return updatedFileName;
			} catch (IOException ioException) {
				LOGGER.info("An exception occured while upload a file to amazon s3 server. And exception is {}",
						ioException.getMessage());
				return null;
			}
		}
		return null;
	}
}
