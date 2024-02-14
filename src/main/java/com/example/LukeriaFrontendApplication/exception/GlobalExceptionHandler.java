package com.example.LukeriaFrontendApplication.exception;

import com.example.LukeriaFrontendApplication.slack.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private SlackService slackService;

    @ExceptionHandler(Exception.class)
    public void alertSlackChannelWhenUnexpectedErrorOccurs(Exception ex) {
        slackService.publishMessage("lukeria-notifications","Error occurred from the FRONTEND application ->" + ex.getMessage());
    }
}
