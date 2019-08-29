package com.wallfacers.mybatis.scanner.repository;


import com.wallfacers.common.api.domain.Customer;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 客户 dao
 *
 * @author wallfacers
 */
@Repository
public interface CustomerDao {

    /**
     * 通过客户id获取客户信息
     *
     * @author wallfacers
     * @param customerId 客户id
     * @return 客户信息
     */
    Customer getCustomerByCustomerId(@Param("customerId") Integer customerId);
}
