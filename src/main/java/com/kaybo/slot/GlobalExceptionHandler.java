package com.kaybo.slot;

import com.kaybo.slot.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice("com.kaybo.slot")
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMyRuntimeException(AppException exception) {

        ErrorResponse err = new ErrorResponse();
        err.setErrCode(exception.getErrCode());
        err.setErrMsg(exception.getMessage());
        return err;
    }
}