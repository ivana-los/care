package com.example.myportfolio.repository;

import com.example.myportfolio.Family;
import com.example.myportfolio.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByPairingCode(String code);
    List<User> findByFamilyId(Long familyId);
}

