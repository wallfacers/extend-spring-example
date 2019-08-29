package com.wallfacers.mybatis.scanner.service;

import com.wallfacers.common.api.domain.Customer;

/**
 * 客户 Service
 *
 * @author wallfacers
 */
public interface CustomerService {

    /**
     * 通过客户id获取客户信息
     *
     * @author wallfacers
     * @param customerId 客户id
     * @return 客户信息
     */
    Customer getCustomerByCustomerId(Integer customerId);

}
