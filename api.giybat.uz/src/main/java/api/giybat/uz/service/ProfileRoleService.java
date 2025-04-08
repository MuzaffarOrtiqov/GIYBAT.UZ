package api.giybat.uz.service;

import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileRoleService {
    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    public void create (String id, ProfileRole role) {
        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setProfileId(id);
        profileRoleEntity.setRoles(role);
        profileRoleRepository.save(profileRoleEntity);
    }

    public void deleteRoles(String profileId) {
        profileRoleRepository.deleteRole(profileId);
    }


}
