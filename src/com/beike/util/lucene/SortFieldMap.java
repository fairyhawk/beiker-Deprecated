package com.beike.util.lucene;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.SortField;

public class SortFieldMap {

	
	
	
	private static Map<String, SortField> sfm = new HashMap<String, SortField>();
	public static Map<String,SortField> getSortMap(){
			if(sfm.size() ==0){
				//false默认升序asc
				sfm.put("porder:asc", new SortField("price",FieldCache.DEFAULT_DOUBLE_PARSER,false));
				sfm.put("porder:desc", new SortField("price",FieldCache.DEFAULT_DOUBLE_PARSER,true));
				sfm.put("torder:asc", new SortField("ontime",FieldCache.DEFAULT_LONG_PARSER,false));
				sfm.put("torder:desc", new SortField("ontime",FieldCache.DEFAULT_LONG_PARSER,true));
				sfm.put("star:asc", new SortField("star",FieldCache.DEFAULT_DOUBLE_PARSER,false));
				sfm.put("star:desc", new SortField("star",FieldCache.DEFAULT_DOUBLE_PARSER,true));
				sfm.put("quantityorder:asc", new SortField("salescount",FieldCache.DEFAULT_DOUBLE_PARSER,false));
				sfm.put("quantityorder:desc", new SortField("salescount",FieldCache.DEFAULT_DOUBLE_PARSER,true));
				sfm.put("relativeorder:asc", SortField.FIELD_SCORE);
				sfm.put("relativeorder:desc", SortField.FIELD_SCORE);

			}
			
		return sfm;
	}
	
	
}
