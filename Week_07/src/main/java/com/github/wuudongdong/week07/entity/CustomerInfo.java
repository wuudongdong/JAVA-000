package com.github.wuudongdong.week07.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户信息表(CustomerInfo)实体类
 *
 * @author makejava
 * @since 2020-11-29 14:55:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
    * 自增主键ID
    */
    private Integer customerInfoId;
    /**
    * 用户真实姓名
    */
    private String customerName;
    /**
    * 证件类型：1 身份证，2 军官证，3 护照
    */
    private Integer identityCardType;
    /**
    * 证件号码
    */
    private String identityCardNo;
    /**
    * 手机号
    */
    private Long mobilePhone;
    /**
    * 邮箱
    */
    private String customerEmail;
    /**
    * 性别
    */
    private String gender;
    /**
    * 注册时间
    */
    private Date registerTime;
    /**
    * 最后修改时间
    */
    private Date modifiedTime;


}