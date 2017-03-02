package com.leqee.wms.api;

public interface LeqeeClient {
	public abstract <T extends LeqeeResponse> T execute(
			LeqeeRequest<T> leqeeRequest) throws ApiException;

}
