package com.ruoyi.classroom.mapper;

import java.util.List;
import com.ruoyi.classroom.domain.Homework;

/**
 * 发布作业Mapper接口
 * 
 * @author rain
 * @date 2023-09-08
 */
public interface HomeworkMapper 
{
    /**
     * 查询发布作业
     * 
     * @param homeworkId 发布作业主键
     * @return 发布作业
     */
    public Homework selectHomeworkByHomeworkId(Long homeworkId);

    /**
     * 查询发布作业列表
     * 
     * @param homework 发布作业
     * @return 发布作业集合
     */
    public List<Homework> selectHomeworkList(Homework homework);

    /**
     * 新增发布作业
     * 
     * @param homework 发布作业
     * @return 结果
     */
    public int insertHomework(Homework homework);

    /**
     * 修改发布作业
     * 
     * @param homework 发布作业
     * @return 结果
     */
    public int updateHomework(Homework homework);

    /**
     * 删除发布作业
     * 
     * @param homeworkId 发布作业主键
     * @return 结果
     */
    public int deleteHomeworkByHomeworkId(Long homeworkId);

    /**
     * 批量删除发布作业
     * 
     * @param homeworkIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHomeworkByHomeworkIds(Long[] homeworkIds);
}
