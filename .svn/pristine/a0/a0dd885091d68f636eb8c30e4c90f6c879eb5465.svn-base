package com.elasticsearch.API_TEST;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONObject;

import com.elasticsearch.util.JsonUtil;

public class Test2 {
	
	private static TransportClient client;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		//TEST
		Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));  
        
        //예시 Site List
        List<String> siteList = new ArrayList<String>();
        siteList.add("Turk_TSC");
        
        String site_bld_script = "for(int i=0; i<params.site_bld.length; i++) {if(params.site_bld[i] == doc['site_id.keyword'].value + doc['bld_id.keyword'].value){ return true } } ";
        String site_bld_flr_script = "for(int i=0; i<params.site_bld_flr.length; i++) {if(params.site_bld_flr[i] == doc['site_id.keyword'].value + doc['bld_id.keyword'].value + doc['flr_id.keyword'].value){ return true } } ";
        
        //예시 Bld List
        List<String> sitebldList = new ArrayList<String>();
        sitebldList.add("Turk_TSCTSC_AR000");
        
        Map<String, Object> params_sitebld = new HashMap();
        params_sitebld.put("site_bld", sitebldList);
        
        //예시 Flr List
        List<String> sitebldflrList = new ArrayList<String>();
        sitebldflrList.add("ALZAHALZAH_AR01A01");
        
        Map<String, Object> params_sitebldflr = new HashMap();
        params_sitebldflr.put("site_bld_flr", sitebldflrList);
        
        BoolQueryBuilder validAreaQueryBuilder = QueryBuilders.boolQuery();
        validAreaQueryBuilder.should(QueryBuilders.termsQuery("site_id.keyword", siteList));
        validAreaQueryBuilder.should(QueryBuilders.scriptQuery(new Script(ScriptType.INLINE, "painless", site_bld_script, params_sitebld)));
        validAreaQueryBuilder.should(QueryBuilders.scriptQuery(new Script(ScriptType.INLINE, "painless", site_bld_flr_script, params_sitebldflr)));
        
//        validAreaQueryBuilder.should(QueryBuilders.termsQuery("bld_id.keyword", bldList));
//        validAreaQueryBuilder.should(QueryBuilders.termsQuery("flr_id.keyword", flrList));
        
        		
        //집계 연산
//        AggregationBuilder aggregation = AggregationBuilders
//                .terms("agg")
//                .field("alm_grade_cd.keyword");
        AggregationBuilder aggregation = AggregationBuilders
        		.dateRange("Agg_DateRange")
        		.field("alm_time")
        		.format("yyyy-MM-dd HH:mm:ss")
        		.keyed(true)
        		.addRange("date_range", "2017-05-01 00:00:00", "2017-10-31 23:59:59")
        		//.addRange("month_06", "2017-06-01 00:00", "2017-06-30 23:59")
        		.subAggregation(
        				AggregationBuilders.filters("validArea", validAreaQueryBuilder)
        				.subAggregation(AggregationBuilders
                                .terms("Agg_Device_Count")
                                .field("seq_no.keyword")
                                .subAggregation(AggregationBuilders
                                        .terms("Agg_main_Count")
                                        .field("alm_mas_id.keyword")
                                )
        				)
        		)
        		;
        
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch()
        		.setIndices("secu_rt_alm")
                .setTypes("alarm_data")
                //.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                //.setQuery(QueryBuilders.matchQuery("alm_grade_cd", "MAJ"))
                .setFrom(0)
                .setSize(0)
                .setExplain(false)
                //.highlighter(highlightBuilder)
                //.addSort(sortBuilders)
                //.suggest(suggestBuilder)
                .addAggregation(aggregation);

//        if (aggregationBuilder != null){
//            searchRequestBuilder.addAggregation(aggregationBuilder);
//        }
        
        SearchResponse searchResponse =  searchRequestBuilder.execute().actionGet();
        
//        Terms agg = searchResponse.getAggregations().get("alm_grade_count_value");
//
//        System.out.println(agg.toString());

//        for (Terms.Bucket list : agg.getBuckets()){
//            System.out.println(list.getAggregations());
//        }
        
        
        System.out.println(searchResponse.toString());
        
        JSONObject jsonObj = new JSONObject(searchResponse.toString());
        
        Map<String,Object> map = JsonUtil.jsonString2mapJSonFlattner(searchResponse.toString());
        
//        searchResponse.getAggregations().get("Agg_DateRange")
        
//        SearchResponse response = client.prepareSearch("rt_alm")
//                .setTypes("alarmdata")
//                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                //.setQuery(QueryBuilders.matchQuery("alm_mas_id", "VAFR_SUS01"))                 // Query
//                .setQuery(QueryBuilders.matchQuery("alm_grade_cd", "MAJ"))                 // Query
//                //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
//                .setFrom(0).setSize(10).setExplain(false)
//                .get();
//        
//        System.out.println(response.toString());
        
        client.close();
	}

}
