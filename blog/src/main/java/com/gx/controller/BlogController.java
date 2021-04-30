package com.gx.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.common.lang.Result;
import com.gx.entity.Blog;
import com.gx.service.BlogService;
import com.gx.util.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：gx
 * @since 2021-01-01
 */
@RestController
public class BlogController {

    @Autowired
    BlogService blogService;
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient clint;

    private static String searchtext;
    @GetMapping("/blogs")

    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {

        Page page = new Page(currentPage, 5);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.succ(pageData);
    }

    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable(name = "id") Long id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "该博客已被删除");

        return Result.succ(blog);
    }
    @GetMapping("/blog/search/{text}")
    public Result search(@PathVariable(name = "text") String text,@RequestParam(defaultValue = "1") Integer currentPage) throws IOException {

        if (!text.equals("undefined")){
            System.out.println(1);
        searchtext=text;}

        SearchRequest request = new SearchRequest("blog");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置了高亮
        sourceBuilder.highlighter();
        //term name为cyx1的
        CollapseBuilder collapseBuilder = new CollapseBuilder("concat_field");
        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("title", "*"+searchtext+"*");
        sourceBuilder.query(queryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(sourceBuilder);
        SearchResponse response = clint.search(request, RequestOptions.DEFAULT);

        JSONObject jsonObject = JSONObject.parseObject(response.toString());

        int size=jsonObject.getJSONObject("hits").getJSONArray("hits").size();
        ArrayList<String>list=new ArrayList<String>();
        StringBuffer blogs=new StringBuffer();
        for (int i = 0; i < size; i++) {
            String r = jsonObject.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_source");
            list.add(r);
        }
        int pages=0;
        if (size%5==0){
            pages=size/5;
        }else {
           pages=size/5+1;
        }
        if (list.size()>currentPage*5){
        blogs.append(list.get(currentPage*5-5));
            blogs.append(",");
        blogs.append(list.get(currentPage*5-4));
            blogs.append(",");
        blogs.append(list.get(currentPage*5-3));
            blogs.append(",");
        blogs.append(list.get(currentPage*5-2));
            blogs.append(",");
        blogs.append(list.get(currentPage*5-1));}
        else if (currentPage==1&&list.size()<currentPage*5){
            for (int i = 0; i < list.size(); i++) {
                blogs.append(list.get(i));
                if (!(i ==list.size()-1)){
                    blogs.append(",");
                }

            }
        }
        else {
            int endpage=size-(currentPage-1)*5;
            for (int i = (currentPage-1)*5; i < (currentPage-1)*5+endpage; i++) {
                blogs.append(list.get(i));
                if (!(i ==(currentPage-1)*5+endpage-1)){
                    blogs.append(",");
                }
            }
        }
        String records= "{ \"records\":["+blogs+"],  \"total\": "+String.valueOf(size)+",\n" +
                "        \"size\": 5,\n" +
                "        \"current\": "+currentPage.toString()+",\n" +
                "        \"orders\": [],\n" +
                "        \"searchCount\": true,\n" +
                "        \"pages\": "+pages+"}";

        JSONObject json = JSONObject.parseObject(records);
        System.out.println(json );
        return Result.succ(json);


    }

    @RequiresAuthentication
    @PostMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog) {

//        Assert.isTrue(false, "公开版不能任意编辑！");

        Blog temp = null;
        if(blog.getId() != null) {
            temp = blogService.getById(blog.getId());
            // 只能编辑自己的文章
            System.out.println(ShiroUtil.getProfile().getId());
            Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(), "没有权限编辑");

        } else {
            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);
        }

        BeanUtil.copyProperties(blog, temp, "id", "userId", "created", "status");
        System.out.println(temp);
        blogService.saveOrUpdate(temp);

        return Result.succ(null);
    }
    @RequiresAuthentication
    @PostMapping(value = "/blog/del")
    public Result del(@Validated @RequestBody String blogid) {

//        Assert.isTrue(false, "公开版不能任意删除！");
        System.out.println("删除");
        Blog temp = null;
        boolean status = false;
        System.out.println(blogid);
        JSONObject json= JSON.parseObject(blogid);
        int id=Integer.valueOf(json.getByte("blogid"));
            temp = blogService.getById(id);
            status=blogService.removeById(id);

            Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(), "没有权限删除");

        if (status){
            return Result.succ(null);
        }else
        {
            return Result.fail("没这数据");
        }

    }

}

