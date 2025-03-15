package ru.vtb.at.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DPMapper {
    List<String> getFailedTestFor(@Param("teamName") String teamName, @Param("jobName") String jobName, @Param("num") Integer runsNum);
}
