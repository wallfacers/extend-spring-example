package com.wallfacers.common.api.domain;

import lombok.Data;

/**
 * 客户实体类
 *
 * @author wallfacers
 */
@Data
public class Customer {

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户名称
     */
    private String  customerName;

    /**
     * 客户年龄
     */
    private Integer customerAge;

}
