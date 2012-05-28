package com.klik.core.service;

import java.util.List;

import com.klik.core.mo.KlikActionMO;

public interface IKlikActionService {
	
	public void insertAction(KlikActionMO action) throws Exception;
	public List<KlikActionMO> findActions2Analyze() throws Exception;
	
	
	
}
