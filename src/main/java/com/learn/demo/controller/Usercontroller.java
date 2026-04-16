package com.learn.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.Dto.Response.Apiresponse;
import com.learn.demo.model.User;
import com.learn.demo.service.UserService;

@RestController   //it handles http req then send response as json format
@RequestMapping("/api/users")  //base url for all user related endpoints
public class Usercontroller {
    @Autowired
    private UserService service;

    //create
    @PostMapping
    public ResponseEntity<Apiresponse> saveUser(@RequestBody User user){
        return ResponseEntity.ok(new Apiresponse(HttpStatus.CREATED.value(), "User created successfully", service.saveUser(user)));
    }

    //read all
    @GetMapping
    public ResponseEntity<Apiresponse> getAllUsers(){
        return ResponseEntity.ok(new Apiresponse(HttpStatus.OK.value(), "Users retrieved successfully", service.getAllUsers()));
    }

    //read by id
    @GetMapping("/{userId}")
    public ResponseEntity<Apiresponse> getUserById(@PathVariable Long userId){
        return ResponseEntity.ok(new Apiresponse(HttpStatus.OK.value(), "User retrieved successfully", service.getUserById(userId)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Apiresponse> updateUser(@PathVariable Long userId, @RequestBody User user){
        return ResponseEntity.ok(new Apiresponse(HttpStatus.OK.value(), "User updated successfully", service.updateUser(userId, user)));
    }

    //delete
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Long userId){
        service.deleteUser(userId);
        return "Deleted successfully!";
    }


    @GetMapping("/search")
public ResponseEntity<Apiresponse> searchUsers(
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String role) {

    return ResponseEntity.ok(
        new Apiresponse(
            HttpStatus.OK.value(),
            "Users filtered successfully",
            service.searchUsers(username, role)
        )
    );
}
    
}
