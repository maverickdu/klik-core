package com.klik.core.dao.imp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.klik.common.dao.imp.CassandraDao;
import com.klik.common.util.CommonUtil;
import com.klik.core.dao.IKlikActionDao;
import com.klik.core.mo.KlikActionMO;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.mapping.Mapping;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.CqlResult;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.serializers.StringSerializer;

@Component("klikActionDao")
public class KlikActionDaoImp extends CassandraDao
	implements IKlikActionDao {
	@PostConstruct
	public void init(){
		cf=new ColumnFamily<String, String>("KLIK_ACTION", 
				StringSerializer.get(), StringSerializer.get());
	}

	@Override
	public List<KlikActionMO> findAction2Analyze() throws Exception {
		Mapping<KlikActionMO> mapper=(Mapping<KlikActionMO>) cache.getMapping(KlikActionMO.class);
		List<KlikActionMO> actionList=new ArrayList<KlikActionMO>();
		
		OperationResult<CqlResult<String, String>> result
			=this.keyspace.prepareQuery(cf).withCql("select * from KLIK_ACTION where ANALYZED='0'").execute();
		

		for(Row<String, String> row:result.getResult().getRows()){
			ColumnList<String> cl=row.getColumns();
			KlikActionMO mo=mapper.newInstance(cl);	
			actionList.add(mo);
			
		}
		
		return actionList;
	}
}
