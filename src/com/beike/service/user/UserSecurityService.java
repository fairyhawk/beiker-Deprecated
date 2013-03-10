package com.beike.service.user;

import java.util.Map;

public interface UserSecurityService {

	public Map getUserBySign(String sign);

	public void updateSign(String sign);
}
