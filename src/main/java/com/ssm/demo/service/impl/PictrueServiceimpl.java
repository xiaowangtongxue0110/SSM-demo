package com.ssm.demo.service.impl;

import com.ssm.demo.common.Constants;
import com.ssm.demo.dao.PictureDao;
import com.ssm.demo.entity.Picture;
import com.ssm.demo.redis.RedisUtil;
import com.ssm.demo.service.PictureService;
import com.ssm.demo.utils.PageResult;
import com.ssm.demo.utils.PageUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictrueServiceimpl implements PictureService {

    final static Logger logger = Logger.getLogger(PictrueServiceimpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PictureDao pictureDao;

    @Override
    public PageResult getPicturePage(PageUtil pageUtil) {
        List<Picture> pictures = pictureDao.findPictures(pageUtil);  //获取对应页数的全部数据
        int totalPictures = pictureDao.getTotalPictures(pageUtil);   //获取数据的总条数
        return new PageResult(pictures, totalPictures, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public Picture queryObject(Integer id) {
        //检测redis缓存中是否存储过对应id的数据
        Picture picture = (Picture) redisUtil.get(Constants.Picture_CACHE_KEY + id, Picture.class);
        if (picture != null) {
            //如果存储过，就直接从redis缓存中读取
            logger.info("redis缓存中已经存在此数据，从redis中获取"+Constants.Picture_CACHE_KEY+id);
            return picture;
        }else {
            Picture pictureById = pictureDao.findPictureById(id);
            logger.info("redis缓存中没有此数据，存入redis缓存中"+Constants.Picture_CACHE_KEY+id);
            redisUtil.put(Constants.Picture_CACHE_KEY+id,pictureById);
            return pictureById;
        }
    }

    @Override
    public int save(Picture picture) {
        logger.info("将新增的图片放入redis缓存");
        int i = pictureDao.insertPicture(picture);
        redisUtil.put(Constants.Picture_CACHE_KEY+picture.getId(),picture);
         return i;
    }

    @Override
    public int update(Picture picture) {
        logger.info("图片修改成功，更新redis中的图片数据："+Constants.ARTICLE_CACHE_KEY+picture.getId());
        redisUtil.del(Constants.Picture_CACHE_KEY+picture.getId());
        redisUtil.put(Constants.Picture_CACHE_KEY+picture.getId(),picture);
        return pictureDao.updPicture(picture);
    }

    @Override
    public int delete(Integer id) {
        logger.info("删除redis缓存中的图片"+Constants.Picture_CACHE_KEY+id);
        redisUtil.del(Constants.Picture_CACHE_KEY+id);
        return pictureDao.delPicture(id);
    }

    @Override
    public int deleteBatch(Integer[] ids) {
         if (pictureDao.deleteBatch(ids)>0){
             logger.info("批量删除redis缓存中的图片"+Constants.Picture_CACHE_KEY);
             for (int i = 0; i < ids.length; i++) {
                   redisUtil.del(Constants.Picture_CACHE_KEY+ids[i]);
             }
         }
        return pictureDao.deleteBatch(ids);
    }
}
