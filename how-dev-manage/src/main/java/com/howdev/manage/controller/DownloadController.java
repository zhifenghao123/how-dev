package com.howdev.manage.controller;

import com.howdev.manage.annotation.ApiException;
import com.howdev.manage.enumeration.RetCodeEnum;
import com.howdev.manage.exception.RetCodeException;
import com.howdev.manage.vo.DownLoadParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * DownloadController class
 *
 * @author haozhifeng
 * @date 2023/12/07
 */
@Controller
@RequestMapping("download")
public class DownloadController {

    @RequestMapping(value = "downloadResult")
    public void downloadResult(@RequestBody DownLoadParam downLoadParam, HttpServletResponse response) {
        throw new RetCodeException(RetCodeEnum.FAILED, "失败");
    }
}
