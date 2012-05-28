package com.klik.core.vo;

import java.util.List;
import java.util.Map.Entry;

public class KlikAnalyzerResultVO {
	private List<Entry<String,Double>> keywordsWeight;
	private List<Entry<String,Double>> relateWordsWeight;
	private List<Entry<String,Double>> categoriesWeight;
	
	public List<Entry<String, Double>> getKeywordsWeight() {
		return keywordsWeight;
	}
	public void setKeywordsWeight(List<Entry<String, Double>> keywordsWeight) {
		this.keywordsWeight = keywordsWeight;
	}
	public List<Entry<String, Double>> getRelateWordsWeight() {
		return relateWordsWeight;
	}
	public void setRelateWordsWeight(
			List<Entry<String, Double>> relateWordsWeight) {
		this.relateWordsWeight = relateWordsWeight;
	}
	public List<Entry<String, Double>> getCategoriesWeight() {
		return categoriesWeight;
	}
	public void setCategoriesWeight(List<Entry<String, Double>> categoriesWeight) {
		this.categoriesWeight = categoriesWeight;
	}
	
	
	
}
