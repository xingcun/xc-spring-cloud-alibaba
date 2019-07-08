package com.xc.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 接口转换类
 *
 */
public class ModelVo extends BaseModelVo {

	@JsonIgnore
	@JSONField(serialize = false)
	public Pageable getPage() {
		List<Order> orders = getOrders();
		if (orders != null) {
			return PageRequest.of(getPageNo() - 1, getPageSize(), Sort.by(orders));
		} else {
			return PageRequest.of(getPageNo() - 1, getPageSize());
		}

	}

	@JsonIgnore
	@JSONField(serialize = false)
	public Pageable getPage(Direction direction, String... properties) {
		return PageRequest.of(getPageNo() - 1, getPageSize(), direction, properties);
	}

	@JsonIgnore
	@JSONField(serialize = false)
	public List<Order> getOrders() {
		List<Order> orders = null;
		if (getOrderBys() != null && !getOrderBys().isEmpty()) {
			orders = new ArrayList<Sort.Order>();
			for (String key : getOrderBys()) {
				String[] keys = key.split("_&_");

				if (keys.length > 1) {
					if (Direction.DESC.name().equalsIgnoreCase(keys[1])) {
						orders.add(Order.desc(keys[0]));
					} else {
						orders.add(Order.asc(keys[0]));
					}
				} else {
					orders.add(Order.by(key));
				}
			}

		}
		return orders;
	}

	public void setPageObje(Page page) {
		setPageNo(page.getNumber() + 1);
		this.setPageSize(page.getSize());
		setTotal(page.getTotalElements());
		setTotalPage(page.getTotalPages());
		if (getTotal() != null && getTotal().longValue() > 0) {
			setCode(Code.SUCCESS);
		}

	}

}
