package com.github.wuudongdong.week07.mapper;

import com.github.wuudongdong.week07.entity.CustomerInfo;

public interface CustomerInfoMapper {
    void insert(CustomerInfo info);

    CustomerInfo getById(Long id);
}
