package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.post.*;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.PostService;
import api.giybat.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@Slf4j
@Tag(name = "PostController", description = "A set of APIs to work with Post")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Create post", description = "Method used to create a new post")
    public ResponseEntity<PostDTO> create(@Valid @RequestBody PostCreateDTO dto,
                                          @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Create post: {}", dto);
        return ResponseEntity.ok(postService.create(dto, lang));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @Operation(summary = "Get profile's own post", description = "Method used to get profile's own posts")
    public ResponseEntity<Page<PostDTO>> getProfilePost(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                        @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Get profile's own posts: ");
        return ResponseEntity.ok(postService.getProfilePost(PageUtil.giveProperPageNumbering(page), size, lang));
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "Get post's full detail", description = "Method used to get all details of a post")
    public ResponseEntity<PostDTO> getPostById(@PathVariable(name = "id") String postId,
                                               @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Get post by id: {}", postId);
        return ResponseEntity.ok(postService.getFullPostDetails(postId, lang));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @Operation(summary = "Update post details", description = "Method used to get all details of a post")
    public ResponseEntity<PostDTO> updatePost(@PathVariable(name = "id") String postId,
                                              @RequestBody PostUpdateDTO postUpdateDTO,
                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Update post by id: {}", postId);
        return ResponseEntity.ok(postService.updatePost(postId, postUpdateDTO, lang));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @Operation(summary = "Delete a post", description = "Method used to delete by id")
    public ResponseEntity<Boolean> deletePostById(@PathVariable(name = "id") String postId,
                                                  @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Delete post by id: {}", postId);
        return ResponseEntity.ok(postService.deletePostById(postId, lang));
    }

    @PostMapping("/public/filter")
    @Operation(summary = "Filter posts", description = "Method used to filter posts")
    public ResponseEntity<Page<PostDTO>> filter(@RequestBody PostFilterDTO postFilterDTO,
                                                @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                @RequestParam(name = "size", defaultValue = "5") Integer size) {
        log.info("Filter posts: ");
        return ResponseEntity.ok(postService.filter(postFilterDTO, page - 1, size));
    }

    @PostMapping("/public/similar")
    @Operation(summary = "Get similar post", description = "Method used to get posts in the same category")
    public ResponseEntity<List<PostDTO>> similarPosts(@Valid @RequestBody SimilarPostDTO similarPostDTO) {
        return ResponseEntity.ok(postService.getSimilarPosts(similarPostDTO));
    }

}
