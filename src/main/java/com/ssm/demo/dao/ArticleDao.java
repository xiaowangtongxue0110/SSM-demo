package com.ssm.demo.dao;

import com.ssm.demo.entity.Article;

import java.util.List;
import java.util.Map;

public interface ArticleDao {

        int insertArticle(Article article);

        List<Article> findArticles(Map<String,Object> map);

        int countArticles(Map<String,Object> map);

        int updArticle(Article article);

        int delArticle(Integer id);

        Article getArticleById(Integer id);

        /**
         * 批量删除
         *
         * @param id
         * @return
         */
        int deleteBatch(Object[] id);

}
