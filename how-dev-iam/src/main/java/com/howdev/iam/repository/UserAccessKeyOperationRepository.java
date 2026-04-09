package com.howdev.iam.repository;

import com.howdev.iam.entity.UserAccessKeyOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户访问密钥操作流水数据访问层
 */
@Repository
public interface UserAccessKeyOperationRepository extends JpaRepository<UserAccessKeyOperation, Long> {

    List<UserAccessKeyOperation> findByUserId(String userId);

}