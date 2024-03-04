package org.example.client;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class ESTest_Doc_Query {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
        System.out.println("==============Begin 查询索引中全部的数据=============");
        // 1. 查询索引中全部的数据
        SearchRequest simpleQeuryRequest = new SearchRequest();
        simpleQeuryRequest.indices("user");

        simpleQeuryRequest.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));

        SearchResponse simpleQueryResponse = esClient.search(simpleQeuryRequest, RequestOptions.DEFAULT);

        SearchHits hits = simpleQueryResponse.getHits();

        System.out.println(hits.getTotalHits());
        System.out.println(simpleQueryResponse.getTook());

        for ( SearchHit hit : hits ) {
            System.out.println(hit.getSourceAsString());
        }

        System.out.println("==============END 查询索引中全部的数据=============");

        // 2. 条件查询 : termQuery
        System.out.println("==============Begin 条件查询=============");
        SearchRequest termQueryRequest = new SearchRequest();
        termQueryRequest.indices("user");

        termQueryRequest.source(new SearchSourceBuilder().query(QueryBuilders.termQuery("age", 30)));
        SearchResponse termQueryResponse = esClient.search(termQueryRequest, RequestOptions.DEFAULT);

        SearchHits termQueryHits = termQueryResponse.getHits();

        System.out.println(termQueryHits.getTotalHits());
        System.out.println(termQueryResponse.getTook());

        for ( SearchHit hit : termQueryHits ) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==============END 条件查询=============");


        // 3. 分页查询
        System.out.println("==============Begin 分页查询=============");
        SearchRequest pageRequest = new SearchRequest();
        pageRequest.indices("user");

        SearchSourceBuilder builderPage = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        // (当前页码-1)*每页显示数据条数
        builderPage.from(2);
        builderPage.size(2);
        pageRequest.source(builderPage);
        SearchResponse responsePage = esClient.search(pageRequest, RequestOptions.DEFAULT);

        SearchHits hitsPage = responsePage.getHits();

        System.out.println(hitsPage.getTotalHits());
        System.out.println(responsePage.getTook());

        for ( SearchHit hit : hitsPage ) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==============END 分页查询=============");


        // 4. 查询排序
        System.out.println("==============Begin 查询排序=============");
        SearchRequest orderDescRequest = new SearchRequest();
        orderDescRequest.indices("user");

        SearchSourceBuilder builderOrderDesc = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        builderOrderDesc.sort("age", SortOrder.DESC);

        orderDescRequest.source(builderOrderDesc);
        SearchResponse responseOrderDesc = esClient.search(orderDescRequest, RequestOptions.DEFAULT);

        SearchHits hitsOrderDesc = responseOrderDesc.getHits();

        System.out.println(hitsOrderDesc.getTotalHits());
        System.out.println(responseOrderDesc.getTook());

        for ( SearchHit hit : hitsOrderDesc ) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==============END 查询排序=============");

        // 5. 过滤字段
        System.out.println("==============Begin 过滤字段=============");
        SearchRequest requestFilter = new SearchRequest();
        requestFilter.indices("user");

        SearchSourceBuilder builderFilter = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        //
        String[] excludes = {"age"};
        String[] includes = {};
        builderFilter.fetchSource(includes, excludes);

        requestFilter.source(builderFilter);
        SearchResponse responseFilter = esClient.search(requestFilter, RequestOptions.DEFAULT);

        SearchHits hitsFilter = responseFilter.getHits();

        System.out.println(hitsFilter.getTotalHits());
        System.out.println(responseFilter.getTook());

        for ( SearchHit hit : hitsFilter ) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==============END 过滤字段=============");

        // 6. 组合查询
        System.out.println("==============Begin 组合查询=============");
        SearchRequest requestCombination = new SearchRequest();
        requestCombination.indices("user");

        SearchSourceBuilder builderCombination = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilderCombination = QueryBuilders.boolQuery();

        //boolQueryBuilderCombination.must(QueryBuilders.matchQuery("age", 30));
        boolQueryBuilderCombination.must(QueryBuilders.matchQuery("sex", "男"));
        //boolQueryBuilderCombination.mustNot(QueryBuilders.matchQuery("sex", "男"));
        boolQueryBuilderCombination.should(QueryBuilders.matchQuery("age", 30));
        boolQueryBuilderCombination.should(QueryBuilders.matchQuery("age", 40));

        builderCombination.query(boolQueryBuilderCombination);

        requestCombination.source(builderCombination);
        SearchResponse responseCombination = esClient.search(requestCombination, RequestOptions.DEFAULT);

        SearchHits hitsCombination = responseCombination.getHits();

        System.out.println(hitsCombination.getTotalHits());
        System.out.println(responseCombination.getTook());

        for ( SearchHit hit : hitsCombination ) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==============END 组合查询=============");

        // 7. 范围查询
        System.out.println("==============Begin 范围查询=============");
        SearchRequest requestRange = new SearchRequest();
        requestRange.indices("user");

        SearchSourceBuilder builderRange = new SearchSourceBuilder();
        RangeQueryBuilder rangeQueryRange = QueryBuilders.rangeQuery("age");

        rangeQueryRange.gte(30);
        rangeQueryRange.lt(50);

        builderRange.query(rangeQueryRange);

        requestRange.source(builderRange);
        SearchResponse responseRange = esClient.search(requestRange, RequestOptions.DEFAULT);

        SearchHits hitsRange = responseRange.getHits();

        System.out.println(hitsRange.getTotalHits());
        System.out.println(responseRange.getTook());

        for ( SearchHit hit : hitsRange ) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==============END 范围查询=============");

        // 8. 模糊查询
        System.out.println("==============Begin 模糊查询=============");
        SearchRequest requestLike = new SearchRequest();
        requestLike.indices("user");

        SearchSourceBuilder builderLike = new SearchSourceBuilder();
        builderLike.query(QueryBuilders.fuzzyQuery("name", "wangwu").fuzziness(Fuzziness.TWO));

        requestLike.source(builderLike);
        SearchResponse responseLike = esClient.search(requestLike, RequestOptions.DEFAULT);

        SearchHits hitsLike = responseLike.getHits();

        System.out.println(hitsLike.getTotalHits());
        System.out.println(responseLike.getTook());

        for ( SearchHit hit : hitsLike ) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==============END 模糊查询=============");

        // 9. 高亮查询
        System.out.println("==============Begin 高亮查询=============");
        SearchRequest requestHight = new SearchRequest();
        requestHight.indices("user");

        SearchSourceBuilder builderHight = new SearchSourceBuilder();
        TermsQueryBuilder termsQueryBuilderHight = QueryBuilders.termsQuery("name", "zhangsan");

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("name");

        builderHight.highlighter(highlightBuilder);
        builderHight.query(termsQueryBuilderHight);

        requestHight.source(builderHight);
        SearchResponse responseHight = esClient.search(requestHight, RequestOptions.DEFAULT);

        SearchHits hitsHight = responseHight.getHits();

        System.out.println(hitsHight.getTotalHits());
        System.out.println(responseHight.getTook());

        for ( SearchHit hit : hitsHight ) {
            System.out.println(hit.getSourceAsString()+"   高亮字段："+hit.getHighlightFields().toString());
        }
        System.out.println("==============END 高亮查询=============");


        // 10. 聚合查询
        System.out.println("==============Begin 聚合查询=============");
        SearchRequest requestAggregation = new SearchRequest();
        requestAggregation.indices("user");

        SearchSourceBuilder builderAggregation = new SearchSourceBuilder();

        AggregationBuilder aggregationBuilder = AggregationBuilders.max("maxAge").field("age");
        builderAggregation.aggregation(aggregationBuilder);

        requestAggregation.source(builderAggregation);
        SearchResponse responseAggregation = esClient.search(requestAggregation, RequestOptions.DEFAULT);

        SearchHits hitsAggregation = responseAggregation.getHits();

        System.out.println(hitsAggregation.getTotalHits());
        System.out.println(responseAggregation.getTook());

        for ( SearchHit hit : hitsAggregation ) {
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("==============END 聚合查询=============");


        // 11. 分组查询
        System.out.println("==============END 分组查询=============");
        SearchRequest requestGroup = new SearchRequest();
        requestGroup.indices("user");

        SearchSourceBuilder builderGroup = new SearchSourceBuilder();

        AggregationBuilder aggregationBuilderGroup = AggregationBuilders.terms("ageGroup").field("age");
        builderGroup.aggregation(aggregationBuilderGroup);

        requestGroup.source(builderGroup);
        SearchResponse responseGroup = esClient.search(requestGroup, RequestOptions.DEFAULT);

        SearchHits hitsGroup = responseGroup.getHits();

        System.out.println(hitsGroup.getTotalHits());
        System.out.println(responseGroup.getTook());

        for ( SearchHit hit : hitsGroup ) {
            System.out.println(hit.getSourceAsString());
        }

        System.out.println("==============END 分组查询=============");

        esClient.close();
    }
}
