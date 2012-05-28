package com.klik.core.service.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klik.core.dao.IKlikActionDao;
import com.klik.core.mo.KlikActionMO;
import com.klik.core.service.IKlikActionService;

@Service("klikActionService")
public class KlikActionServiceImp implements IKlikActionService {
	
	@Autowired
	IKlikActionDao klikActionDao;
	
	@Override
	public void insertAction(KlikActionMO action) throws Exception{
		klikActionDao.insert(action, action.getId());
	}

	@Override
	public List<KlikActionMO> findActions2Analyze() throws Exception{
		return klikActionDao.findAction2Analyze();
	}

}
