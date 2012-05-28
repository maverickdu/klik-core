package com.klik.core.mo;

import com.netflix.astyanax.annotations.Component;

public class KlikCompositeColumn {
	
	public final static String  _COL_TYPE_KLIK="KLIK";
	public final static String _COL_TYPE_ACTION="ACTION";
	
	private @Component(ordinal=0) Long timestamp;
	private @Component(ordinal=1) String colType;
	
	public KlikCompositeColumn(){
		
	}
	
	public KlikCompositeColumn(Long timestamp,String colType){
		this.timestamp=timestamp;
		this.colType=colType;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getColType() {
		return colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}
	
	
	
	
}
