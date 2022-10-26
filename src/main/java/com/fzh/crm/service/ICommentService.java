package com.fzh.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzh.crm.entity.Comment;

import java.util.List;

public interface ICommentService extends IService<Comment> {

    List<Comment> findCommentDetail(Integer articleId);
}
