package com.howdev.jwtauth.repository;

import com.howdev.jwtauth.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色名称查询角色
     */
    Optional<Role> findByName(String name);

    List<Role> findByNameIn(List<String> names);
    
    /**
     * 分页查询角色列表，支持根据名称和状态过滤，并按照指定字段排序
     * @param name 角色名称（可选，为空时不过滤）
     * @param status 角色状态（可选，为空时不过滤）
     * @param pageable 分页和排序信息
     * @return 分页的角色列表
     */
    @Query("SELECT r FROM Role r WHERE " +
           "(:name IS NULL OR r.name LIKE %:name%) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<Role> findByConditions(@Param("name") String name, 
                                @Param("status") String status, 
                                Pageable pageable);
}
