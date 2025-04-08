package api.giybat.uz.repository;

import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.enums.ProfileRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProfileRoleRepository extends CrudRepository<ProfileRoleEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ProfileRoleEntity AS pr WHERE pr.profileId=?1")
    void deleteRole(String profileId);

    @Query("FROM ProfileRoleEntity AS pr INNER JOIN ProfileEntity AS p ON pr.profileId=p.id WHERE p.username=?1 AND p.visible=true ")
    ProfileRoleEntity findProfileRolesByUsernameAndVisibleTrue(String username);

    @Query("SELECT p.roles FROM ProfileRoleEntity AS p WHERE p.profileId=?1")
    List<ProfileRole> getAllRoles(String id);
}
