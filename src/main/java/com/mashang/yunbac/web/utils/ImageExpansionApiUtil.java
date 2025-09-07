package com.mashang.yunbac.web.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mashang.yunbac.web.entity.params.common.CreateOutPaintingTaskRequest;
import com.mashang.yunbac.web.entity.vo.common.CreateOutPaintingTaskResponse;
import com.mashang.yunbac.web.entity.vo.common.GetOutPaintingTaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 阿里百炼扩图API工具类
 * 提供创建扩图任务和查询任务状态的功能
 */
@Component
@Slf4j
public class ImageExpansionApiUtil {

    private static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";
    private static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    @Value("${aliYunAi.apiKey}")
    private String apiKey;

    /**
     * 创建扩图任务
     *
     * @param createOutPaintingTaskRequest 扩图请求参数
     * @return 扩图任务响应
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        if (createOutPaintingTaskRequest == null) {
            throw new RuntimeException("扩图参数为空");
        }

        HttpRequest httpRequest = HttpRequest.post(CREATE_OUT_PAINTING_TASK_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("X-DashScope-Async", "enable")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(createOutPaintingTaskRequest));

        try (HttpResponse httpResponse = httpRequest.execute()) {
            if (!httpResponse.isOk()) {
                log.error("请求异常: {}", httpResponse.body());
                throw new RuntimeException("AI 扩图失败");
            }
            CreateOutPaintingTaskResponse response = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);
            String errorCode = response.getCode();
            if (StrUtil.isNotBlank(errorCode)) {
                String errorMessage = response.getMessage();
                log.error("AI 扩图失败, errorCode:{}, errorMessage:{}", errorCode, errorMessage);
                throw new RuntimeException("AI 扩图接口响应异常");
            }
            return response;
        } catch (Exception e) {
            log.error("创建任务异常: {}", e.getMessage());
            throw new RuntimeException("创建任务失败");
        }
    }

    /**
     * 查询创建的任务
     *
     * @param taskId 任务ID
     * @return 任务查询响应
     */
    public GetOutPaintingTaskResponse getOutPaintingTask(String taskId) {
        if (StrUtil.isBlank(taskId)) {
            throw new RuntimeException("任务 id 不能为空");
        }

        try (HttpResponse httpResponse = HttpRequest.get(String.format(GET_OUT_PAINTING_TASK_URL, taskId))
                .header("Authorization", "Bearer " + apiKey)
                .execute()) {
            if (!httpResponse.isOk()) {
                throw new RuntimeException("获取任务失败");
            }
            return JSONUtil.toBean(httpResponse.body(), GetOutPaintingTaskResponse.class);
        } catch (Exception e) {
            log.error("查询任务异常: {}", e.getMessage());
            throw new RuntimeException("查询任务失败");
        }
    }
}
