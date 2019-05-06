package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getAllUser",method = RequestMethod.GET)
    @ResponseBody
    public List<User> getAllUser(){
        return userService.getAllUser();
    }

    @RequestMapping(value = "/deleteUser",method = RequestMethod.POST)
    @ResponseBody
    public Integer deleteUser(@RequestParam("id")Integer id){
        return userService.delete(id);
    }

    @RequestMapping(value = "/getUser",method = RequestMethod.GET)
    @ResponseBody
    public User getUser(@RequestParam("id")Integer id){
        return userService.getUserById(id);
    }
}
