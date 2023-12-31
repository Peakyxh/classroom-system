package com.ruoyi.classroom.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ruoyi.classroom.domain.*;
import com.ruoyi.classroom.domain.vo.*;
import com.ruoyi.classroom.mapper.*;
import com.ruoyi.classroom.utils.ClassRoomConstants;
import com.ruoyi.classroom.utils.RandomStringGenerator;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.classroom.service.ICourseService;
import org.springframework.util.ObjectUtils;

/**
 * 课程管理Service业务层处理
 *
 * @author Yuan
 * @date 2023-09-08
 */
@Service
public class CourseServiceImpl implements ICourseService
{
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseChapterMapper courseChapterMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private ChapterContentMapper chapterContentMapper;

    @Autowired
    private CourseUserMapper courseUserMapper;
    @Autowired
    private TopicMapper topicMapper;
    @Autowired
    private UserTopicMapper userTopicMapper;
    @Autowired
    private CommentContentMapper commentContentMapper;
    @Autowired
    private  CommentMapper commentMapper;
    @Autowired
    private LikesMapper likesMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 查询课程管理
     *
     * @param courseId 课程管理主键
     * @return 课程管理
     */
    @Override
    public Course selectCourseByCourseId(Long courseId)
    {
        return courseMapper.selectCourseByCourseId(courseId);
    }

    /**
     * 查询课程管理列表
     *
     * @param course 课程管理
     * @return 课程管理
     */
    @Override
    public List<Course> selectCourseList(Course course)
    {
        return courseMapper.selectCourseList(course);
    }

    /**
     * 新增课程管理
     *
     * @param course 课程管理
     * @return 结果
     */
    @Override
    public int insertCourse(Course course)
    {
        //设置课程码
        course.setCode(RandomStringGenerator.generateRandomString(6));
        //设置创建者
        course.setCreateBy(SecurityUtils.getUsername());
        course.setCreateTime(DateUtils.getNowDate());
        //设置状态
        course.setStatus("0");

        int insertCourse = courseMapper.insertCourse(course);

        //添加课程和用户之间的关系
        CourseUser courseUser = new CourseUser();
        courseUser.setUserId(SecurityUtils.getUserId());
        courseUser.setCourseId((long) insertCourse);
        courseUser.setType(ClassRoomConstants.COURSE_TEACH);
        courseUserMapper.insertCourseUser(courseUser);

        return insertCourse;
    }

    /**
     * 修改课程管理
     *
     * @param course 课程管理
     * @return 结果
     */
    @Override
    public int updateCourse(Course course)
    {
        course.setUpdateTime(DateUtils.getNowDate());
        return courseMapper.updateCourse(course);
    }

    /**
     * 批量删除课程管理
     *
     * @param courseIds 需要删除的课程管理主键
     * @return 结果
     */
    @Override
    public int deleteCourseByCourseIds(Long[] courseIds)
    {
        return courseMapper.deleteCourseByCourseIds(courseIds);
    }

    /**
     * 删除课程管理信息
     *
     * @param courseId 课程管理主键
     * @return 结果
     */
    @Override
    public int deleteCourseByCourseId(Long courseId)
    {
        return courseMapper.deleteCourseByCourseId(courseId);
    }

    /**
     * 得到课程相关信息
     * @param courseId
     * @return
     */
    @Override
    public CourseContentVo getCourseContentInfo(Long courseId) {
        // 查询课程和章节的关系
        List<CourseChapter> courseChapters = courseChapterMapper.selectChaptersByCourserId(courseId);
        CourseContentVo courseContentVo = new CourseContentVo();
        courseContentVo.setChapterNumber(courseChapters.size());

        // 获得课程总学时数
        Integer courseHour = courseMapper.selectCreditHoursByCourseId(courseId);
        courseContentVo.setCreditHours(courseHour);

        // 获取已经分配的学时数和课程中的活动数
        AtomicInteger allocated = new AtomicInteger(0);
        AtomicInteger activeNumber = new AtomicInteger(0);
        courseChapters.stream().forEach(courseChapter -> {
            // 已经分配的学时数
            Long chapterId = courseChapter.getChapterId();
            Integer chapterHour = chapterMapper.selectCreditHoursByCourseId(chapterId);
            if(!ObjectUtils.isEmpty(chapterHour)){
                allocated.addAndGet(chapterHour);
            }

            // 课程中的活动数
            List<ChapterContent> chapterContents = chapterContentMapper.selectByChapterId(chapterId);
            if(chapterContents.size() >= 0){
                activeNumber.addAndGet(chapterContents.size());
            }
        });
        courseContentVo.setAllocatedHours(allocated.get());

        courseContentVo.setActiveNumber(activeNumber.get());
        return courseContentVo;
    }

    /**
     * 得到用户教的课程
     * @param userId
     * @return
     */
    @Override
    public Map<String, List<Course>> getUserTeachCourse(Long userId, String type) {
        List<CourseUser> courseUsers = courseUserMapper.selectByUserId(userId, type);
        List<CourseTypeVo> courseTypeVos = new ArrayList<>();

        List<Course> courseList = new ArrayList<>();
        courseUsers.forEach(courseUser -> {
            Course course = courseMapper.selectCourseByCourseId(courseUser.getCourseId());
            if(course != null){
                courseList.add(course);
            }
        });

        Map<String, List<Course>> groupedCourses = courseList.stream().collect(Collectors.groupingBy(
                        c -> c.getSchoolYear() + " " + c.getTerm()));

        return groupedCourses;
    }

    /**
     * 加入课程
     * @param code
     * @return
     */
    @Override
    public int joinCourse(String code) {
//        Long courseId  = courseMapper.selectCourseIdByCode(code);
        Course course = courseMapper.selectCourseByCode(code);
        if(course == null){
            return -1;
        }
        if("1".equals(course.getIsOver())){
            return -2;
        }
        Long courseId = course.getCourseId();
        if(courseId != null){
            Long userId = SecurityUtils.getUserId();
            CourseUser courseUser = new CourseUser();
            courseUser.setCourseId(courseId);
            courseUser.setUserId(userId);
            courseUser.setType(ClassRoomConstants.COURSE_STUDY);

            // 给所加入的课程人数加1
            courseMapper.updateJoinNumber(courseId);
            //给新加入课程用户与话题建立关系
            List<CourseChapter> courseChapters=courseChapterMapper.findChapterByCourseById(courseId);
            courseChapters.forEach((courseChapter -> {
                List<Topic> topics=topicMapper.findTopicByChapterId(courseChapter.getChapterId());

                topics.forEach(topic -> {
                    //用户和话题建立关系
                    UserTopic userTopic = new UserTopic();
                    userTopic.setTopicId(topic.getTopicId());
                    userTopic.setUserId(userId);
                    userTopic.setCreateTime(DateUtils.getNowDate());
                    userTopic.setCreateBy(SecurityUtils.getUsername());
                    userTopicMapper.insertUserTopic(userTopic);
                    //用户和评论建立关系
                    Long []commentIds=commentContentMapper.selectCommentIdByContentId(topic.getTopicId());
                    for (int i = 0; i <commentIds.length ; i++) {
                        Likes likes=new Likes();
                        likes.setType("0");
                        likes.setCommentId(commentIds[i]);
                        likes.setUserId(userId);
                        likesMapper.insertLikes(likes);
                    }
                });
                ;
            }));
            return courseUserMapper.insertCourseUser(courseUser);
        }
        return -1;
    }

    /**
     * 根据用户查询课程
     * @param userId
     * @return
     */
    @Override
    public List<CourseVo> listByUserId(Long userId) {
        List<CourseUser> courseUsers = courseUserMapper.selectCourseUserByUserId(userId);
        List<CourseVo> courseVoList = new ArrayList<>();
        courseUsers.forEach(courseUser -> {
            CourseVo courseVo = new CourseVo();
            Course course = courseMapper.selectCourseByCourseIdTop(courseUser.getCourseId());
            if(course != null){
                BeanUtils.copyProperties(course, courseVo);
                courseVo.setCourseType(courseUser.getType());
                courseVoList.add(courseVo);
            }
        });
        return courseVoList;
    }

    /**
     * 重置课程码
     * @param courseId
     * @return
     */
    @Override
    public int renewCourseCode(Long courseId) {
        String newCode = RandomStringGenerator.generateRandomString(6);
        return courseMapper.renewCourseCode(courseId, newCode);
    }

    /**
     * 结课
     * @param courseId
     * @return
     */
    @Override
    public int overCourse(Long courseId) {
        return courseMapper.overCourse(courseId);
    }

    /**
     * 取消结课
     * @param courseId
     * @return
     */
    @Override
    public int openCourse(Long courseId) {
        return courseMapper.openCourse(courseId);
    }

    /**
     * 置顶课程或取消置顶
     * @param courseId
     * @return
     */
    @Override
    public int cancelTopCourse(Long courseId, String isTop) {
        if ("1".equals(isTop)){
            courseMapper.updateCourseTop(courseId, "0");
        }else {
            courseMapper.updateCourseTop(courseId, "1");
        }
        return 1;
    }

    /**
     * 退课
     * @param courseId
     * @return
     */
    @Override
    public int exitCourse(Long courseId) {
        Long userId = SecurityUtils.getUserId();
        return courseUserMapper.exitCourse(courseId, userId);
    }

    /**
     * 查询课程成员
     * @param courseId
     * @return
     */
    @Override
    public CourseUserVo getMemberInfo(Long courseId) {
        List<CourseUser> userCountByCourse = courseUserMapper.findUserCountByCourse(courseId);
        List<CourseTeacherVo> courseTeacherVos = new ArrayList<>();
        List<CourseStudentVo> courseStudentVos = new ArrayList<>();
        userCountByCourse.stream().forEach(courseUser -> {
            if(ClassRoomConstants.COURSE_TEACH.equals(courseUser.getType())){
                CourseTeacherVo courseTeacherVo = new CourseTeacherVo();
                SysUser sysUser = sysUserMapper.selectUserById(courseUser.getUserId());
                courseTeacherVo.setUserName(sysUser.getUserName());
                courseTeacherVo.setEmail(sysUser.getEmail());
                courseTeacherVo.setPhonenumber(sysUser.getPhonenumber());
                courseTeacherVos.add(courseTeacherVo);
            } else if (ClassRoomConstants.COURSE_STUDY.equals(courseUser.getType())) {
                CourseTeacherVo courseTeacherVo = new CourseTeacherVo();
                SysUser sysUser = sysUserMapper.selectUserById(courseUser.getUserId());
                courseTeacherVo.setUserName(sysUser.getUserName());
                courseTeacherVo.setEmail(sysUser.getEmail());
                courseTeacherVo.setPhonenumber(sysUser.getPhonenumber());
                courseTeacherVos.add(courseTeacherVo);
            } else {
                CourseStudentVo courseStudentVo = new CourseStudentVo();
                SysUser sysUser = sysUserMapper.selectUserById(courseUser.getUserId());
                courseStudentVo.setUserName(sysUser.getUserName());
                courseStudentVo.setEmail(sysUser.getEmail());
                courseStudentVo.setPhonenumber(sysUser.getPhonenumber());
                courseStudentVos.add(courseStudentVo);
            }
        });
        CourseUserVo courseUserVo = new CourseUserVo();
        courseUserVo.setCourseTeacherVos(courseTeacherVos);
        courseUserVo.setCourseStudentVos(courseStudentVos);

        return courseUserVo;
    }
}
