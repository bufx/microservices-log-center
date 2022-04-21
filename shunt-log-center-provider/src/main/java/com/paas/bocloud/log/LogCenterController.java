package com.paas.buubiu.log;

import com.paas.buubiu.common.http.response.DataResponse;
import com.paas.buubiu.log.model.qo.logcenter.AppLogQo;
import com.paas.buubiu.log.service.LogCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @program: 日志中心查询控制类
 * @author: buubiu
 * @create: 2021/11/22 16:36
 */

@Api(tags = "日志中心查询相关接口")
@RestController
@RequestMapping("log_center")
public class LogCenterController {

	@Autowired
	private LogCenterService logCenterService;

	@ApiOperation("获取应用的日志")
	@PostMapping("app_log")
	public DataResponse appLog(@RequestBody @Valid AppLogQo appLogQo) {
		return DataResponse.success(logCenterService.appLog(appLogQo));
	}
}
