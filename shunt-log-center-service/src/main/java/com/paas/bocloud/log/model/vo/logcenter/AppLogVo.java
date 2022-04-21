package com.paas.buubiu.log.model.vo.logcenter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @program: 获取应用的日志信息-查询结果
 * @author: buubiu
 * @create: 2021/11/22 16:56
 */

@ApiModel
@Data
public class AppLogVo {

	@ApiModelProperty("时间，格式：2021-11-22 17:11:10")
	private String time;

	@ApiModelProperty("日志级别")
	private String level;

	@ApiModelProperty("日志类名")
	private String className;

	@ApiModelProperty("日志信息")
	private String info;

	@ApiModelProperty("after字段")
	private String after;
}
