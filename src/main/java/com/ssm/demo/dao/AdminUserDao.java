package com.ssm.demo.dao;

import com.ssm.demo.entity.AdminUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Created by 13 on 2018/7/4.
 */
public interface AdminUserDao {

    /**
     * 根据登录名和密码获取用户记录
     *
     * @return
     */
    AdminUser getAdminUserByUserNameAndPassword(@Param("userName") String userName, @Param("passwordMD5") String passwordMD5);

    /**
     * 根据userToken获取用户记录
     *
     * @return
     */
    AdminUser getAdminUserByToken(@Param("userToken") String userToken);

    /**
     * 更新用户token值
     *
     * @param userId
     * @param newToken
     * @return
     */
    int updateUserToken(@Param("userId") Long userId, @Param("newToken") String newToken);


    List<AdminUser> selectUser(Map param);


    int countUser();


    void insertAdmin(AdminUser user);

    AdminUser selectusername(String username);

    void updatePassword(@Param("id") String id,@Param("password") String password);

    AdminUser selectUserId(Long id);

    int addExcel(@RequestParam("adminUsers") List<AdminUser> adminUsers);

    int deleteUser(Object[] ids);

    List<AdminUser> getAllAdminUsers();
}
