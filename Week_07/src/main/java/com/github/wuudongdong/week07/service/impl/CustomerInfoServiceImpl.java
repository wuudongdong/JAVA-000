package com.github.wuudongdong.week07.service.impl;

import com.github.wuudongdong.week07.annotation.ReadOnly;
import com.github.wuudongdong.week07.entity.CustomerInfo;
import com.github.wuudongdong.week07.mapper.CustomerInfoMapper;
import com.github.wuudongdong.week07.pojo.CustomerVO;
import com.github.wuudongdong.week07.service.CustomerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomerInfoServiceImpl implements CustomerInfoService {

    @Autowired
    CustomerInfoMapper customerInfoMapper;

    @Override
    @ReadOnly(dataSourceId = "master")
    public void addCustomer(CustomerVO customerVO) {
        CustomerInfo info = new CustomerInfo();
        info.setCustomerName(customerVO.getName());
        info.setGender(customerVO.getGender());
        info.setCustomerEmail(customerVO.getEmail());
        info.setIdentityCardNo(customerVO.getIdCardNO());
        info.setMobilePhone(customerVO.getPhone());
        info.setIdentityCardType(customerVO.getIdCardType());
        info.setRegisterTime(new Date());
        info.setModifiedTime(new Date());
        customerInfoMapper.insert(info);
    }

    @Override
    @ReadOnly(dataSourceId = "slave")
    public CustomerInfo queryCustomer(Long id) {
        return customerInfoMapper.getById(id);
    }
}
