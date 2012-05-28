package com.klik.core.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.klik.dic.base.dao.IBaseDicDao;
import com.klik.dic.base.mo.BaseWord;
import com.klik.dic.commodity.dao.ICommodityDicDao;
import com.klik.dic.commodity.mo.CommodityWord;


@Component("commonDicTools")
public class CommonDicTools {

	@Autowired
	ICommodityDicDao commodityDicDao;
	@Autowired
	IBaseDicDao baseDicDao;
	
	
	public List<CommodityWord> findRelateCommodityWords(String keyword) throws Exception{
		
		List<CommodityWord> relateCommodityWords=new ArrayList<CommodityWord>();
		
		BaseWord baseWord=baseDicDao.getBaseWord(keyword);
		if (baseWord!=null){
			HashMap<Integer, String> relateWordsMap=baseWord.getRelateWords();
			for(Integer idx:relateWordsMap.keySet()){
				String[] relateWordList=relateWordsMap.get(idx).split(",");
				if (relateWordList!=null){
					for(String relateKey:relateWordList){
						CommodityWord commodityWord=commodityDicDao.findWord(relateKey);
						if (commodityWord!=null) relateCommodityWords.add(commodityWord);
					}
				}
			}
			
		}
		return relateCommodityWords;
	}
	
	
}
