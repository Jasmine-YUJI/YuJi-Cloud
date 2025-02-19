package com.yuji.contentcore.template.tag;

import com.yuji.common.core.utils.StringUtils;
import com.yuji.common.staticize.FreeMarkerUtils;
import com.yuji.common.staticize.enums.TagAttrDataType;
import com.yuji.common.staticize.tag.AbstractListTag;
import com.yuji.common.staticize.tag.TagAttr;
import com.yuji.contentcore.service.ICmsPublishpipeService;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CmsSitePropertyTag extends AbstractListTag {

	public final static String TAG_NAME = "cms_site_property";
	public final static String NAME = "{FREEMARKER.TAG.NAME." + TAG_NAME + "}";
	public final static String DESC = "{FREEMARKER.TAG.DESC." + TAG_NAME + "}";

	public final static String TagAttr_SiteId = "siteid";

	public final static String TagAttr_Code = "code";

	private final ICmsPublishpipeService cmsPublishpipeService;

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> tagAttrs = super.getTagAttrs();
		tagAttrs.add(new TagAttr(TagAttr_SiteId, false, TagAttrDataType.INTEGER, "站点ID，默认从模板变量中获取${Site.siteId}"));
		tagAttrs.add(new TagAttr(TagAttr_Code, false, TagAttrDataType.STRING, "属性编码"));
		return tagAttrs;
	}

	@Override
	public TagPageData prepareData(Environment env, Map<String, String> attrs, boolean page, int size, int pageIndex)
			throws TemplateException {
		long siteId = MapUtils.getLongValue(attrs, TagAttr_SiteId,
				FreeMarkerUtils.evalLongVariable(env, "Site.siteId"));
		if (siteId <= 0) {
			throw new TemplateException("站点数据ID异常：" + siteId, env);
		}
		String code = MapUtils.getString(attrs, TagAttr_Code);
		String condition = MapUtils.getString(attrs, TagAttr.AttrName_Condition);

		/*LambdaQueryWrapper<CmsSiteProperty> q = new LambdaQueryWrapper<CmsSiteProperty>()
				.eq(CmsSiteProperty::getSiteId, siteId)
				.eq(StringUtils.isNotEmpty(code), CmsSiteProperty::getPropCode, code);
		q.apply(StringUtils.isNotEmpty(condition), condition);
		Page<CmsSiteProperty> pageResult = this.sitePropertyService.page(new Page<>(pageIndex, size, page), q);
		if (pageIndex > 1 & pageResult.getRecords().isEmpty()) {
			throw new TemplateException("站点自定义属性数据列表页码超出上限：" + pageIndex, env);
		}
		return TagPageData.of(pageResult.getRecords(), pageResult.getTotal());*/
		return null;
	}

	@Override
	public String getTagName() {
		return TAG_NAME;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESC;
	}
}
