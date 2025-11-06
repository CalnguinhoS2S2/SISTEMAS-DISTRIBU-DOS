package com.sisdistro.tweetapp.repository;

import com.sisdistro.tweetapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para operações de {@link User}.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByHandleIgnoreCase(String handle);

    boolean existsByHandleIgnoreCase(String handle);
}
