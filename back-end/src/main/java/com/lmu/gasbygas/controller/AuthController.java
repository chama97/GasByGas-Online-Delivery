package com.lmu.gasbygas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lmu.gasbygas.dto.request.LoginReqDTO;
import com.lmu.gasbygas.service.UserService;
import com.lmu.gasbygas.util.ResponseUtil;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("auth")
public class AuthController {

    @Autowired
	private UserService userService;

    @PostMapping("/login")
    public ResponseUtil validateAuthentication(@RequestBody LoginReqDTO loginReqDTO) {
        try {
            return userService.authenticationLogin(loginReqDTO);
        } catch (Exception e) {
            return new ResponseUtil(500, e.getMessage(), null);
        }
    }
    
}
