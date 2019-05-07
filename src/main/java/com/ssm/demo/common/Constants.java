package com.ssm.demo.common;

/**
 * Created by 13 on 2017/6/26.
 */
public class Constants {

    public static final int RESULT_CODE_SUCCESS = 200;  // 成功处理请求
    public static final int RESULT_CODE_BAD_REQUEST = 412;  // 请求错误
    public static final int RESULT_CODE_NOT_LOGIN = 402;  // 未登录
    public static final int RESULT_CODE_PARAM_ERROR = 406;  // 传参错误
    public static final int RESULT_CODE_SERVER_ERROR = 500;  // 服务器错误

    public final static int PAGE_SIZE = 10;//默认分页条数

    public final static String FILE_PRE_URL = "http://localhost:8080";//上传文件的默认url前缀，根据部署设置自行修改

    public static final String ARTICLE_CACHE_KEY = "ssm-demo:article:";//文章存储于redis的key前缀

    public static final String Picture_CACHE_KEY = "ssm-demo:picture";//图片储存于redis的key前缀

    public static final String Admin_CACHE_KEY = "ssm-demo:admin";//图片储存于redis的key前缀



}
