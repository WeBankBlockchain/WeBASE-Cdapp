package com.certapp.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NewPasswordInfo {

    @NotBlank
    private String newPwd;
    private String oldPwd;
    private Integer Id;

}
