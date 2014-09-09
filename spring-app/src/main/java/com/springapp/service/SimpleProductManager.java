package com.springapp.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.springapp.domain.Product;

public class SimpleProductManager implements ProductManager {
	protected final Log logger = LogFactory.getLog(getClass());
	private List<Product> products;
	
	@Override
	public void increasePrice(int percentage) {
		if (products != null) {
            for (Product product : products) {
                double newPrice = product.getPrice().doubleValue() * (100 + percentage)/100;
                product.setPrice(newPrice);
            }
        }
		logger.info("Price increased by: " + percentage + "%");
 	}

	@Override
	public List<Product> getProducts() {
		return this.products;
	}

	public void setProducts(List<Product> list) {
		this.products = list;
	}

}
