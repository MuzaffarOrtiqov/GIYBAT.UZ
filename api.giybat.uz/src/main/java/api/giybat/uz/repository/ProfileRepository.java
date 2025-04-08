package api.giybat.uz.repository;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByIdAndVisibleTrue(String id);

    @Transactional
    @Modifying
    @Query("UPDATE ProfileEntity AS p SET p.status=?2 WHERE p.id=?1")
    void updateStatus(String profileId, GeneralStatus generalStatus);

    @Query("FROM ProfileEntity AS p WHERE p.username=?1 AND p.visible=TRUE")
    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);


    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity p SET p.password=?1 WHERE p.id=?2")
    void updatePassword(String password, String id);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET name=?1 WHERE id=?2")
    void updateProfileName(String name, String id);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET tempUsername=?1 WHERE id=?2")
    void updateTempUsername(String username, String userId);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET username=?2 WHERE id=?1")
    void updateUsername(String userId, String tempUsername);
}
