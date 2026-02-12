package com.howdev.jwtauth.repository;

import com.howdev.jwtauth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserId(String userId);

    List<UserRole> findByUserIdAndRoleNameIn(String userId, List<String> roleName);

}
