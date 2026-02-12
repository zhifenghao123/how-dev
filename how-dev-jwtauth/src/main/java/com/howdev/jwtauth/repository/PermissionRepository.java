package com.howdev.jwtauth.repository;

import com.howdev.jwtauth.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String url);
    Optional<Permission> findByUrl(String url);

    /**
     * 分页查询权限列表，支持根据url和name过滤，并按照指定字段排序
     * @param url 权限URL（可选，为空时不过滤）
     * @param name 权限名称（可选，为空时不过滤）
     * @param pageable 分页和排序信息
     * @return 分页的权限列表
     */
    @Query("SELECT p FROM Permission p WHERE " +
           "(:url IS NULL OR p.url LIKE %:url%) AND " +
           "(:name IS NULL OR p.name LIKE %:name%)")
    Page<Permission> findByConditions(@Param("url") String url, 
                                     @Param("name") String name, 
                                     Pageable pageable);

    List<Permission> findByNameIn(List<String> names);
}
