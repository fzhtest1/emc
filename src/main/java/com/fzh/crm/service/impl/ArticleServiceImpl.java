package com.fzh.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzh.crm.entity.Article;
import com.fzh.crm.mapper.ArticleMapper;
import com.fzh.crm.service.IArticleService;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

}
