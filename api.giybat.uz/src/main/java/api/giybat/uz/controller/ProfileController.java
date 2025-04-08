package api.giybat.uz.controller;


import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.CodeConfirmDTO;
import api.giybat.uz.dto.profile.ProfileDetailUpdateDTO;
import api.giybat.uz.dto.profile.ProfileUpdatePasswordDTO;
import api.giybat.uz.dto.profile.ProfileUpdateUsernameDTO;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile/")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @PutMapping("/detail")
    public ResponseEntity<AppResponse<String>> updateDetail(
            @Valid @RequestBody ProfileDetailUpdateDTO profile,
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateDetail(profile, lang);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/password")
    public ResponseEntity<AppResponse<String>> updatePassword(
            @Valid @RequestBody ProfileUpdatePasswordDTO profile,
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updatePassword(profile, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/username")
    public ResponseEntity<AppResponse<String>> updateUsername(
            @Valid @RequestBody ProfileUpdateUsernameDTO profileDTO,
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateUsername(profileDTO, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/username-confirmation")
    public ResponseEntity<AppResponse<String>> updateUsernameConfirm(
            @Valid @RequestBody CodeConfirmDTO codeConfirmDTO,
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateUsernameConfirm(codeConfirmDTO, lang);
        return ResponseEntity.ok(response);
    }

}
