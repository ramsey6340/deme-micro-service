package com.infinity.serviceauth.controllers;

import com.infinity.serviceauth.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth/")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("profile/pic")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile multipartFile, @RequestParam("fileName") String fileName) throws IOException {
        //logger.info("HIT -/upload | File Name : {}", multipartFile.getOriginalFilename());
        return fileService.upload(multipartFile, fileName);
    }

    @PostMapping("profile/pic/{fileName}")
    public Object download(@PathVariable String fileName) throws IOException {
        //logger.info("HIT -/download | File Name : {}", fileName);
        return fileService.download(fileName);
    }
}
