package com.tindy.app.exceptions.global;


import com.tindy.app.dto.respone.ErrorRespone;
import com.tindy.app.exceptions.ForbiddenException;
import com.tindy.app.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> globalExceptionHandling(Exception exception, WebRequest request) {
        ErrorRespone errorResponse = new ErrorRespone(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> ResourceNotFoundHandling(ResourceNotFoundException exception, WebRequest request) {
        ErrorRespone errorResponse = new ErrorRespone(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<Object> ForbiddenExceptionHandling(ForbiddenException exception, WebRequest request) {
        ErrorRespone errorResponse = new ErrorRespone(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
