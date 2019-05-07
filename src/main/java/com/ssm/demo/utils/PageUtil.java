package com.ssm.demo.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * param 获取传进来的Map，然后通过page和limit计算出每次访问时的起始数据
 * 再把获取的值都put到this类(此类继承了Map接口所以自动上升成map)
 */
public class PageUtil extends LinkedHashMap<String,Object> {

          private int page;
          private int limit;


          public PageUtil(Map<String,Object> params){

              this.page = Integer.parseInt(params.get("page").toString());
              this.limit = Integer.parseInt(params.get("limit").toString());

              this.put("start",(page-1)*limit);
              this.put("page",page);
              this.put("limit",limit);
              this.put("keyword",params.get("keyword"));
          }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
