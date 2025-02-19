package com.yuji.contentcore.fixed.config;

import com.yuji.common.core.constant.SecurityConstants;
import com.yuji.common.core.fixed.FixedConfig;
import com.yuji.common.core.utils.ArrayUtils;
import com.yuji.common.core.utils.SpringUtils;
import com.yuji.common.core.utils.StringUtils;
import com.yuji.system.api.RemoteConfigService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 允许上传的文件类型
 */
@Component(FixedConfig.BEAN_PREFIX + AllowUploadFileType.ID)
public class AllowUploadFileType extends FixedConfig {

	public static final String ID = "allow.upload.fileType";

	private static final RemoteConfigService remoteConfigService = SpringUtils.getBean(RemoteConfigService.class);

	/**
	 * 文件管理默认允许上传的文件类型
	 */
	public static final List<String> ALLOWED_UPLOAD_EXTENSION = List.of("bmp", "gif", "jpg", "jpeg", "png", "webp",
			"psd", "ai", "tif", "tiff", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "fla", "swf", "js", "css",
			"shtml", "html", "htm", "txt", "ttf", "eot", "mp4", "avi", "rmvb", "mpg", "flv", "mpeg", "rm", "mov", "wmv",
			"wmp", "mp3", "wma", "wav", "ogg", "rar", "zip", "gz", "bz2", "z", "iso", "cab", "jar");

	public AllowUploadFileType() {
		super(ID, "{CONFIG." + ID + "}", StringUtils.EMPTY,
				"默认支持：bmp,gif,jpg,jpeg,png,webp,psd,ai,tif,tiff,doc,docx,xls,xlsx,ppt,pptx,pdf,fla,swf,js,css,shtml,html,htm,txt,ttf,eot,mp4,avi,rmvb,mpg,flv,mpeg,rm,mov,wmv,wmp,mp3,wma,wav,ogg,rar,zip,gz,bz2,z,iso,cab,jar");
	}

	/**
	 * 指定文件名或后缀名是否允许上传的文件类型
	 * 
	 * @param ext
	 * @return
	 */
	public static boolean isAllow(String ext) {
		if (StringUtils.isEmpty(ext)) {
			return false;
		}
		if (ext.indexOf(".") > -1) {
			ext = StringUtils.substringAfterLast(ext, ".");
		}
		if (ALLOWED_UPLOAD_EXTENSION.contains(ext)) {
			return true;
		}
		String configValue = remoteConfigService.getConfigKey(ID, SecurityConstants.INNER).getData();
		if (StringUtils.isBlank(configValue)) {
			return false;
		}
		return ArrayUtils.indexOf(ext, configValue.split(",")) > -1;
	}
}
