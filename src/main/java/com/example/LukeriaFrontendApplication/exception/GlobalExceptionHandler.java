package com.example.LukeriaFrontendApplication.exception;

import com.example.LukeriaFrontendApplication.slack.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SlackService slackService;

    @ExceptionHandler(Exception.class)
    public void alertSlackChannelWhenUnexpectedErrorOccurs(Exception ex) {
        slackService.publishMessage("lukeria-notifications","Error occurred from the FRONTEND application ->" + ex.getMessage());
    }
}
