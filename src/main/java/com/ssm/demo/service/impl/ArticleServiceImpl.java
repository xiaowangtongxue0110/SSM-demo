package com.ssm.demo.service.impl;

import com.ssm.demo.common.Constants;
import com.ssm.demo.dao.ArticleDao;
import com.ssm.demo.entity.Article;
import com.ssm.demo.redis.RedisUtil;
import com.ssm.demo.service.ArticleService;
import com.ssm.demo.utils.PageResult;
import com.ssm.demo.utils.PageUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    final static Logger logger = Logger.getLogger(ArticleServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ArticleDao articleDao;

    @Override
    public int insertArticle(Article article) {
        article.setUpdateTime(new Date());
        String pu = Constants.ARTICLE_CACHE_KEY + "pageLimit";
        if (articleDao.insertArticle(article) > 0) {
            redisUtil.put(Constants.ARTICLE_CACHE_KEY + article.getId(), article);
            logger.info("新增文章成功，将文章数据存储至redis:" + Constants.ARTICLE_CACHE_KEY + article.getId());
            redisUtil.del(pu);
            return 1;
        }
        return 0;
    }

    @Override
    public PageResult getArticlePage(PageUtil pageUtil) {
            List<Article> articles = articleDao.findArticles(pageUtil);
            int i = articleDao.countArticles(pageUtil);
            PageResult pageResult = new PageResult(articles, i, pageUtil.getLimit(), pageUtil.getPage());
            return pageResult;
    }

    @Override
    public Article getArticleById(Integer id) {
        logger.info("根据id获取文章数据:" + id);
        Article article = (Article) redisUtil.get(Constants.ARTICLE_CACHE_KEY + id, Article.class);
        if (article != null) {
            logger.info("文章数据已存在于redis中直接读取:" + Constants.ARTICLE_CACHE_KEY + id);
            return article;
        }
        Article articleById = articleDao.getArticleById(id);
        if (articleById != null) {
            logger.info("redis中无此文章的数据,从MySQL数据库中读取文章并存储至redis中:" + Constants.ARTICLE_CACHE_KEY + id);
            redisUtil.put(Constants.ARTICLE_CACHE_KEY + articleById.getId(), articleById);
            return articleById;
        }
        return null;
    }

    @Override
    public int updArticle(Article article) {
        article.setUpdateTime(new Date());
        logger.info("文章修改成功，更新redis中的文章数据：" + Constants.ARTICLE_CACHE_KEY + article.getArticleTitle());
        redisUtil.del(Constants.ARTICLE_CACHE_KEY + article.getId());
        redisUtil.put(Constants.ARTICLE_CACHE_KEY + article.getId(), article);
        return articleDao.updArticle(article);
    }

    @Override
    public int deleteBatch(Object[] ids) {
        if (articleDao.deleteBatch(ids) > 0) {
            for (int i = 0; i < ids.length; i++) {
                redisUtil.del(Constants.ARTICLE_CACHE_KEY + ids[i]);
                logger.info("删除redis中的缓存");
                return 1;
            }
        }
        return 0;
    }
}
