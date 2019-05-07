package com.ssm.demo.service;

import com.ssm.demo.entity.Article;
import com.ssm.demo.utils.PageResult;
import com.ssm.demo.utils.PageUtil;

public interface ArticleService {

    int insertArticle(Article article);

    PageResult getArticlePage(PageUtil pageUtil);

    Article getArticleById(Integer id);

    int updArticle(Article article);

    int deleteBatch(Object[] id);


}
