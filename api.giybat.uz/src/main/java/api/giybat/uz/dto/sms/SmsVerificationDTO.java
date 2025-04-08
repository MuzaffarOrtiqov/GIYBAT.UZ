package api.giybat.uz.dto.sms;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsVerificationDTO {
    @NotBlank(message = "Phone is required")
    private String phone;
    @NotBlank(message = "Code is required")
    private String code;
}
