package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.auth.AuthDTO;
import api.giybat.uz.dto.auth.ResetPasswordDTO;
import api.giybat.uz.dto.auth.ResetPasswordConfirmDTO;
import api.giybat.uz.dto.profile.ProfileResponseDTO;
import api.giybat.uz.dto.auth.RegistrationDTO;
import api.giybat.uz.dto.sms.SmsResendDTO;
import api.giybat.uz.dto.sms.SmsVerificationDTO;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
@Tag(name = "AuthController", description = "A set of APIs to work with Authentication and Authorization")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registration")
    @Operation(summary = "Registration",description ="Method used to register a new user" )
    public ResponseEntity<AppResponse<String>> registration(@Valid @RequestBody RegistrationDTO dto,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok().body(authService.registration(dto, lang));
    }

    @GetMapping("/registration/email-verification/{token}")
    @Operation(summary = "Registration Verification email",description ="Method used to verify the user getting registered through email" )
    public ResponseEntity<ProfileResponseDTO> regVerificationEmail(@PathVariable String token,
                                                                   @RequestParam(name = "lang", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok().body(authService.regVerificationEmail(token, lang));
    }

    @PostMapping("/registration/sms-verification")
    @Operation(summary = "Registration Verification SMS",description ="Method used to verify the user getting registered through SMS" )
    public ResponseEntity<ProfileResponseDTO> regVerificationSms(@RequestBody SmsVerificationDTO dto,
                                                                 @RequestHeader(name = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok(authService.regVerificationSms(dto, lang));

    }

    @PostMapping("/registration/sms-verification-resend")
    @Operation(summary = "Registration SMS resend",description ="Method used to resend SMS for an unregistered user" )
    public ResponseEntity<AppResponse<String>> regVerificationSmsResend(@RequestBody SmsResendDTO dto,
                                                                        @RequestHeader(name = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok(authService.regVerificationSmsResend(dto, lang));

    }

    @PostMapping("/login")
    @Operation(summary = "Login",description ="Method used to log in to the website" )
    public ResponseEntity<ProfileResponseDTO> login(@Valid @RequestBody AuthDTO authDTO,
                                                    @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok(authService.login(authDTO, lang));
    }

    @PostMapping("/password-reset")
    @Operation(summary = "Reset Password",description ="Method used to reset the forgotten password" )
    public ResponseEntity<AppResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordDTO dto,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok(authService.resetPassword(dto, lang));
    }

    @PostMapping("/password-reset-confirm")
    @Operation(summary = "Password Reset Confirm",description ="Method used to confirm whether the new username belongs to him/her" )
    public ResponseEntity<AppResponse<String>> resetPasswordConfirm(@Valid @RequestBody ResetPasswordConfirmDTO dto,
                                                                        @RequestHeader(name = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok(authService.resetPasswordConfirm(dto, lang));

    }
}
