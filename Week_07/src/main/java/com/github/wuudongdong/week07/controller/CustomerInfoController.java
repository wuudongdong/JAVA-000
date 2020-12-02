package com.github.wuudongdong.week07.controller;

import com.github.wuudongdong.week07.entity.CustomerInfo;
import com.github.wuudongdong.week07.pojo.CustomerVO;
import com.github.wuudongdong.week07.service.CustomerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息相关
 *
 * @author wdd
 * @date 2020/11/29
 */
@RestController
public class CustomerInfoController {

    @Autowired
    CustomerInfoService customerInfoService;

    @PutMapping("customer")
    public String addCustomer(@RequestBody CustomerVO customerVO){
        customerInfoService.addCustomer(customerVO);
        return "success";
    }

    @GetMapping("customer/{id}")
    public CustomerInfo queryCustomer(@PathVariable("id") Long id){
        return customerInfoService.queryCustomer(id);
    }
}
