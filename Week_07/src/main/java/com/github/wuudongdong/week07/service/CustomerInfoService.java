package com.github.wuudongdong.week07.service;

import com.github.wuudongdong.week07.entity.CustomerInfo;
import com.github.wuudongdong.week07.pojo.CustomerVO;

public interface CustomerInfoService {
    void addCustomer(CustomerVO customerVO);

    CustomerInfo queryCustomer(Long id);
}
