package com.klik.core.dao;

import java.util.HashMap;

import com.klik.core.mo.KlikActionMO;
import com.klik.core.mo.KlikMO;

public interface IKlikDao {
	
	public void insert(KlikMO klik,KlikActionMO action) throws Exception;
	
	public HashMap<KlikActionMO, KlikMO> queryKlik(String userId,long start,long end) throws Exception;
}
