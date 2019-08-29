package com.wallfacers.mybatis.scanner.service.impl;

import com.wallfacers.common.api.domain.Customer;
import com.wallfacers.mybatis.scanner.repository.CustomerDao;
import com.wallfacers.mybatis.scanner.service.CustomerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 默认的客户 Service
 *
 * @author wallfacers
 */
@Service
public class DefaultCustomerService implements CustomerService {

    @Resource
    private CustomerDao customerDao;

    @Override
    public Customer getCustomerByCustomerId(Integer customerId) {
        return customerDao.getCustomerByCustomerId(customerId);
    }
}
