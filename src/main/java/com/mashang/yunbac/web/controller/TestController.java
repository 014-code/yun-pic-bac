package com.mashang.yunbac.web.controller;

import com.mashang.yunbac.web.utils.ResultTUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    @ApiOperation("测试1")
    public ResultTUtil<String> test1() {
        return new ResultTUtil<String>().success("查询");
    }
}
