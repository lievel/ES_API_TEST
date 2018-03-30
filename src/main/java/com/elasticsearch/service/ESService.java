package com.elasticsearch.service;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import com.elasticsearch.common.ESConnector;

public class ESService {
	
	public static SearchResponse ESServiceRequest(QueryBuilder queryBuilder, AggregationBuilder aggregationBuilder) throws Exception{
		
		SearchRequestBuilder searchRequestBuilder = ESConnector.getInstance().getClient().prepareSearch()
        		.setIndices(ESConnector.INDEX_NAME)
                .setTypes(ESConnector.TYPE_NAME)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(0)
                .setSize(0)
                .setExplain(false)
                .setQuery(queryBuilder)
                .addAggregation(aggregationBuilder);

        
        SearchResponse searchResponse =  searchRequestBuilder.execute().actionGet();
        
        //임시
        ESConnector.getInstance().getClient().close();
        
        return searchResponse;
		
	}
}
