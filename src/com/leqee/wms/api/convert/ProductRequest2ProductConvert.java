package com.leqee.wms.api.convert;

import org.springframework.stereotype.Component;

import com.leqee.wms.api.request.SyncProductRequest;
import com.leqee.wms.convert.AbstractConvert;
import com.leqee.wms.entity.Product;
@Component
public class ProductRequest2ProductConvert extends AbstractConvert<SyncProductRequest, Product> {

	@Override
	public Product covertToTargetEntity(SyncProductRequest productAddRequest) {
		
		Product product = new Product();
		product.setBrand_name(productAddRequest.getBrand_name());
		product.setBarcode(productAddRequest.getBarcode());
		product.setProduct_name(productAddRequest.getProduct_name());
		product.setSku_code(productAddRequest.getSku_code());
		product.setCat_name(productAddRequest.getCat_name());
		product.setCustomer_id(productAddRequest.getCustomer_id());
		product.setVolume(productAddRequest.getVolume());
		product.setLength(productAddRequest.getLength());
		product.setWidth(productAddRequest.getWidth());
		product.setHeight(productAddRequest.getHeight());
		product.setWeight(productAddRequest.getWeight());
		product.setSpec(productAddRequest.getSpec());
		product.setUnit_price(productAddRequest.getUnit_price());
		product.setValidity(productAddRequest.getValidity());
		product.setValidity_unit(productAddRequest.getValidity_unit());
		product.setIs_delete(productAddRequest.getIs_delete());
		product.setIs_contraband(productAddRequest.getIs_contraband());
		product.setIs_maintain_batch_sn(productAddRequest.getIs_maintain_batch_sn());
		product.setIs_maintain_warranty(productAddRequest.getIs_maintain_warranty());
		product.setIs_maintain_weight(productAddRequest.getIs_maintain_weight());
		product.setIs_serial(productAddRequest.getIs_serial());
		product.setProduct_type(productAddRequest.getProduct_type());
		product.setCreated_user(productAddRequest.getCreated_user());
		product.setCreated_time(productAddRequest.getCreated_time());
		product.setLast_updated_user(productAddRequest.getLast_updated_user());
		product.setLast_updated_time(productAddRequest.getLast_updated_time());
		product.setWarranty_unsalable_days(productAddRequest.getWarranty_unsalable_days() == null ? 0 :productAddRequest.getWarranty_unsalable_days());
		product.setWarranty_warning_days(productAddRequest.getWarranty_warning_days() ==  null ? 0 : productAddRequest.getWarranty_warning_days());
		
		return product;
	}

	@Override
	public SyncProductRequest covertToSourceEntity(Product product) {
		// TODO Auto-generated method stub
		return null;
	}

}
