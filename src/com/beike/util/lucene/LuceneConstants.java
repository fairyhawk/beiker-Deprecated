package com.beike.util.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.util.Version;
/**
 * lucene constants
 * @author janwen
 * Mar 26, 2012
 */
public class LuceneConstants {

	//lucene version
	public static final Version LUCENE_CURRENT_VERSION = Version.LUCENE_35;
	
	
	/**
	 * 不使用带Norm分词方式
	 */
	public static final Field.Index INDEX_ANALYZED = Field.Index.ANALYZED;
	
	public static final Field.Index INDEX_NOT_ANALYZED = Field.Index.NOT_ANALYZED;
	
	public static final Field.Index INDEX_NO = Field.Index.NO;
	
	//只分存储和不存储
	public static final Field.Store FIELD_STORE_YES = Field.Store.YES;
	
	public static final Field.Store FIELD_STORE_NO = Field.Store.NO;
	
	//二元分词bigram
	public static final Analyzer QP_ANALYZER = new CJKAnalyzer(LUCENE_CURRENT_VERSION);
}
