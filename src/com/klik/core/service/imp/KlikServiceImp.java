package com.klik.core.service.imp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.klik.core.dao.IKlikActionDao;
import com.klik.core.dao.IKlikDao;
import com.klik.core.mo.KlikActionMO;
import com.klik.core.mo.KlikMO;
import com.klik.core.service.IKlikService;
import com.klik.core.tools.CommodityWordAmbiguityTools;
import com.klik.core.tools.TermTools;
import com.klik.dic.commodity.mo.CommodityWord;


@Service("klikService")
public class KlikServiceImp implements IKlikService {
	
	@Autowired
	protected CommodityWordAmbiguityTools commodityWordAmbiguityTools;
	@Autowired
	protected IKlikDao klikDao;
	@Autowired
	protected IKlikActionDao klikActionDao;
	
	Logger logger=LoggerFactory.getLogger(this.getClass());
	
	@Override
	public KlikMO translate2CommodityKlik(KlikActionMO action) throws Exception{
		String title=action.getTargetTitle();
		if (title==null||title.length()==0){
			try{
				title=parseUrl(action.getTargetUrl()).get("TITLE");
			}catch(Exception e){
				e.printStackTrace();
				logger.error("-----------ERROR----------\n ACTION_ID="+action.getId());
			}
		}
		String sentence=title;
		List<String> keywords = TermTools.getTermWords(sentence);
		if (keywords==null||keywords.size()==0){
			return null;
		}
		return translate2CommodityKlik(keywords,action);
	}
	
	private Map<String, String> parseUrl(String url) throws Exception{
		Map<String,String> result=new HashMap<String, String>();
		
		Document doc=Jsoup.connect(url).timeout(5000).get();
		String title=doc.select("title").text();
		result.put("TITLE", title.replaceAll(" ", ""));
		return result;
	}
	
	/**
	 * 转换Action为klik信息
	 */
	@Override
	public KlikMO translate2CommodityKlik(List<String> keywords,KlikActionMO action) throws Exception{
		HashMap<CommodityWord, Double> commodityWords= commodityWordAmbiguityTools.removeAmbiguity(keywords, action);
		List<String> urlCategories=commodityWordAmbiguityTools.getUrlCategories(action);
		
		KlikMO klik=new KlikMO();
		
		Collection<String> categories=new ArrayList<String>();
		Collection<String> relateWords=new ArrayList<String>();
		double weight;
		
		for(CommodityWord word:commodityWords.keySet()){
			weight = commodityWords.get(word);
			categories.clear();
			relateWords.clear();
			categories=word.getCategories().values();
			relateWords = word.getRelateWords().values();
			
			klik.addKeyword(word.getKey(), weight);
			for(String relateWord:relateWords){
				klik.addRelateWord(relateWord, weight);
			}
			for(String category:categories){
				klik.addCategory(category, weight);
			}
		}
		if (urlCategories!=null){
			for(String urlCategory:urlCategories){
				klik.addCategory(urlCategory, 1);
			}
		}
		
		return klik;
	}
	
	@Override
	public void insertKlik(KlikMO klik,KlikActionMO action) throws Exception{
		action.setAnalyzed(KlikActionMO._ANALYZED);
		klikDao.insert(klik, action);
	}
	
	
	public Map<KlikActionMO, KlikMO> queryKlik(String userId,long start,long end) throws Exception{
		return this.klikDao.queryKlik(userId, start, end);
	}
	
	

	
	
	
	public static void main(String[] args) throws Exception{
		AbstractApplicationContext ctx= new ClassPathXmlApplicationContext("classpath:spring/*.xml");
		ctx.registerShutdownHook();
		IKlikService klikService=(IKlikService)ctx.getBean("klikService");
		
		KlikActionMO action=new KlikActionMO();
		action.setLocationUrl("www.sina.com.cn");
		action.setTargetUrl("http://bbs.weiphone.com/thread-htm-fid-385.html");
		action.setTargetTitle("乔布斯传苹果通知零组件商：iPad Mini六月小量出货");
		action.setTime(System.currentTimeMillis()+"");
		
		action.setAnalyzed(KlikActionMO._ANALYZED);
		action.setBrowser("ie5");
		action.setIp("127.0.0.1");
		action.setUserId("myUserID");
		action.setUserIdType("cookie");
		
		List<String> keyWords=new ArrayList<String>();
		keyWords.add("苹果");
		keyWords.add("乔布斯");
		keyWords.add("豪车");
		
		
		KlikMO klik=klikService.translate2CommodityKlik(action);
		
		
		
		
		System.out.println(klik);
		klikService.insertKlik(klik, action);
	}
	
}
