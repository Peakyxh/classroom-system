package com.ruoyi.classroom.mapper;

import java.util.List;
import com.ruoyi.classroom.domain.SudentHomework;

/**
 * 学生作业Mapper接口
 * 
 * @author rain
 * @date 2023-09-08
 */
public interface SudentHomeworkMapper 
{
    /**
     * 查询学生作业
     * 
     * @param shId 学生作业主键
     * @return 学生作业
     */
    public SudentHomework selectSudentHomeworkByShId(Long shId);

    /**
     * 查询学生作业列表
     * 
     * @param sudentHomework 学生作业
     * @return 学生作业集合
     */
    public List<SudentHomework> selectSudentHomeworkList(SudentHomework sudentHomework);

    /**
     * 新增学生作业
     * 
     * @param sudentHomework 学生作业
     * @return 结果
     */
    public int insertSudentHomework(SudentHomework sudentHomework);

    /**
     * 修改学生作业
     * 
     * @param sudentHomework 学生作业
     * @return 结果
     */
    public int updateSudentHomework(SudentHomework sudentHomework);

    /**
     * 删除学生作业
     * 
     * @param shId 学生作业主键
     * @return 结果
     */
    public int deleteSudentHomeworkByShId(Long shId);

    /**
     * 批量删除学生作业
     * 
     * @param shIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSudentHomeworkByShIds(Long[] shIds);
}
