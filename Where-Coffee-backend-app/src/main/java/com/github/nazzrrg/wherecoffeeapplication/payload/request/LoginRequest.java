package com.github.nazzrrg.wherecoffeeapplication.payload.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String name;
    private String password;
}