package com.klik.core.tools;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

	
public class TermTools {
	
	private static IKSegmenter ikSeg = new IKSegmenter(null, true);
	
	
	public static List<String> getTermWords(String sentence){
		ikSeg.reset(new StringReader(sentence));
		List<String> words=new ArrayList<String>();
		try{
			Lexeme l=null;
			while((l=ikSeg.next())!=null){
				if(!termFilter(l.getLexemeText())){
					continue;
				}
				words.add(l.getLexemeText());
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return words;
	}
	
	/**
	 * 分词过滤 1.单文字 2.纯数字
	 * @param term
	 * @return
	 */
	private  static boolean termFilter(String term){

		if (term.length()<2) return false;//单个文字去除
		
		try{//纯数字去除
			Integer.parseInt(term);
			return false;
		}catch(NumberFormatException nex){
		}
		
		return true;
	}
	
}
