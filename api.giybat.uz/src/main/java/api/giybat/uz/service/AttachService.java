package api.giybat.uz.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import api.giybat.uz.dto.attach.AttachDTO;
import api.giybat.uz.entity.AttachEntity;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.AttachRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AttachService {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Autowired
    private AttachRepository attachRepository;

    public AttachDTO upload(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("File is empty");
            throw new AppBadException("File not found");
        }

        try {
            String pathFolder = getYmDString(); // e.g., 2026/1/29
            String key = UUID.randomUUID().toString();
            String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
            String fileId = key + "." + extension;

            // S3 Key (Full path in bucket)
            String s3Key = pathFolder + "/" + fileId;

            // Prepare S3 Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // Upload to S3
            s3Client.putObject(new PutObjectRequest(bucketName, s3Key, file.getInputStream(), metadata));

            // Save metadata to database
            AttachEntity entity = new AttachEntity();
            entity.setId(fileId);
            entity.setPath(pathFolder); // Storing folder structure in DB
            entity.setSize(file.getSize());
            entity.setOriginName(file.getOriginalFilename());
            entity.setExtension(extension);
            entity.setVisible(true);
            attachRepository.save(entity);

            return toDTO(entity);
        } catch (IOException e) {
            log.error("S3 Upload failed: {}", e.getMessage());
            throw new AppBadException("Could not upload file to cloud");
        }
    }

    /**
     * Generates the public S3 URL for the file
     */
    public String openURL(AttachEntity entity) {
        String s3Key = entity.getPath() + "/" + entity.getId();
        return s3Client.getUrl(bucketName, s3Key).toString(); // Returns direct S3 URL
    }

    public boolean delete(String id) {
        AttachEntity entity = getEntity(id);
        String s3Key = entity.getPath() + "/" + entity.getId();

        // Delete from S3
        s3Client.deleteObject(bucketName, s3Key);

        // Delete from DB
        attachRepository.deleteById(id);
        return true;
    }

    // --- Util Methods ---

    private String getYmDString() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private AttachDTO toDTO(AttachEntity entity) {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(entity.getId());
        attachDTO.setOriginName(entity.getOriginName());
        attachDTO.setSize(entity.getSize());
        attachDTO.setExtension(entity.getExtension());
        attachDTO.setUrl(openURL(entity)); // Link to S3 instead of local /open/ controller
        return attachDTO;
    }

    public AttachEntity getEntity(String id) {
        return attachRepository.findById(id)
                .orElseThrow(() -> new AppBadException("File not found in database"));
    }

    public AttachDTO toDTO(String attachId) {
        if (attachId == null) {
            return null;
        }
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(attachId);
        attachDTO.setUrl(openURL(getEntity(attachId)));
        return attachDTO;
    }
}