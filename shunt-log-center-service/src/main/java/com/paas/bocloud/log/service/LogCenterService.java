package com.paas.buubiu.log.service;

import com.paas.buubiu.log.model.qo.logcenter.AppLogQo;
import com.paas.buubiu.log.model.vo.logcenter.AppLogVo;
import java.util.List;

/**
 *
 * @program: 日志中心查询接口
 * @author: buubiu
 * @create: 2021/11/22 16:41
 */
public interface LogCenterService {

	/**
	 * 获取应用日志信息
	 * @author: buubiu
	 * @create: 2021/11/22 16:59
	 * @param appLogQo 日志查询条件
	 * @return {@link List<AppLogQo>}
	 */
	List<AppLogVo> appLog(AppLogQo appLogQo);
}
