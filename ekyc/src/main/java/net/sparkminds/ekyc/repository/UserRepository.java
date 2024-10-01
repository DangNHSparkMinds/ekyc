package net.sparkminds.ekyc.repository;

import net.sparkminds.ekyc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByPhoneNumberOrEmail(String phoneNumber, String email);
    Optional<User> findByPhoneNumberOrEmail(String phoneNumber, String email);
}
