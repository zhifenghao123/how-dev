package com.howdev.iam.repository;

import com.howdev.iam.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(String userId);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 分页查询用户列表，支持根据用户名和状态过滤，并按照指定字段排序
     * @param name 用户名（可选，为空时不过滤）
     * @param status 用户状态（可选，为空时不过滤）
     * @param pageable 分页和排序信息
     * @return 分页的用户列表
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR u.name LIKE %:name%) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> findByConditions(@Param("name") String name, 
                               @Param("status") String status, 
                               Pageable pageable);
    
}