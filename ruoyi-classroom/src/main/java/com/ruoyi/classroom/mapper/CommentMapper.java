package com.ruoyi.classroom.mapper;

import java.util.List;
import com.ruoyi.classroom.domain.Comment;

/**
 * 评论管理Mapper接口
 * 
 * @author Yuan
 * @date 2023-09-08
 */
public interface CommentMapper 
{
    /**
     * 查询评论管理
     * 
     * @param commentId 评论管理主键
     * @return 评论管理
     */
    public Comment selectCommentByCommentId(Long commentId);

    /**
     * 查询评论管理列表
     * 
     * @param comment 评论管理
     * @return 评论管理集合
     */
    public List<Comment> selectCommentList(Comment comment);

    /**
     * 新增评论管理
     * 
     * @param comment 评论管理
     * @return 结果
     */
    public int insertComment(Comment comment);

    /**
     * 修改评论管理
     * 
     * @param comment 评论管理
     * @return 结果
     */
    public int updateComment(Comment comment);

    /**
     * 删除评论管理
     * 
     * @param commentId 评论管理主键
     * @return 结果
     */
    public int deleteCommentByCommentId(Long commentId);

    /**
     * 批量删除评论管理
     * 
     * @param commentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCommentByCommentIds(Long[] commentIds);
}
