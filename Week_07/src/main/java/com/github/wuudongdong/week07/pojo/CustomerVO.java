package com.github.wuudongdong.week07.pojo;

import lombok.Data;

@Data
public class CustomerVO {
    String name;
    Integer idCardType;
    String idCardNO;
    Long phone;
    String email;
    String gender;
}
