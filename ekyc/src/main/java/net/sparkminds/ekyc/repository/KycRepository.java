package net.sparkminds.ekyc.repository;

import net.sparkminds.ekyc.entity.Kyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycRepository extends JpaRepository<Kyc, Long> {
    Optional<Kyc> findByUserId(Long id);
}
