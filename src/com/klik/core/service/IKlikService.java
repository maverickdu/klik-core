package com.klik.core.service;

import java.util.List;
import java.util.Map;

import com.klik.core.mo.KlikActionMO;
import com.klik.core.mo.KlikMO;

public interface IKlikService {
	
	public KlikMO translate2CommodityKlik(KlikActionMO action) throws Exception;
	
	public KlikMO translate2CommodityKlik(List<String> keywords,KlikActionMO action) throws Exception;
	
	public void insertKlik(KlikMO klik,KlikActionMO action) throws Exception;
	
	public Map<KlikActionMO, KlikMO> queryKlik(String userId,long start,long end) throws Exception;
	
}
