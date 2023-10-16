package com.mail.mailViolation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor

// 엑셀을 받는 DTO
public class FileUploadDto {
	private MultipartFile file;
}
