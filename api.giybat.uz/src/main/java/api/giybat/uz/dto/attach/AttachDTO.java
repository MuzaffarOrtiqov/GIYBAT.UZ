package api.giybat.uz.dto.attach;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class AttachDTO {

    private String id;
    private String originName;
    private Long size;
    private String extension;
    private LocalDateTime createdData;
    private String url;

    public AttachDTO(String id, String url) {
    }
}
