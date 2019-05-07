package com.ssm.demo.controller;

import com.ssm.demo.common.Constants;
import com.ssm.demo.common.Result;
import com.ssm.demo.common.ResultGenerator;
import com.ssm.demo.controller.annotation.TokenToUser;
import com.ssm.demo.entity.AdminUser;
import com.ssm.demo.service.AdminUserService;
import com.ssm.demo.utils.FileUtil;
import com.ssm.demo.utils.PageResult;
import com.ssm.demo.utils.PageUtil;
import com.ssm.demo.utils.PoiUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 13 on 2018/7/4.
 */
@RestController
@RequestMapping("/users")
public class AdminUserController {

    final static Logger logger = Logger.getLogger(AdminUserController.class);

    @Autowired
    private AdminUserService adminUserService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody AdminUser user) {
        logger.info("请求登陆方法，参数为{" + user.toString() + "}");
        Result result = ResultGenerator.genFailResult("登录失败");
        if (StringUtils.isEmpty(user.getUserName()) || StringUtils.isEmpty(user.getPassword())) {
            logger.error("未填写登陆信息，登陆失败");
            result.setMessage("请填写登录信息！");
        }
        AdminUser loginUser = adminUserService.updateTokenAndLogin(user.getUserName(), user.getPassword());
        if (loginUser != null) {
            result = ResultGenerator.genSuccessResult(loginUser);
        }
        logger.info("登陆成功，用户名为：" + user.getUserName());
        return result;
    }

    @RequestMapping(value = "/checktoken/{token}", method = RequestMethod.POST)
    public Result checktoken(@PathVariable String token) {
        Result result = ResultGenerator.genFailResult("登陆失败");
        AdminUser user = adminUserService.getAdminUserByToken(token);
        if (user != null) {
            if (user.getUserName().equals("admin")) {
                result = ResultGenerator.genSuccessResult(200);
            } else {
                result = ResultGenerator.genSuccessResult(300);
            }
        }
        return result;
    }

    @RequestMapping(value = "/list")
    public Result list(@RequestParam Map<String, Object> params) throws Exception {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            logger.error("请求用户列表错误，参数异常！");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }

        PageUtil pageUtil = new PageUtil(params);
        logger.info("请求用户列表成功，参数为 page:" + params.get("page").toString() + ",limit:" + params.get("limit").toString());
        PageResult pageResult = adminUserService.selectUser(pageUtil);
        return ResultGenerator.genSuccessResult(pageResult);
    }


    @RequestMapping("/save")
    public Result insertAdmin(@RequestBody AdminUser user, @TokenToUser AdminUser loginUser) {
        System.out.println("user=" + user);
        System.out.println("loginUser=" + loginUser);
        if (loginUser == null) {
            logger.error("请求添加用户失败，未登录");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "您还未登录！");
        }
        if (StringUtils.isEmpty(user.getUserName()) || StringUtils.isEmpty(user.getPassword())) {
            logger.error("请求添加用户失败，参数异常");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        if ("admin".endsWith(user.getUserName().trim())) {
            logger.error("请求添加用户失败，不能添加admin用户");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "不能添加admin用户！");
        }
        AdminUser selectusername = adminUserService.selectusername(user.getUserName());
        if (selectusername != null) {
            logger.error("请求添加用户失败，用户已存在");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_BAD_REQUEST, "该用户已被使用");
        }
        try {
            adminUserService.insertAdmin(user);
            logger.info("请求添加用户成功 " + user.toString());
            return ResultGenerator.genSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("请求添加用户失败 " + user.toString());
            return ResultGenerator.genFailResult("添加失败");
        }
    }


    @RequestMapping("/updatePassword")
    public Result updatePassword(@RequestBody AdminUser user, @TokenToUser AdminUser loginUser) {
        if (loginUser == null) {
            logger.error("请求修改用户失败，未登录");
            return ResultGenerator.genNullResult("未登录无法修改密码！");
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            logger.error("请求修改用户失败，未输入密码");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_SERVER_ERROR, "请输入密码！");
        }
        AdminUser adminUser = adminUserService.selectUserId(user.getId());
        if (adminUser == null) {
            logger.error("请求修改用户失败，无此用户");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "无此用户！");
        }
        if (adminUser.getUserName().trim().equals("admin")) {
            logger.error("请求修改用户失败，不能修改admin用户");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "不能修改admin用户！");
        }
        try {
            adminUserService.updatePassword(String.valueOf(user.getId()), user.getPassword());
            logger.info("请求修改用户成功 " + user.toString());
            return ResultGenerator.genSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("请求修改用户失败 " + user.toString());
            return ResultGenerator.genFailResult("修改失败！");
        }
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result deleteUser(@RequestBody Integer[] ids, @TokenToUser AdminUser loginUser) {
        if (loginUser == null) {
            logger.error("请求删除用户失败，未登录");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "未登录！");
        }
        if (ids.length < 1) {
            logger.error("请求删除用户失败，参数异常");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == 1) {
                logger.info("请求删除用户Admin失败！");
                return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "不能删除Admin用户！");
            }
        }
        if (adminUserService.deleteUser(ids) > 0) {
            logger.info("请求删除用户成功 " + Arrays.toString(ids));
            return ResultGenerator.genSuccessResult();
        } else {
            logger.error("请求删除用户失败 " + Arrays.toString(ids));
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    /**
     * <p>
     * 批量导入用户(直接导入)
     */
    @RequestMapping(value = "/importV1", method = RequestMethod.POST)
    public Result saveByExcelFileV1(@RequestParam("file") MultipartFile multipartfile) {
        File file = FileUtil.convertMultipartFileToFile(multipartfile);
        if (file == null) {
            logger.error("上传文件为空，importV1导入失败");
            return ResultGenerator.genFailResult("导入失败！");
        }
        int i = adminUserService.importUsersByExcelFile(file);
        if (i > 0) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(i);
            logger.info("importV1用户导入成功");
            return result;
        } else {
            logger.error("上传文件为空，importV1导入失败");
            return ResultGenerator.genFailResult("导入失败");
        }
    }

    /**
     * 批量导入用户V2(通过url的方式来导入)
     */
    @RequestMapping(value = "/importV2", method = RequestMethod.POST)
    public Result saveV2(String fileUrl) {
        if (fileUrl == null) {
            logger.error("fileUrl为空，importV2导入失败");
            return ResultGenerator.genNullResult("导入的数据不能为空");
        }
        //根据url获取文件对象
        File file = FileUtil.downloadFile(fileUrl);
        if (file == null) {
            logger.error("文件不存在，importV2导入失败");
            return ResultGenerator.genNullResult("文件不存在！");
        }
        int i = adminUserService.importUsersByExcelFile(file);
        if (i > 0) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(i);
            logger.info("importV2用户导入成功");
            return result;
        } else {
            logger.error("importV2导入失败");
            return ResultGenerator.genFailResult("导入失败");
        }
    }

    /**
     * 导出功能
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportUsers(HttpServletRequest request, HttpServletResponse response) {
        List<AdminUser> userList = adminUserService.getUsersForExport();
        //单元格表头
        String[] excelHeader = {"用户id", "用户名", "账号状态", "添加时间"};
        //字段名称
        String[] fileds = {"userId", "userName", "status", "createTime"};
        //单元格宽度内容格式
        int[] formats = {4, 2, 1, 1};
        //单元格宽度
        int[] widths = {256 * 14, 512 * 14, 256 * 14, 512 * 14};
        try {
            List<Map<String, Object>> excelData = new ArrayList<Map<String, Object>>();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (CollectionUtils.isNotEmpty(userList)) {
                for (AdminUser user : userList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", user.getId());
                    map.put("userName", user.getUserName());
                    map.put("status", user.getIsDeleted() == 0 ? "正常账号" : "废弃账号");
                    map.put("createTime", formatter.format(user.getCreateTime()));
                    excelData.add(map);
                }
            }
            String excelName = "用户数据_" + System.currentTimeMillis();
            logger.info("用户数据导出");
            PoiUtil.exportFile(excelName, excelHeader, fileds, formats, widths, excelData, request, response);
        } catch (Exception e) {
            logger.error("用户数据导出失败" + e.toString());
            e.printStackTrace();
        }
    }
}

