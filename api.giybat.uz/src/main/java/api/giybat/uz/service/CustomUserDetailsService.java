package api.giybat.uz.service;

import api.giybat.uz.config.CustomUserDetails;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        ProfileEntity profile = optional.get();
        CustomUserDetails userDetails = new CustomUserDetails(profile,profileRoleRepository.getAllRoles(profile.getId()));
        return userDetails;
    }
}
