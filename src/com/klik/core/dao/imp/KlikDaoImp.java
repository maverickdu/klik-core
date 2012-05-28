package com.klik.core.dao.imp;

import java.util.HashMap;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.klik.core.dao.IKlikDao;
import com.klik.core.mo.KlikActionMO;
import com.klik.core.mo.KlikCompositeColumn;
import com.klik.core.mo.KlikMO;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.StringSerializer;

@Component("klikDao")
public class KlikDaoImp implements IKlikDao, InitializingBean {
	
	@Autowired
	protected Keyspace keyspace;
	
	protected ColumnFamily<String,KlikCompositeColumn> cf;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		AnnotatedCompositeSerializer<KlikCompositeColumn> klikSerializer
			=new AnnotatedCompositeSerializer<KlikCompositeColumn>(KlikCompositeColumn.class);
		cf = new ColumnFamily<String, KlikCompositeColumn>("KLIK", StringSerializer.get(), klikSerializer);
		
	}
	@Override
	public void insert(KlikMO klik, KlikActionMO action) throws Exception {
		String key=action.getUserId();
		MutationBatch m=keyspace.prepareMutationBatch();
		
		KlikCompositeColumn klikCol=new KlikCompositeColumn(Long.parseLong(action.getTime()),KlikCompositeColumn._COL_TYPE_KLIK);
		KlikCompositeColumn actionCol=new KlikCompositeColumn(Long.parseLong(action.getTime()),KlikCompositeColumn._COL_TYPE_ACTION);
		
		m.withRow(cf, key)
			.putColumn(klikCol, JSONObject.fromObject(klik).toString(), null)
			.putColumn(actionCol, JSONObject.fromObject(action).toString(), null);
		m.execute();
	}
	
	
	@Override
	public HashMap<KlikActionMO, KlikMO> queryKlik(String userId,long start,long end) throws Exception{
		
		KlikCompositeColumn endCol = new KlikCompositeColumn(end,"þ");
		KlikCompositeColumn startCol = new KlikCompositeColumn(start,"0");
		
		ColumnList<KlikCompositeColumn> cols=keyspace.prepareQuery(cf).getKey(userId)
			.withColumnRange(endCol, startCol, true, Integer.MAX_VALUE).execute().getResult();
		
		HashMap<KlikActionMO, KlikMO> klikEntryInfos=new HashMap<KlikActionMO, KlikMO>();
		
		HashMap<Long, KlikMO> klikMap = new HashMap<Long, KlikMO>();
		HashMap<Long, KlikActionMO> actionMap=new HashMap<Long, KlikActionMO>();
		
		KlikCompositeColumn colName;
		for(Column<KlikCompositeColumn> col:cols){
			colName=col.getName();
			String jsonStr=col.getStringValue();
			if (colName.getColType().equalsIgnoreCase(KlikCompositeColumn._COL_TYPE_ACTION)){
				KlikActionMO action=KlikActionMO.parseFromJson(jsonStr);
				actionMap.put(colName.getTimestamp(), action);
			}else if (colName.getColType().equalsIgnoreCase(KlikCompositeColumn._COL_TYPE_KLIK)){
				KlikMO klik=KlikMO.parseFromJson(jsonStr);
				klikMap.put(colName.getTimestamp(), klik);
			}
		}
		
		for(Long key:actionMap.keySet()){
			klikEntryInfos.put(actionMap.get(key), klikMap.get(key));
		}
		
		return klikEntryInfos;
	}
	
	
}
