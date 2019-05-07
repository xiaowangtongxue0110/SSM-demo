package com.ssm.demo.service.impl;

import com.ssm.demo.common.Constants;
import com.ssm.demo.dao.AdminUserDao;
import com.ssm.demo.entity.AdminUser;
import com.ssm.demo.redis.RedisUtil;
import com.ssm.demo.service.AdminUserService;
import com.ssm.demo.utils.*;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 13 on 2018/7/4.
 */
@Service("adminUserService") //注意这个注解
public class AdminUserServiceImpl implements AdminUserService {

    final static Logger logger = Logger.getLogger(AdminUser.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AdminUserDao adminUserDao;

    @Override
    public AdminUser updateTokenAndLogin(String userName, String password) {

        AdminUser adminUser = adminUserDao.getAdminUserByUserNameAndPassword(userName, MD5Util.MD5Encode(password, "UTF-8"));
        if (adminUser != null) {
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", adminUser.getId());
            if (adminUserDao.updateUserToken(adminUser.getId(), token) > 0) {
                //返回数据时带上token
                adminUser.setUserToken(token);
                return adminUser;
            }
        }
        return null;
    }

    /**
     * 获取token值
     *
     * @param sessionId
     * @param userId
     * @return
     */
    private String getNewToken(String sessionId, Long userId) {
        String src = sessionId + userId + NumberUtil.genRandomNum(4);
        return SystemUtil.genToken(src);
    }

    @Override
    public AdminUser getAdminUserByToken(String userToken) {
        return adminUserDao.getAdminUserByToken(userToken);
    }

    @Override
    public PageResult selectUser(PageUtil pageUtil) {
        int i = adminUserDao.countUser();
        List<AdminUser> adminUsers = adminUserDao.selectUser(pageUtil);
        PageResult pageResult = new PageResult(adminUsers, i, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public void insertAdmin(AdminUser user) {
        user.setPassword(MD5Util.MD5Encode(user.getPassword(), "UTF-8"));
        AdminUser adminUser = (AdminUser) redisUtil.get(Constants.Admin_CACHE_KEY + user.getId(), AdminUser.class);
        if (adminUser!=null){
            logger.info("新增用户成功，将文章数据存储至redis:" + Constants.Admin_CACHE_KEY + user.getId());
        }
        adminUserDao.insertAdmin(user);
    }

    @Override
    public AdminUser selectusername(String username) {
        return adminUserDao.selectusername(username);
    }

    @Override
    public void updatePassword(String id, String password) {
        adminUserDao.updatePassword(id, password);
    }

    @Override
    public AdminUser selectUserId(Long id) {
        return adminUserDao.selectUserId(id);
    }

    @Override
    public int importUsersByExcelFile(File file) {
        XSSFSheet xssfSheet = null;
        //读取File对象并转换成XSSFSheet类型对象进行处理
        try {
            //表格对象
            xssfSheet = PoiUtil.getXSSFSheet(file);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        ArrayList<AdminUser> adminUsers = new ArrayList<>();
        //第一行是表名称，第二行才是数据，所以从第二行开始读取
        for (int i = 1; i <= xssfSheet.getLastRowNum(); i++) {
            //获取Excel表格指定行的对象
            XSSFRow row = xssfSheet.getRow(i);
            if (row != null) {
                AdminUser adminUser = new AdminUser();
                //获取用户名
                XSSFCell userName = row.getCell(0);
                //获取密码
                XSSFCell password = row.getCell(1);

                //设置用户名
                if (!StringUtils.isEmpty(userName)) {
                    adminUser.setUserName(PoiUtil.getValue(userName));
                }
                if (!StringUtils.isEmpty(password)) {
                    adminUser.setPassword(MD5Util.MD5Encode(PoiUtil.getValue(password), "utf-8"));
                }
                //用户验证 已存在或者为空则不进行insert操作
                if (!StringUtils.isEmpty(adminUser.getUserName()) && !StringUtils.isEmpty(adminUser.getPassword()) && selectusername(adminUser.getUserName()) == null) {
                    adminUsers.add(adminUser);
                }
            }
        }
        //判空
        if (!CollectionUtils.isEmpty(adminUsers)) {
            //adminUsers用户列表不为空则执行批量添加sql
            return adminUserDao.addExcel(adminUsers);
        }
        return 0;
    }

    @Override
    public int deleteUser(Object[] ids) {
        return adminUserDao.deleteUser(ids);
    }

    @Override
    public List<AdminUser> getUsersForExport() {
        return adminUserDao.getAllAdminUsers();
    }
}