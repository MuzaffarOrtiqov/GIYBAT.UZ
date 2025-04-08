package api.giybat.uz.dto.profile;

import api.giybat.uz.dto.attach.AttachShortDTO;
import api.giybat.uz.enums.ProfileRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDTO {
    //name,username,roleList, jwt, photo{id,url}
    private String id;
    private String name;
    private String username;
    private List<ProfileRole> roleList;
    private String jwt;
    private AttachShortDTO attachShortDTO;
}
