package entus.authServer.repository;

import entus.authServer.domain.user.AuthProvider;
import entus.authServer.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String email);
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}