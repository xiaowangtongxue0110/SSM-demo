package com.ssm.demo.controller;

import com.ssm.demo.common.Constants;
import com.ssm.demo.common.Result;
import com.ssm.demo.common.ResultGenerator;
import com.ssm.demo.controller.annotation.TokenToUser;
import com.ssm.demo.entity.AdminUser;
import com.ssm.demo.entity.Article;
import com.ssm.demo.service.ArticleService;
import com.ssm.demo.utils.PageResult;
import com.ssm.demo.utils.PageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/articles")
public class ArticleController {

            @Autowired
            private ArticleService articleService;


    /**
     * 添加
     * @param article
     * @param loginUser
     * @return
     */
    @RequestMapping("/save")
    public Result addArticle(@RequestBody Article article, @TokenToUser AdminUser loginUser){
        if (loginUser == null) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "未登录！");
        }
        if (articleService.insertArticle(article) > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("添加失败");
        }
    }

    /**
     * 列表
     * @param params
     * @return
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        PageUtil pageUtil = new PageUtil(params);
        PageResult articlePage = articleService.getArticlePage(pageUtil);
        return ResultGenerator.genSuccessResult(articlePage);
    }

    /**
     * 详情
     * @param id
     * @return
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Integer id){
        return ResultGenerator.genSuccessResult(articleService.getArticleById(id));
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Article article,@TokenToUser AdminUser loginUser){
                if (loginUser==null){
                    return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "未登录！");
                }
        if (articleService.updArticle(article) > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Object[] ids, @TokenToUser AdminUser loginUser){
        if (loginUser == null) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "未登录！");
        }
        if (ids.length < 1) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        if (articleService.deleteBatch(ids) > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    /**
     * 搜索功能的实现
     */

    @RequestMapping("/search")
    public Result search(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        if (!StringUtils.isEmpty(params.get("keyword")) && params.get("keyword").toString().length() > 20) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "关键字长度不能大于20！");
        }
        PageUtil pageUtil = new PageUtil(params);
        PageResult articlePage = articleService.getArticlePage(pageUtil);
        return ResultGenerator.genSuccessResult(articlePage);
    }


}
