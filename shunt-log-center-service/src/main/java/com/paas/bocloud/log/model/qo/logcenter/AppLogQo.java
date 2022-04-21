package com.paas.buubiu.log.model.qo.logcenter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

/**
 *
 * @program: 获取应用的日志信息-查询条件
 * @author: buubiu
 * @create: 2021/11/22 16:48
 */

@ApiModel
@Data
public class AppLogQo {

	@NotNull(message = "应用英文名称[enName]不能为空")
	@ApiModelProperty("应用英文名称")
	private String enName;

	@ApiModelProperty("关键字")
	private String keyWord;

	@ApiModelProperty("日志级别")
	private String level;

	@ApiModelProperty("开始时间，格式：2021-11-22 17:11:10")
	private String start;

	@ApiModelProperty("结束时间，格式：2021-11-22 17:11:10")
	private String end;

	@ApiModelProperty("上一条记录after的值")
	private String after;

	@ApiModelProperty("每次加载的条数，默认50条")
	@Pattern(regexp = "^[1-9]\\d*$",message = "参数[size]须为正整数")
	private String size = "50";
}
