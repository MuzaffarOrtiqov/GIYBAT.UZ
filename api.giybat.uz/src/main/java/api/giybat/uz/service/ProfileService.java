package api.giybat.uz.service;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.CodeConfirmDTO;
import api.giybat.uz.dto.profile.ProfileDetailUpdateDTO;
import api.giybat.uz.dto.profile.ProfileUpdatePasswordDTO;
import api.giybat.uz.dto.profile.ProfileUpdateUsernameDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.util.JwtUtil;
import api.giybat.uz.util.SpringSecurityUtil;
import api.giybat.uz.util.ValidityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
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

    public ProfileEntity findProfileById(String id, AppLanguage lang) {
        return profileRepository.findByIdAndVisibleTrue(id).orElseThrow(() -> new AppBadException(resourceBundleMessageService.getMessage("profile.not.found", lang)));

    }


    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO profile, AppLanguage lang) {
        String userId = SpringSecurityUtil.getCurrentUserId();
        profileRepository.updateProfileName(profile.getName(), userId);
        return new AppResponse<>(resourceBundleMessageService.getMessage("profile.name.updated", lang));
    }

    public AppResponse<String> updatePassword(ProfileUpdatePasswordDTO profileDTO, AppLanguage lang) {
        String userId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profileEntity = findProfileById(userId, lang);
        if (!bCryptPasswordEncoder.matches(profileDTO.getCurrentPassword(), profileEntity.getPassword())) {
            throw new AppBadException(resourceBundleMessageService.getMessage("password.not.match", lang));
        }
        profileRepository.updatePassword(bCryptPasswordEncoder.encode(profileDTO.getNewPassword()), userId);
        return new AppResponse<>(resourceBundleMessageService.getMessage("password.update.success", lang));
    }

    public AppResponse<String> updateUsername(ProfileUpdateUsernameDTO profileDTO, AppLanguage lang) {
        //check
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(profileDTO.getUsername());
        if (optional.isPresent()) {
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
        return new AppResponse<>( jwt, resourceBundleMessageService.getMessage("username.update.success", lang));
    }
}
