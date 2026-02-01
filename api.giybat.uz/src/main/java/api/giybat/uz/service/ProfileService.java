package api.giybat.uz.service;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.CodeConfirmDTO;
import api.giybat.uz.dto.ProfilePhotoUpdateDTO;
import api.giybat.uz.dto.profile.*;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.mapper.ProfileDetailMapper;
import api.giybat.uz.repository.PostRepository;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.util.JwtUtil;
import api.giybat.uz.util.SpringSecurityUtil;
import api.giybat.uz.util.ValidityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private PostService postService;

    public ProfileEntity findProfileById(String id, AppLanguage lang) {
        log.error("No profile found with id: {}", id);
        return profileRepository.findByIdAndVisibleTrue(id).orElseThrow(() -> new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang)));

    }

    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO dto, AppLanguage lang) {
        String userId = SpringSecurityUtil.getCurrentUserId();

        // Verification: Check how many rows were modified
        int rowsAffected = profileRepository.updateProfileName(dto.getName(), userId);

        if (rowsAffected == 0) {
            // This happens if the user ID from the token doesn't match any DB row
            // (e.g., account deleted/banned mid-session)
            log.error("Profile update failed. User ID {} not found in DB.", userId);
            throw new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang));
        }

        return new AppResponse<>(resourceBundleMessageService.getMessage("profile.name.updated", lang));
    }

    public AppResponse<String> updatePassword(ProfileUpdatePasswordDTO profileDTO, AppLanguage lang) {
        String userId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profileEntity = findProfileById(userId, lang);
        if (!bCryptPasswordEncoder.matches(profileDTO.getCurrentPassword(), profileEntity.getPassword())) {
            log.warn("Password mismatch: userId:{}", userId);
            throw new AppBadException(resourceBundleMessageService.getMessage("password.not.match", lang));
        }
        profileRepository.updatePassword(bCryptPasswordEncoder.encode(profileDTO.getNewPassword()), userId);
        return new AppResponse<>(resourceBundleMessageService.getMessage("password.update.success", lang));
    }

    public AppResponse<String> updateUsername(ProfileUpdateUsernameDTO profileDTO, AppLanguage lang) {
        //check
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(profileDTO.getUsername());
        if (optional.isPresent()) {
            log.info("Username already in use: {}", profileDTO.getUsername());
            throw new AppBadException(resourceBundleMessageService.getMessage("email.phone.exists", lang));
        }

        //send confirm code
        if (ValidityUtil.isValidEmail(profileDTO.getUsername())) {
            emailSendingService.sendUsernameChangeConfirmEmail(profileDTO.getUsername(), lang);
        }
        if (ValidityUtil.isValidPhone(profileDTO.getUsername())) {
            smsSendService.sendUsernameChangeConfirmSms(profileDTO.getUsername(), lang);
        }

        String userId = SpringSecurityUtil.getCurrentUserId();
        profileRepository.updateTempUsername(profileDTO.getUsername(), userId);

        return new AppResponse<>(resourceBundleMessageService.getMessage("confirm.code.sent", lang, profileDTO.getUsername()));
    }

    public AppResponse<String> updateUsernameConfirm(CodeConfirmDTO dto, AppLanguage lang) {
        String userId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profileEntity = findProfileById(userId, lang);
        String tempUsername = profileEntity.getTempUsername();
        // check if email valid
        if (ValidityUtil.isValidEmail(tempUsername)) {
            emailHistoryService.check(tempUsername, dto.getCode(), lang);
        }
        //check if phone valid
        if (ValidityUtil.isValidPhone(tempUsername)) {
            smsHistoryService.check(tempUsername, dto.getCode(), lang);
        }
        //update username after checking
        profileRepository.updateUsername(userId, tempUsername);

        List<ProfileRole> roleList = profileRoleRepository.getAllRoles(profileEntity.getId());
        String jwt = JwtUtil.encode(userId, tempUsername, roleList);
        return new AppResponse<>(jwt, resourceBundleMessageService.getMessage("username.update.success", lang));
    }

    public AppResponse<String> updateProfilePhoto(ProfilePhotoUpdateDTO profileUpdateDTO, AppLanguage lang) {
        String userId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profileEntity = findProfileById(userId, lang);
        String oldPhotoId = profileEntity.getPhotoId();
        String newPhotoId = profileUpdateDTO.getPhotoId();

        // 1. Update the profile to the NEW photo first (clears the reference to the old one)
        if (newPhotoId != null && !newPhotoId.equals(oldPhotoId)) {
            profileRepository.updateProfilePhoto(userId, newPhotoId);

            // 2. Now that the reference is gone, delete the old photo record
            if (oldPhotoId != null) {
                attachService.delete(oldPhotoId);
            }
        }

        return new AppResponse<>(resourceBundleMessageService.getMessage("profile.photo.updated", lang));
    }

    public Page<ProfileDTO> filterProfile(ProfileFilterDTO profileFilterDTO, AppLanguage lang, int page, Integer size) {
        Page<ProfileDetailMapper> profileDetailMapperPage = null;
        Pageable pageable = PageRequest.of(page, size);
        if (profileFilterDTO.getQuery() == null) {
            profileDetailMapperPage = profileRepository.filterProfile(pageable);
        } else {
            profileDetailMapperPage = profileRepository.filterProfile("%" + profileFilterDTO.getQuery().toLowerCase() + "%", pageable);
        }
        List<ProfileDTO> profileDTOList = Objects.requireNonNull(profileDetailMapperPage).stream().map(this::toDTO).toList();
        return new PageImpl<>(profileDTOList, pageable, profileDetailMapperPage.getTotalElements());
    }

    public AppResponse<String> changeProfileStatus(String targetUserId, ProfileStatusDTO dto, AppLanguage lang) {
        // 2. Capture who is performing the action for logs (Auditing)
        String adminId = SpringSecurityUtil.getCurrentUserId();

        // 3. Perform the update and capture the result
        int rowsAffected = profileRepository.updateStatus(targetUserId, dto.getStatus());

        // 4. Verification: Did we actually change anything?
        if (rowsAffected == 0) {
            log.warn("Admin {} tried to update status for non-existent user {}", adminId, targetUserId);
            throw new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang));
        }

        // 5. Log the security event
        log.info("Profile status changed. Admin [{}] changed User [{}] to Status [{}]",
                adminId, targetUserId, dto.getStatus());

        return new AppResponse<>(resourceBundleMessageService.getMessage("profile.update.success", lang));
    }


    public AppResponse<String> deleteProfile(String userId, AppLanguage lang) {
        // 1. Perform Soft Delete (Change 'visible' to false)
        int rowsAffected = profileRepository.softDeleteProfile(userId);

        // 2. Verification: If 0 rows changed, the user didn't exist or was already deleted
        if (rowsAffected == 0) {
            throw new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang));
        }

        // 3. Delete post's of a deleted user
        postService.deletePostOfDeletedUser(userId);

        return new AppResponse<>(resourceBundleMessageService.getMessage("profile.delete.success", lang));
    }

    // util methods
    private ProfileDTO toDTO(ProfileEntity profileEntity) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(profileEntity.getId());
        profileDTO.setName(profileEntity.getName());
        profileDTO.setUsername(profileEntity.getUsername());
        if (profileEntity.getRoles() != null) {
            List<ProfileRole> profileRoleList = profileEntity.getRoles().stream().map(ProfileRoleEntity::getRoles).toList();
            profileDTO.setRole(profileRoleList);
        }
        profileDTO.setAttachDTO(attachService.toDTO(profileEntity.getPhotoId()));
        profileDTO.setStatus(profileEntity.getStatus());
        profileDTO.setCreatedDate(profileEntity.getCreatedDate());
        return profileDTO;
    }

    private ProfileDTO toDTO(ProfileDetailMapper mapper) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(mapper.getId());
        profileDTO.setName(mapper.getName());
        profileDTO.setPostCount(mapper.getPostCount());
        profileDTO.setUsername(mapper.getUsername());
        if (mapper.getProfileRole() != null) {
            String roles = mapper.getProfileRole();
            String[] rolesArray=  roles.split(",");
            List<ProfileRole> profileRoleList = new ArrayList<>();
            for (String role : rolesArray) {
                ProfileRole profileRole = ProfileRole.valueOf(role);
                profileRoleList.add(profileRole);
            }
            profileDTO.setRole(profileRoleList);
        }
        profileDTO.setAttachDTO(attachService.toDTO(mapper.getPhotoId()));
        profileDTO.setStatus(mapper.getStatus());
        profileDTO.setCreatedDate(mapper.getCreatedDate());
        return profileDTO;
    }


}
