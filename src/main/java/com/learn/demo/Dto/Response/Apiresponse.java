package com.learn.demo.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Apiresponse {
   
    private int Httpstatus;
    private String message;
    private Object data;
}