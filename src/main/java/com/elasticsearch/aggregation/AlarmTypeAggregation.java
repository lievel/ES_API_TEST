package com.elasticsearch.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import com.elasticsearch.querybuilder.QueryBuilderUtil;
import com.elasticsearch.service.ESService;

public class AlarmTypeAggregation {
	
	public static List<Map<String, Object>> getAggregation(String fromDate, String toDate, String condition,
			List<String> siteList, List<String> sitebldList, List<String> sitebldflrList ) throws Exception{
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		TermQueryBuilder termQueryBuilder = null;
		
		RangeQueryBuilder dateRangeBuilder = QueryBuilderUtil.getDateTimeRangeQueryBuilder("alm_time", "yyyy-MM-dd HH:mm:ss", fromDate, toDate);
		BoolQueryBuilder validAreaQueryBuilder = QueryBuilderUtil.getValidAreaQueryBuilder(siteList, sitebldList, sitebldflrList);
		
		if(condition != null && condition != ""){
			termQueryBuilder = QueryBuilders.termQuery("alm_type_cd.keyword", condition);
		}
		
		//검색 연산
		BoolQueryBuilder searchQuery = QueryBuilders.boolQuery();
		searchQuery.must(dateRangeBuilder);
		//searchQuery.must(validAreaQueryBuilder);
		
		if(termQueryBuilder != null){
			searchQuery.must(termQueryBuilder);
		}
		
        //집계 연산
        AggregationBuilder aggregation = AggregationBuilders
                .terms("Agg_Condition1_Count")
                .field("seq_no.keyword")
                .subAggregation(AggregationBuilders
                        .terms("Agg_Condition2_Count")
                        .field("alm_type_cd.keyword"));
		
        SearchResponse response = ESService.ESServiceRequest(searchQuery, aggregation);
        
	     // Make resultList
	     for (Terms.Bucket entry1 : ((Terms) response.getAggregations().get("Agg_Condition1_Count")).getBuckets()) {
	    	 
	    	 String seq_no = (String) entry1.getKey();
	    	 
	    	 for (Terms.Bucket entry2 : ((Terms) entry1.getAggregations().get("Agg_Condition2_Count")).getBuckets()){
	    		 Map<String, Object> map  = new HashMap<String, Object>();
	    		 map.put("SEQ_NO", seq_no);
	    		 map.put("ALM_TYPE_CD", entry2.getKey());
	    		 map.put("CNT", entry2.getDocCount());
	    		 
	    		 resultList.add(map);
	    	 }
	               
	     }
        
        System.out.println(response.toString());
		
		return resultList;
	}

}
