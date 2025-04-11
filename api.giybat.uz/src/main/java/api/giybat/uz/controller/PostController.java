package api.giybat.uz.controller;

import api.giybat.uz.util.SpringSecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post")
@Tag(name = "PostController", description = "A set of APIs to work with Post")
public class PostController {
    @PostMapping("/create")
    @Operation(summary = "Create post",description ="Method used to create a new post" )
    public ResponseEntity<String> create() {
        System.out.println(SpringSecurityUtil.getCurrentProfile());
        System.out.println(SpringSecurityUtil.getCurrentUserId());
        return null;
    }
}
