package api.giybat.uz.service;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.attach.AttachDTO;
import api.giybat.uz.dto.post.*;
import api.giybat.uz.entity.PostEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.CustomRepository;
import api.giybat.uz.repository.PostRepository;
import api.giybat.uz.util.SpringSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ResourceBundleMessageService resourceBundleMessageService;
    @Autowired
    private AttachService attachService;
    @Autowired
    private CustomRepository customRepository;

    public PostDTO create(PostCreateDTO dto, AppLanguage lang) {
        PostEntity post = new PostEntity();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setProfileId(SpringSecurityUtil.getCurrentUserId());
        post.setPhotoId(dto.getPhoto().getPhotoId());
        postRepository.save(post);
        return toDTO(post);
    }


    public Page<PostDTO> getProfilePost(Integer page, Integer size, AppLanguage lang) {

        Pageable pageable = PageRequest.of(page, size);
        String userId = SpringSecurityUtil.getCurrentUserId();
        Page<PostEntity> postEntityList = postRepository.findAllByProfileIdAndVisibleTrue(userId, pageable);
        List<PostDTO> postDTOList = postEntityList
                .stream()
                .map(postEntity -> toDTO(postEntity))
                .collect(Collectors.toList());
        long totalCount = postEntityList.getTotalElements();

        return new PageImpl<>(postDTOList, pageable, totalCount);
    }

    public PostDTO getFullPostDetails(String postId, AppLanguage lang) {
        PostEntity post = getPostById(postId, lang);
        PostDTO postDTO = toDTO(post);
        postDTO.setContent(post.getContent());
        return postDTO;
    }

    public PostDTO updatePost(String postId, PostUpdateDTO postUpdateDTO, AppLanguage lang) {
        PostEntity post = getPostById(postId, lang);
        String deletedPhotoId = null;
        String userId = SpringSecurityUtil.getCurrentUserId();

        //check for admin
        if (!SpringSecurityUtil.hasRole(ProfileRole.ROLE_ADMIN) && !post.getProfileId().equals(userId)) {
            log.warn("User {} trying to update other's post", userId);
            throw new AppBadException(resourceBundleMessageService.getMessage("no.post.update", lang));
        }

        post.setTitle(postUpdateDTO.getTitle());
        post.setContent(postUpdateDTO.getContent());
        if (!post.getPhotoId().equals(postUpdateDTO.getPhoto().getPhotoId())) {
            deletedPhotoId = post.getPhotoId();
        }
        post.setPhotoId(postUpdateDTO.getPhoto().getPhotoId());
        postRepository.save(post);
        if (deletedPhotoId != null) {
            attachService.delete(deletedPhotoId);
        }
        return toDTO(post);
    }

    public Boolean deletePostById(String postId, AppLanguage lang) {
        //receiving profileID of a post
        String profileEntityId = postRepository.getProfileId(postId);

        String userId = SpringSecurityUtil.getCurrentUserId();
        if (!SpringSecurityUtil.hasRole(ProfileRole.ROLE_ADMIN) && !profileEntityId.equals(userId)) {
            log.warn("User {} trying to update other's post", userId);
            throw new AppBadException(resourceBundleMessageService.getMessage("no.post.delete", lang));
        }        postRepository.delete(postId);
        return true;
    }

    public PageImpl<PostDTO> filter(PostFilterDTO filterDTO,int page, int size) {
     FilterResultDTO<PostEntity> result  = customRepository.filter(filterDTO,page,size);
     List<PostDTO> postList = result.getList()
             .stream()
             .map(postEntity -> toDTO(postEntity)).toList();
     return new PageImpl<>(postList,PageRequest.of(page,size),result.getCount());
    }


    //util methods
    private PostDTO toDTO(PostEntity postEntity) {
        //id, title, content, photo{id,url}, createdDate}
        PostDTO dto = new PostDTO();
        dto.setId(postEntity.getId());
        dto.setTitle(postEntity.getTitle());
        dto.setAttachDTO(attachService.attachDTO(postEntity.getPhotoId()));
        dto.setCreatedDate(postEntity.getCreatedDate());
        return dto;

    }

    public PostEntity getPostById(String postId, AppLanguage lang) {
        return postRepository.findByIdAndVisibleTrue(postId)
                .orElseThrow(() -> new AppBadException(resourceBundleMessageService.getMessage("post.not.found", lang)));
    }


}
