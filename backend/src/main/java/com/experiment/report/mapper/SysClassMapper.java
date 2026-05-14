package com.experiment.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.experiment.report.entity.SysClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysClassMapper extends BaseMapper<SysClass> {

    @Select("SELECT c.*, g.grade_name, u.real_name as teacher_name " +
            "FROM sys_class c " +
            "LEFT JOIN sys_grade g ON c.grade_id = g.id " +
            "LEFT JOIN sys_user u ON c.teacher_id = u.id " +
            "WHERE c.deleted = 0 " +
            "ORDER BY g.sort_order, c.class_name")
    IPage<SysClass> selectClassPage(Page<SysClass> page);
}
