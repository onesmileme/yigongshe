package com.weikan.app.util;

public class VSErrorResponseHandler {

	// | 10001 | System error | 系统错误 |
	// | 10002 | Service unavailable | 服务暂停 |
	// | 10003 | Remote service error | 远程服务错误 |
	// | 10004 | IP limit | IP限制不能请求该资源 |
	// | 10005 | Illegal request | 非法请求 |
	// | 10006 | Invalid web user | 不合法的网站用户 |
	// | 10007 | Miss required parameter (%s) , see doc for more info | 缺失必选参数
	// (%s)，请参考API文档 |
	// | 10008 | Parameter (%s)\'s value invalid, expect (%s) , but get (%s) ,
	// see doc for more info | 参数值(%s)非法，需为 (%s)，实际为 (%s)，请参考API文档 |
	// | 10009 | HTTP method is not suported for this request | 请求的HTTP
	// METHOD不支持，请检查是否选择了正确的POST/GET方式 |
	// | 10010 | Permission denied, need a high level appkey | 该资源需要appkey拥有授权 |
	// | 10011 | Param error, see doc for more info | 参数错误，请参考API文档 |

	// | 20001 | IDs is null | IDs参数为空 |
	// | 20002 | Uid parameter is null | uid参数为空 |
	// | 20003 | User does not exists | 用户不存在 |
	// | 20004 | Unsupported image type, only suport JPG, GIF, PNG |
	// 不支持的图片类型，仅仅支持JPG、GIF、PNG |
	// | 20005 | Image size too large | 图片太大 |
	// | 20006 | Content is null | 内容为空 |
	// | 20007 | IDs is too many | IDs参数太长了 |
	// | 20008 | Text too long, please input text less than 140 characters |
	// 输入文字太长，请确认不超过140个字符输入文字太长，请确认不超过140个字符 |
	// | 20009 | Text too long, please input text less than 300 characters |
	// 输入文字太长，请确认不超过300个字符 |
	// | 20010 | Param is error, please try again | 安全检查参数有误，请再调用一次 |
	// | 20011 | Account or ip or app is illgal, can not continue |
	// 账号、IP或应用非法，暂时无法完成此操作 |
	// | 20012 | Out of limit | 发布内容过于频繁 |
	// | 20013 | epeat content | 提交相似的信息 |
	// | 20014 | Contain illegal website' | 包含非法网址 |
	// | 20015 | Repeat conetnt | 提交相同的信息 |
	// | 20016 | Contain advertising | 包含广告信息 |
	// | 20017 | Content is illegal | 包含非法内容 |
	// | 20018 | Your ip\'s behave in a comic boisterous or unruly manner |
	// 此IP地址上的行为异常 |
	// | 20019 | Test and verify | 需要验证码 |
	// | 20020 | Update success, while server slow now, please wait 1-2 minutes
	// | 发布成功，目前服务器可能会有延迟，请耐心等待1-2分钟 |
	// | 20021 | The model does not exist | 指定模型不存在 |
	// | 20022 | No find any information | 未检索到指定信息 |
	// | - | 用户模块 | - |
	// | 20101 | ownership of information is not correct | 信息归属不正确 |
	// | 20102 | Uid parameter is null | uid参数为空 |
	// | 20103 | This phone have used | 用户已存在 |
	// | 20104 | This user is not activated | 用户未激活 |
	// | 20105 | This user is locked | 用户异常锁定 |
	// | 20106 | This user is locked on today | 用户临时锁定 |
	// | 20107 | This account has been claimed | 用户已经被认领 |
	// | - | 邮件模块 | - |
	// | 20201 | This mail address is not valid | 邮箱地址不合法 |
	// | 20202 | Email authentication information does not exist | 邮件验证信息不存在 |
	// | 20203 | Email authentication information failure | 邮件验证信息失效 |
	// | 20204 | Email authentication information is not correct | 邮件验证信息不正确 |
	// | 20205 | This mail address is empty | 邮箱地址不能为空 |
	// | - | 邮件模块 | - |
	// | 20301 | This phone number format error | 手机号码格式错误 |
	// | 20302 | Sms authentication information does not exist | 邮件验证信息不存在 |
	// | 20303 | Sms authentication information failure | 邮件验证信息失效 |
	// | 20304 | Sms authentication information is not correct | 邮件验证信息不正确 |
	// | 20305 | On the same day to use text messaging have reached the maximum
	// number of times | 当天短信使用次数已达上限 |
	// | 20306 | Do not meet the message using the minimum interval |
	// 不满足短信使用最小间隔 |
	// | 20307 | This phone number is empty | 手机号码不能为空 |
	// | 20308 | Token failed to get | token获取失败 |
	// | 20309 | Get token error | token获取异常 |
	// | - | 数据库提示 | - |
	// | 20401 | (%s) failed to add | (%s)新增失败 |
	// | 20402 | (%s) failed to delete | (%s)删除失败 |
	// | 20403 | (%s) update failed | (%s)更新失败 |
	// | 20404 | (%s) nothing changed | (%s)没有发生变化 |
	// | - | 上传提示 | - |
	// | 20500 | unknown error | 未知的错误 |
	// | 20501 | The uploaded file exceeds the upload_max_filesize directive in
	// php.ini | 上传的文件超过了 php.ini 中 upload_max_filesize 选项限制的值。 |
	// | 20502 | The uploaded file exceeds the MAX_FILE_SIZE directive that was
	// specified in the HTML form | 上传文件的大小超过了 HTML 表单中 MAX_FILE_SIZE 选项指定的值 |
	// | 20503 | The uploaded file was only partially uploaded | 文件只有部分被上传 |
	// | 20504 | No file was uploaded | 没有文件被上传 |
	// | 20505 | 0000 | 0000 |
	// | 20506 | Missing a temporary folder. | 找不到临时文件夹 |
	// | 20507 | Failed to write file to disk | 文件写入失败 |
	// | 20508 | A PHP extension stopped the file upload | 缺少php扩展 |
	// | 20509 | Unsupported image type, only suport JPG, GIF, PNG |
	// 不支持的图片类型，仅仅支持JPG、GIF、PNG |
	// | 20510 | Image size too large | 图片太大 |
	// | 20511 | Image style doesnot exists | 图片样式不存在 |
	// | 20512 | file_get_contents function failed | file_get_contents
	// 函数获取图片内容失败 |

	public static String getErrMessage(int errNO) {
		String messageString = "网络错误";
		switch (errNO) {
		case 10001:
			messageString = "系统错误";
			break;
		case 10002:
			messageString = "服务暂停";
			break;
		case 10003:
			messageString = "远程服务错误";
			break;
		case 10004:
			messageString = "IP限制不能请求该资源";
			break;
		case 10005:
			messageString = "非法请求";
			break;
		case 10006:
			messageString = "用户名或密码错误";//"不合法的网站用户";
			break;
		case 10007:
			messageString = "缺失必选参数 (%s)，请参考API文档";
			break;
		case 10008:
			messageString = "参数值错误";
			break;
		case 10009:
			messageString = "请求的HTTP METHOD不支持，请检查是否选择了正确的POST/GET方式";
			break;
		case 10010:
			messageString = "该资源需要appkey拥有授权";
			break;
		case 10011:
			messageString = "参数错误，请参考API文档";
			break;
		case 20001:
			messageString = "IDs参数为空";
			break;
		case 20002:
			messageString = "uid参数为空";
			break;
		case 20003:
			messageString = "用户不存在";
			break;
		case 20004:
			messageString = "不支持的图片类型，仅仅支持JPG、GIF、PNG";
			break;
		case 20005:
			messageString = " 图片太大";
			break;
		case 20006:
			messageString = "内容为空";
			break;
		case 20007:
			messageString = "IDs参数太长了";
			break;
		case 20008:
			messageString = "输入文字太长，请确认不超过140个字符输入文字太长，请确认不超过140个字符";
			break;
		case 20009:
			messageString = "输入文字太长，请确认不超过300个字符";
			break;
		case 20010:
			messageString = "安全检查参数有误，请再调用一次";
			break;
		case 20011:
			messageString = "账号、IP或应用非法，暂时无法完成此操作";
			break;
		case 20012:
			messageString = "发布内容过于频繁";
			break;
		case 20013:
			messageString = "提交相似的信息";
			break;
		case 20014:
			messageString = "包含非法网址";
			break;
		case 20015:
			messageString = "提交相同的信息";
			break;
		case 20016:
			messageString = "包含广告信息";
			break;
		case 20017:
			messageString = "包含非法内容";
			break;
		case 20018:
			messageString = "IP地址上的行为异常";
			break;
		case 20019:
			messageString = "需要验证码";
			break;
		case 20020:
			messageString = "发布成功，目前服务器可能会有延迟，请耐心等待1-2分钟";
			break;
		case 20021:
			messageString = "指定模型不存在";
			break;
		case 20022:
			messageString = "未检索到指定信息";
			break;
		// | - | 用户模块 | - |
		// | 20101 | ownership of information is not correct | 信息归属不正确 |
		// | 20102 | Uid parameter is null | uid参数为空 |
		// | 20103 | This phone have used | 用户已存在 |
		// | 20104 | This user is not activated | 用户未激活 |
		// | 20105 | This user is locked | 用户异常锁定 |
		// | 20106 | This user is locked on today | 用户临时锁定 |
		// | 20107 | This account has been claimed | 用户已经被认领 |
		case 20101:
			messageString = "信息归属不正确";
			break;
		case 20102:
			messageString = "uid参数为空";
			break;
		case 20103:
			messageString = "用户已存在";
			break;
		case 20104:
			messageString = "用户未激活 ";
			break;
		case 20105:
			messageString = "用户异常锁定";
			break;
		case 20106:
			messageString = "用户临时锁定";
			break;
		case 20107:
			messageString = "用户已经被认领";
			break;

		// | - | 邮件模块 | - |
		// | 20201 | This mail address is not valid | 邮箱地址不合法 |
		// | 20202 | Email authentication information does not exist | 邮件验证信息不存在
		// |
		// | 20203 | Email authentication information failure | 邮件验证信息失效 |
		// | 20204 | Email authentication information is not correct | 邮件验证信息不正确
		// |
		// | 20205 | This mail address is empty | 邮箱地址不能为空 |
		case 20201:
			messageString = "邮箱地址不合法";
			break;
		case 20202:
			messageString = "邮件验证信息不存在";
			break;
		case 20203:
			messageString = "邮件验证信息失效";
			break;
		case 20204:
			messageString = "邮件验证信息不正确";
			break;
		case 20205:
			messageString = "邮箱地址不能为空";
			break;

		// | - | 邮件模块 | - |
		// | 20301 | This phone number format error | 手机号码格式错误 |
		// | 20302 | Sms authentication information does not exist | 邮件验证信息不存在 |
		// | 20303 | Sms authentication information failure | 邮件验证信息失效 |
		// | 20304 | Sms authentication information is not correct | 邮件验证信息不正确 |
		// | 20305 | On the same day to use text messaging have reached the
		// maximum
		// number of times | 当天短信使用次数已达上限 |
		// | 20306 | Do not meet the message using the minimum interval |
		// 不满足短信使用最小间隔 |
		// | 20307 | This phone number is empty | 手机号码不能为空 |
		// | 20308 | Token failed to get | token获取失败 |
		// | 20309 | Get token error | token获取异常 |
		case 20301:
			messageString = "手机号码格式错误";
			break;
		case 20302:
			messageString = "邮件验证信息不存在";
			break;
		case 20303:
			messageString = "邮件验证信息失效";
			break;
		case 20304:
			messageString = "邮件验证信息不正确";
			break;
		case 20305:
			messageString = "当天短信使用次数已达上限";
			break;
		case 20306:
			messageString = "不满足短信使用最小间隔 ";
			break;
		case 20307:
			messageString = "手机号码不能为空 ";
			break;
		case 20308:
			messageString = "token获取失败 ";
			break;
		case 20309:
			messageString = "token获取异常 ";
			break;

		// | - | 数据库提示 | - |
		// | 20401 | (%s) failed to add | (%s)新增失败 |
		// | 20402 | (%s) failed to delete | (%s)删除失败 |
		// | 20403 | (%s) update failed | (%s)更新失败 |
		// | 20404 | (%s) nothing changed | (%s)没有发生变化 |
		case 20401:
			messageString = "新增失败";
			break;
		case 20402:
			messageString = "删除失败";
			break;
		case 20403:
			messageString = "更新失败";
			break;
		case 20404:
			messageString = "没有发生变化";
			break;

		case 20500:
			messageString = "未知的错误";
			break;
		case 20501:
			messageString = "传的文件超过了 php.ini 中 upload_max_filesize 选项限制的值。";
			break;
		case 20502:
			messageString = "上传文件的大小超过了 HTML 表单中 MAX_FILE_SIZE 选项指定的值";
			break;
		case 20503:
			messageString = "文件只有部分被上传";
			break;
		case 20504:
			messageString = "没有文件被上传";
			break;
		case 20505:
			messageString = "0000";
			break;
		case 20506:
			messageString = "找不到临时文件夹";
			break;
		case 20507:
			messageString = "文件写入失败 ";
			break;
		case 20508:
			messageString = "缺少php扩展";
			break;
		case 20509:
			messageString = "不支持的图片类型，仅仅支持JPG、GIF、PNG ";
			break;
		case 205010:
			messageString = "图片太大";
			break;
		case 20511:
			messageString = "图片样式不存在";
			break;
		case 20512:
			messageString = "函数获取图片内容失败";
			break;
		case 21001:
			messageString = "此号码已存在";
			break;
		default:
			break;
		}
		return messageString;
	}
}
