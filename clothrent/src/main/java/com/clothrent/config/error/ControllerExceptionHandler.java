package com.clothrent.config.error;

import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class ControllerExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public ResponseBean exceptionHandler(HttpServletRequest request, Exception e) {

        log.error("系统抛出了异常：{}{}",e.getMessage(),e);
        return ResultUtil.error(e.getMessage());
    }
}
