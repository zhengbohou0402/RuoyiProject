package com.icm.manage.domain.vo;

import com.icm.manage.domain.Task;
import com.icm.manage.domain.TaskType;
import lombok.Data;

@Data
public class TaskVo extends Task {

    // 工单类型
    private TaskType taskType;
}
