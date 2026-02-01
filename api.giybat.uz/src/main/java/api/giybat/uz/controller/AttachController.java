package api.giybat.uz.controller;

import api.giybat.uz.dto.attach.AttachDTO;
import api.giybat.uz.service.AttachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/attach")
@Tag(name = "AttachController", description = "A set of APIs to work with attach")
@Slf4j
public class AttachController {

    @Autowired
    private AttachService attachService;

    @PostMapping("/upload")
    @Operation(summary = "Upload multipart file", description = "Method used to upload files to S3 cloud storage")
    public ResponseEntity<AttachDTO> create(@RequestParam("file") MultipartFile file) {
        // Now returns a DTO containing the direct S3 URL
        return ResponseEntity.ok(attachService.upload(file));
    }

//    /**
//     * NOTE: This method is now optional.
//     * Since S3 provides direct public URLs, your frontend can use those URLs
//     * directly without hitting this endpoint.
//     */
//    @GetMapping("/open/{fileName}")
//    @Operation(summary = "Open multipart file", description = "Serves the file from S3 through the backend")
//    public ResponseEntity<Resource> open(@PathVariable String fileName) {
//        // This will still work if you kept the open() method in your service
//        return attachService.open(fileName);
//    }
}