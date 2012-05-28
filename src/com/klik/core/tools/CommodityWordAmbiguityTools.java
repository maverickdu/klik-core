package com.klik.core.tools;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.klik.common.util.CommonUtil;
import com.klik.core.exception.RemoveAmbiguityException;
import com.klik.core.mo.KlikActionMO;
import com.klik.core.service.IUrlPatternService;
import com.klik.dic.base.dao.IBaseDicDao;
import com.klik.dic.commodity.dao.ICommodityDicDao;
import com.klik.dic.commodity.mo.CommodityWord;

@Component("commodityWordAmbiguityTools")
public class CommodityWordAmbiguityTools {
	
	@Autowired
	IUrlPatternService urlPatternService;
	@Autowired
	ICommodityDicDao commodityDicDao;
	@Autowired
	IBaseDicDao baseDicDao;
	@Autowired
	CommonDicTools commonDicTools;
	
	
	Logger logger=LoggerFactory.getLogger(this.getClass());
	
	public HashMap<CommodityWord, Double> removeAmbiguity(List<String> keywords,KlikActionMO action)
			throws RemoveAmbiguityException,Exception{
		
		HashMap<CommodityWord, Double> wordsWithAmbiguityWeight = new HashMap<CommodityWord, Double>();
		
		for(String keyword:keywords){
			CommodityWord commodityWord=commodityDicDao.findWord(keyword);
			if (commodityWord==null){
				List<CommodityWord> relateWords=commonDicTools.findRelateCommodityWords(keyword);
				for(CommodityWord relateWord:relateWords){
					wordsWithAmbiguityWeight.put(relateWord, SystemConstant._relateWordWeight);
				}
			}else{
				wordsWithAmbiguityWeight.put(commodityWord, 1d);
			}
		}
		
		return removeAmbiguity(wordsWithAmbiguityWeight,action);
	}
	
	
	
	
	public HashMap<CommodityWord, Double> removeAmbiguity(HashMap<CommodityWord, Double> wordsWithAmbiguityWeight,
			KlikActionMO action) throws RemoveAmbiguityException,Exception{
		
		Set<CommodityWord> wordsWithAmbiguity = wordsWithAmbiguityWeight.keySet();
		
		if (!this.shouldRemoveAmbiguity(wordsWithAmbiguity)){//所有单词没有歧义
			return wordsWithAmbiguityWeight;
		}
		
		HashMap<String, Double> categoryTotalWeight= getCategoryTotalWeight(wordsWithAmbiguityWeight,action);
		
		HashMap<CommodityWord, Double> words=new HashMap<CommodityWord,Double>();
		
		for(CommodityWord wordWithAmbiguity:wordsWithAmbiguity){
			
			try{
				CommodityWord word=removeAmbiguity(wordWithAmbiguity,action,categoryTotalWeight);
				words.put(word,wordsWithAmbiguityWeight.get(wordWithAmbiguity));
			}catch(RemoveAmbiguityException ex){
				System.out.println("{"+wordWithAmbiguity.getKey()+"}:无法去除歧义,原因:无法获得去除依据");
				continue;
			}
		}
		
		return words;
	}
	
	public CommodityWord removeAmbiguity(CommodityWord wordWithAmbiguity,
			KlikActionMO action,HashMap<String, Double> categoryTotalWeight) 
					throws RemoveAmbiguityException{
		
		Integer ambiguityCount=wordWithAmbiguity.getAmbiguityCount();
		if (!wordWithAmbiguity.isWithAmbiguity()){
			return wordWithAmbiguity;
		}
		double maxMatchCount=-1;
		int maxMatchIdx=-1;
		double matchCount=0;
		
		for(int i=0;i<ambiguityCount;i++){
			List<String> categories=wordWithAmbiguity.getCategories(i);
			matchCount = CommonUtil.getMatchCount(categories, categoryTotalWeight);
			if (matchCount>maxMatchCount){
				maxMatchCount = matchCount;
				maxMatchIdx = i;
			}
		}
		
		if (maxMatchCount<=wordWithAmbiguity.getCategories(maxMatchIdx).size()){
			throw new RemoveAmbiguityException("");
		}
		return CommodityWord.selectOneAmbiguity(wordWithAmbiguity, maxMatchIdx);
	}
	
	public  List<String> getUrlCategories(KlikActionMO action) throws Exception{
		List<String> urlCategories=urlPatternService.getCategries(action.getTargetUrl());
		if (urlCategories==null){
			urlCategories=urlPatternService.getCategries(action.getLocationUrl());
		}
		return urlCategories;
	}
	
	/*
	private CommodityWord removeAmbiguityUseAction(CommodityWord word,KlikActionMO action) throws Exception{
		if (!word.isWithAmbiguity()){
			return word;
		}
		List<String> urlCategories = getUrlCategories(action);
		int maxMatchCount=-1;
		int maxMatchIdx=-1;
		int matchCount=0;
		for(int i=0;i<word.getAmbiguityCount();i++){
			List<String> wordCategories=word.getCategories(i);
			matchCount=CommonUtil.getMatchCount(wordCategories, urlCategories);
			if (matchCount>maxMatchCount){
				maxMatchCount = matchCount;
				maxMatchIdx = i;
			}
		}
		return CommodityWord.selectOneAmbiguity(word,maxMatchIdx);
	}*/
	
	private HashMap<String, Double> getCategoryTotalWeight(HashMap<CommodityWord,Double> wordsWeight
			,KlikActionMO action) throws Exception{
	
		HashMap<String, Double> categoryWeight=new HashMap<String, Double>();
		Set<CommodityWord> words=wordsWeight.keySet();
		
 		for(CommodityWord word:words){
 			Collection<String> categories=word.getCategories().values();
 			for(String category:categories){
 				Double currentWeight=categoryWeight.get(category);
 				if (currentWeight!=null){
 					currentWeight += wordsWeight.get(word);
 				}else{
 					currentWeight=wordsWeight.get(word);
 				}
 				categoryWeight.put(category, currentWeight);
 			}
		}
 		
 		
 		List<String> urlCategories = getUrlCategories(action);
 		
 		if (urlCategories!=null){
	 		for(String urlCategory:urlCategories){
	 			Double currentWeight=categoryWeight.get(urlCategory);
				if (currentWeight!=null){
					currentWeight ++;
				}else{
					currentWeight=1d;
				}
				categoryWeight.put(urlCategory, currentWeight);
	 		}
 		}
		return categoryWeight;
	}
	
	public boolean shouldRemoveAmbiguity(Set<CommodityWord> words){
		for(CommodityWord word:words){
			if (word.isWithAmbiguity()){
				return true;
			}
		}
		return false;
	}
	
	
	public static void main(String[] args) throws Exception{
		AbstractApplicationContext ctx= new ClassPathXmlApplicationContext("classpath:spring/*.xml");
		ctx.registerShutdownHook();
		CommodityWordAmbiguityTools tools=(CommodityWordAmbiguityTools)ctx.getBean("commodityWordAmbiguityTools");
		
		KlikActionMO action=new KlikActionMO();
		action.setLocationUrl("www.sina.com.cn");
		action.setTargetUrl("http://bbs.weiphone.com/thread-htm-fid-385.html");
		action.setTargetTitle("乔布斯传苹果通知零组件商：iPad Mini六月小量出货");
		action.setTime(System.currentTimeMillis()+"");
		
		List<String> keyWords=new ArrayList<String>();
		keyWords.add("苹果");
		keyWords.add("乔布斯");
		keyWords.add("豪车");
		
		
		HashMap<CommodityWord, Double> words=tools.removeAmbiguity(keyWords, action);
		for(CommodityWord word:words.keySet()){
			System.out.println("----------------------------");
			System.out.println(word.getKey()+":"+words.get(word));
			System.out.println(word.getCategories().values());
			
		}
		
		
	}
	
}
