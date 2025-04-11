package api.giybat.uz.controller;


import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.CodeConfirmDTO;
import api.giybat.uz.dto.ProfilePhotoUpdateDTO;
import api.giybat.uz.dto.profile.ProfileDetailUpdateDTO;
import api.giybat.uz.dto.profile.ProfileUpdatePasswordDTO;
import api.giybat.uz.dto.profile.ProfileUpdateUsernameDTO;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile/")
@Tag(name = "AuthController", description = "A set of APIs to work with profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @PutMapping("/detail")
    @Operation(summary = "Update profile detail",description ="Method used to update details of a profile" )
    public ResponseEntity<AppResponse<String>> updateDetail(
            @Valid @RequestBody ProfileDetailUpdateDTO profile,
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateDetail(profile, lang);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/password")
    @Operation(summary = "Update profile password",description ="Method used to update password of a profile" )
    public ResponseEntity<AppResponse<String>> updatePassword(
            @Valid @RequestBody ProfileUpdatePasswordDTO profile,
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updatePassword(profile, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/username")
    @Operation(summary = "Update profile username",description ="Method used to update username of a profile" )
    public ResponseEntity<AppResponse<String>> updateUsername(
            @Valid @RequestBody ProfileUpdateUsernameDTO profileDTO,
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateUsername(profileDTO, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/username-confirmation")
    @Operation(summary = "Confirm profile's username",description ="Method used to confirm username of a profile" )
    public ResponseEntity<AppResponse<String>> updateUsernameConfirm(
            @Valid @RequestBody CodeConfirmDTO codeConfirmDTO,
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateUsernameConfirm(codeConfirmDTO, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/photo")
    @Operation(summary = "Update profile photo",description ="Method used to update photo of a profile" )
    public ResponseEntity<AppResponse<String>> updateProfilePhoto(@Valid @RequestBody ProfilePhotoUpdateDTO profileUpdateDTO,
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {

        AppResponse<String> response = profileService.updateProfilePhoto(profileUpdateDTO, lang);
        return ResponseEntity.ok(response);
    }

}
