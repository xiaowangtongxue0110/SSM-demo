package com.ssm.demo.service;

import com.ssm.demo.entity.AdminUser;
import com.ssm.demo.utils.PageResult;
import com.ssm.demo.utils.PageUtil;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 * Created by 13 on 2018/7/4.
 */
public interface AdminUserService {
    /**
     * 登陆功能
     *
     * @return
     */
    AdminUser updateTokenAndLogin(String userName, String password);

    /**
     * 根据userToken获取用户记录
     *
     * @return
     */
    AdminUser getAdminUserByToken(String userToken);


    PageResult selectUser(PageUtil pageUtil);

    void insertAdmin(AdminUser user);


    AdminUser selectusername(String username);


    void updatePassword(String id,String password);

    AdminUser selectUserId(Long id);


    /**
     * 根据excel导入用户记录
     *
     * @param file
     * @return
     */
    int importUsersByExcelFile(File file);


    int deleteUser(Object[] ids);

    /**
     * 获取导出数据
     *
     * @return
     */
    List<AdminUser> getUsersForExport();

}
