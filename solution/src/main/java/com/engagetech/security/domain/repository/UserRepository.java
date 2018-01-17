package com.engagetech.security.domain.repository;

import com.engagetech.security.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findOneByUsername(String username);
}
