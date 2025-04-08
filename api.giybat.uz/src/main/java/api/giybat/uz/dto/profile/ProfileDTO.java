package api.giybat.uz.dto.profile;

import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO {
    private String id;
    private String name;
    private String surname;
    private String phone;
    private String password;
    private GeneralStatus status;
    private ProfileRole role;
    private Boolean visible;
    private LocalDateTime createdDate;

}
