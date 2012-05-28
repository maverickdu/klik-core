package com.klik.core.mo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import com.klik.common.exception.WrongParaException;

public class KlikMO {
	
	
	
	private Map<String, Double> keyWordsWeight;
	private Map<String,Double> relateWordsWeight;
	private Map<String,Double> categoriesWeight;
	
	public KlikMO(){
		keyWordsWeight = new HashMap<String, Double>();
		relateWordsWeight = new HashMap<String, Double>();
		categoriesWeight = new HashMap<String, Double>();
	}

	public Map<String, Double> getKeyWordsWeight() {
		return keyWordsWeight;
	}

	public Map<String, Double> getRelateWordsWeight() {
		return relateWordsWeight;
	}
	
	public Map<String, Double> getCategoriesWeight() {
		return categoriesWeight;
	}

	public void addCategory(String category,double weight){
		double currentWeight=0;
		if (this.categoriesWeight.containsKey(category)){
			currentWeight = categoriesWeight.get(category);
		}
		weight += currentWeight;
		categoriesWeight.put(category, weight);
	}
	
	public void addRelateWord(String relateWord,double weight){
		double currentWeight=0;
		if (this.relateWordsWeight.containsKey(relateWord)){
			currentWeight = relateWordsWeight.get(relateWord);
		}
		weight += currentWeight;
		relateWordsWeight.put(relateWord, weight);
	}
	
	public void addKeyword(String keyword,double weight){
		double currentWeight=0;
		if (this.keyWordsWeight.containsKey(keyword)){
			currentWeight = keyWordsWeight.get(keyword);
		}
		weight += currentWeight;
		keyWordsWeight.put(keyword, weight);
	}
	
	public void combine(KlikMO klik){
		
		Set<String> categoies=klik.getCategoriesWeight().keySet();
		Set<String> keywords=klik.getKeyWordsWeight().keySet();
		Set<String> relatewords=klik.getRelateWordsWeight().keySet();
		
		for(String category:categoies){
			this.addCategory(category, klik.getCategoriesWeight().get(category));
		}
		for(String keyword:keywords){
			this.addKeyword(keyword, klik.getKeyWordsWeight().get(keyword));
		}
		for(String relateWord:relatewords){
			this.addRelateWord(relateWord, klik.getRelateWordsWeight().get(relateWord));
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static KlikMO parseFromJson(String jsonString){
		KlikMO klik=new KlikMO();
		
		JSONObject json=JSONObject.fromObject(jsonString);
		JSONObject cateroiesJSON=JSONObject.fromObject(json.get("categoriesWeight"));
		JSONObject keyWordsJSON=JSONObject.fromObject(json.get("keyWordsWeight"));
		JSONObject relateWordsJSON=JSONObject.fromObject(json.get("relateWordsWeight"));
		
		Set<String> keys=cateroiesJSON.keySet();
		for(String key:keys){
			klik.getCategoriesWeight().put(key, (Double)cateroiesJSON.get(key));
		}
		keys=keyWordsJSON.keySet();
		for(String key:keys){
			klik.getKeyWordsWeight().put(key, (Double)keyWordsJSON.get(key));
		}
		
		keys=relateWordsJSON.keySet();
		for(String key:keys){
			klik.getRelateWordsWeight().put(key, (Double)relateWordsJSON.get(key));
		}
		
		return klik;
	}
	
	
	public static KlikMO combineKlik(KlikMO... kliks){
		KlikMO combinedKlik=new KlikMO();
		for(KlikMO klik:kliks){
			combinedKlik.combine(klik);
		}
		return combinedKlik;
	}
	
	public static KlikMO combineKlik(Collection<KlikMO> kliks){
		KlikMO combinedKlik=new KlikMO();
		for(KlikMO klik:kliks){
			combinedKlik.combine(klik);
		}
		return combinedKlik;
	}
	
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("----------------klik info start------------------------------------");
		sb.append("\nKEYWORDS_WEIGHT: "+this.keyWordsWeight.toString());
		sb.append("\nRELATE_WORDS_WEIGHT: "+this.relateWordsWeight.toString());
		sb.append("\nCATEGOIRES_WEIGHT: "+this.categoriesWeight.toString());
		sb.append("\n----------------klik info end------------------------------------");
		return sb.toString();
	}
	
	
	
}
