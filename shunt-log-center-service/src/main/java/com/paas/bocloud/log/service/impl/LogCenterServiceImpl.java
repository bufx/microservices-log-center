package com.paas.buubiu.log.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.paas.buubiu.common.exception.BuubiuException;
import com.paas.buubiu.log.model.qo.logcenter.AppLogQo;
import com.paas.buubiu.log.model.vo.logcenter.AppLogVo;
import com.paas.buubiu.log.service.LogCenterService;
import com.paas.buubiu.sw.entity.AbstractLogRecord;
import com.paas.buubiu.sw.exception.AccessLogExceptionCode;
import com.paas.buubiu.sw.service.ApmService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @program: 日志中心查询接口实现类
 * @author: buubiu
 * @create: 2021/11/22 16:42
 */
@Slf4j
@Service
public class LogCenterServiceImpl implements LogCenterService {

	@Value("${elk.log.max-size}")
	private Integer elkLogMaxSize;

	@Value("${elk.log.min-size}")
	private Integer elkLogMinSize;

	@Autowired
	private ApmService apmService;

	@Override
	public List<AppLogVo> appLog(AppLogQo appLogQo) {
		int size = Integer.parseInt(appLogQo.getSize());
		if (size <elkLogMinSize || size > elkLogMaxSize) {
			log.warn("每次查询日志条数超出设定的范围：{}-{}", elkLogMinSize,elkLogMaxSize);
			throw new BuubiuException(AccessLogExceptionCode.ES_SEARCH_LOG_MAX_SIZE_ERROR.getCode(),
				StrUtil.format(AccessLogExceptionCode.ES_SEARCH_LOG_MAX_SIZE_ERROR.getMsg(), elkLogMinSize, elkLogMaxSize));
		}
		SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		sourceBuilder.query(boolQueryBuilder);
		List<QueryBuilder> mustQueryList = boolQueryBuilder.must();

		if (StrUtil.isNotEmpty(appLogQo.getStart()) && StrUtil.isNotEmpty(appLogQo.getEnd())) {
			mustQueryList.add(QueryBuilders.rangeQuery(AbstractLogRecord.LOG_TIMESTAMP).gte(appLogQo.getStart())
				.lte(appLogQo.getEnd()).format("yyyy-MM-dd HH:mm:ss"));
		}
		if (StrUtil.isNotEmpty(appLogQo.getEnName())) {
			boolQueryBuilder.must().add(QueryBuilders.termQuery(AbstractLogRecord.APP_INDEX_KEYWORD, appLogQo.getEnName()));
		}
		if (StrUtil.isNotEmpty(appLogQo.getKeyWord())) {
			boolQueryBuilder.must().add(QueryBuilders.termQuery(AbstractLogRecord.MESSAGE_INFO, appLogQo.getKeyWord()));
		}
		if (StrUtil.isNotEmpty(appLogQo.getLevel())) {
			boolQueryBuilder.must().add(QueryBuilders.termQuery(AbstractLogRecord.LEVEL_KEYWORD, appLogQo.getLevel()));
		}
		if (StrUtil.isNotEmpty(appLogQo.getAfter())) {
			sourceBuilder.searchAfter(new Object[]{appLogQo.getAfter()});
		}
		sourceBuilder.sort(AbstractLogRecord.LOG_TIMESTAMP, SortOrder.DESC).size(size).query(boolQueryBuilder);

		SearchResponse search = apmService.searchEsLog(sourceBuilder);
		SearchHit[] hits = search.getHits().getHits();

		List<AppLogVo> appLogVoList = new ArrayList<>();
		for (SearchHit hit : hits) {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			AppLogVo appLogVo = new AppLogVo();
			appLogVo.setTime(
				DateUtil.format(DateUtil.parseUTC(sourceAsMap.get(AbstractLogRecord.LOG_TIMESTAMP).toString()),
					DatePattern.NORM_DATETIME_MS_PATTERN));
			appLogVo.setLevel(sourceAsMap.get(AbstractLogRecord.LEVEL).toString());
			appLogVo.setInfo(sourceAsMap.get(AbstractLogRecord.MESSAGE_INFO).toString());
			appLogVo.setClassName(sourceAsMap.get(AbstractLogRecord.LOGGER_NAME).toString());
			appLogVo.setAfter(hit.getSortValues()[0].toString());
			appLogVoList.add(appLogVo);
		}
		return appLogVoList;
	}
}
