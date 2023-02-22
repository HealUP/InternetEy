package com.dengzh.wyy.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dengzh.wyy.model.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * HospitalSet
 * </p>
 *
 * @author Dengzh
 */
@Data
@ApiModel(description = "医院设置")
@TableName("hospital_set")
public class saveHospitalSet extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(type = IdType.ASSIGN_ID)
	@JsonProperty("id")
	//此句为问题关键 相当于吧Long转换为String
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JSONField(serializeUsing = ToStringSerializer.class)
	private Long id;

	@ApiModelProperty(value = "医院名称")
	@TableField("hosname")
	private String hosname;

	@ApiModelProperty(value = "医院编号")
	@TableField("hoscode")
	private String hoscode;

	@ApiModelProperty(value = "api基础路径")
	@TableField("api_url")
	private String apiUrl;

	@ApiModelProperty(value = "签名秘钥")
	@TableField("sign_key")
	private String signKey;

	@ApiModelProperty(value = "联系人姓名")
	@TableField("contacts_name")
	private String contactsName;

	@ApiModelProperty(value = "联系人手机")
	@TableField("contacts_phone")
	private String contactsPhone;

	@ApiModelProperty(value = "状态")
	@TableField("status")
	private Integer status;

}

