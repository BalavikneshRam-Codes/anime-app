package com.bu.anime_web.repository;

import com.bu.anime_web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    Optional<User> findByEmailAndIsVerified(String username,Boolean isVerified);
}
