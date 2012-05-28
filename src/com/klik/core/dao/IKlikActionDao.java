package com.klik.core.dao;

import java.util.List;

import com.klik.common.dao.ICassandraDao;
import com.klik.core.mo.KlikActionMO;

public interface IKlikActionDao extends ICassandraDao{

	public List<KlikActionMO> findAction2Analyze() throws Exception;
	
}
