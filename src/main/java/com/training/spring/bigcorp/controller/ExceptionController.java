package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handle(NotFoundException e){
        List<String> trace = Arrays.asList(e.getStackTrace()).stream()
                .map(st -> st.toString())
                .collect(Collectors.toList());
        ModelAndView mv = new ModelAndView("/error/404")
                .addObject("status", 404)
                .addObject("error", "Not found exception")
                .addObject("trace", trace)
                .addObject("timestamp", new Date())
                .addObject("message", e.getMessage());
        mv.setStatus(HttpStatus.NOT_FOUND);
        return mv;
    }
}