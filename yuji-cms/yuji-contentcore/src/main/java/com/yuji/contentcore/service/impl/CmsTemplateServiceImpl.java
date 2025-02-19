package com.yuji.contentcore.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.yuji.common.core.utils.DateUtils;
import com.yuji.common.core.utils.StringUtils;
import com.yuji.common.core.utils.file.FileTypeUtils;
import com.yuji.common.redis.service.RedisService;
import com.yuji.contentcore.ContentCoreConsts;
import com.yuji.contentcore.domain.CmsSite;
import com.yuji.contentcore.domain.dto.TemplateRenameDTO;
import com.yuji.contentcore.domain.dto.TemplateUpdateDTO;
import com.yuji.contentcore.exception.ContentCoreErrorCode;
import com.yuji.contentcore.mapper.CmsSiteMapper;
import com.yuji.contentcore.template.ITemplateType;
import com.yuji.contentcore.utils.SiteUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuji.contentcore.mapper.CmsTemplateMapper;
import com.yuji.contentcore.domain.CmsTemplate;
import com.yuji.contentcore.service.ICmsTemplateService;

/**
 * 模板管理Service业务层处理
 * 
 * @author Liguoqiang
 * @date 2024-06-06
 */
@Service
public class CmsTemplateServiceImpl implements ICmsTemplateService 
{
    private final static String TEMPLATE_STATIC_CONTENT_CACHE_KEY_PREFIX = "cms:template:";

    @Autowired
    private CmsTemplateMapper cmsTemplateMapper;

    @Autowired
    private CmsSiteMapper cmsSiteMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private Map<String, ITemplateType> templateTypes;

    /**
     * 查询模板管理
     * 
     * @param templateId 模板管理主键
     * @return 模板管理
     */
    @Override
    public CmsTemplate selectCmsTemplateByTemplateId(Long templateId)
    {
        return cmsTemplateMapper.selectCmsTemplateByTemplateId(templateId);
    }

    /**
     * 查询模板管理列表
     * 
     * @param cmsTemplate 模板管理
     * @return 模板管理
     */
    @Override
    public List<CmsTemplate> selectCmsTemplateList(CmsTemplate cmsTemplate)
    {
        return cmsTemplateMapper.selectCmsTemplateList(cmsTemplate);
    }

    @Override
    public List<CmsTemplate>  selectCmsTemplateByTemplateIdInList(Long[] templateIds){
        return cmsTemplateMapper.selectCmsTemplateByTemplateIdInList(templateIds);
    }

    /**
     * 新增模板管理
     * 
     * @param cmsTemplate 模板管理
     * @return 结果
     */
    @Override
    public int insertCmsTemplate(CmsTemplate cmsTemplate)
    {
        cmsTemplate.setCreateTime(DateUtils.getNowDate());
        return cmsTemplateMapper.insertCmsTemplate(cmsTemplate);
    }

    /**
     * 修改模板管理
     * 
     * @param cmsTemplate 模板管理
     * @return 结果
     */
    @Override
    public int updateCmsTemplate(CmsTemplate cmsTemplate)
    {
        cmsTemplate.setUpdateTime(DateUtils.getNowDate());
        return cmsTemplateMapper.updateCmsTemplate(cmsTemplate);
    }

    /**
     * 批量删除模板管理
     * 
     * @param templateIds 需要删除的模板管理主键
     * @return 结果
     */
    @Override
    public int deleteCmsTemplateByTemplateIds(Long[] templateIds)
    {
        return cmsTemplateMapper.deleteCmsTemplateByTemplateIds(templateIds);
    }

    /**
     * 删除模板管理信息
     * 
     * @param templateId 模板管理主键
     * @return 结果
     */
    @Override
    public int deleteCmsTemplateByTemplateId(Long templateId)
    {
        return cmsTemplateMapper.deleteCmsTemplateByTemplateId(templateId);
    }

    /**
     * 查找模板文件
     *
     * @param site 站点信息
     * @param templatePath 模板路径
     * @param publishPipeCode 发布通道编码
     */
    @Override
    public File findTemplateFile(CmsSite site, String templatePath, String publishPipeCode) {
        if (StringUtils.isEmpty(templatePath)) {
            return null;
        }
        String siteRoot = SiteUtils.getSiteRoot(site, publishPipeCode);
        File templateFile = new File(siteRoot + ContentCoreConsts.TemplateDirectory + templatePath);
        if (!templateFile.exists() || !templateFile.isFile()) {
            return null;
        }
        return templateFile;
    }

    @Override
    public String getTemplateStaticContentCache(String templateKey) {
        return redisService.getCacheObject(TEMPLATE_STATIC_CONTENT_CACHE_KEY_PREFIX + templateKey);
    }

    @Override
    public void setTemplateStaticContentCache(String templateKey, String staticContent) {
        redisService.setCacheObject(TEMPLATE_STATIC_CONTENT_CACHE_KEY_PREFIX + templateKey, staticContent, 24L, TimeUnit.HOURS);
    }

    @Override
    public ITemplateType getTemplateType(String typeId) {
        return this.templateTypes.get(ITemplateType.BEAN_NAME_PREFIX + typeId);
    }

    @Override
    public int saveTemplate(CmsTemplate template, TemplateUpdateDTO dto) throws IOException {
        template.setContent(dto.getContent());
        template.setRemark(dto.getRemark());
        // 变更文件内容
        File file = this.getTemplateFile(template);
        FileTypeUtils.mkdirs(file.getParentFile().getAbsolutePath());

        FileUtils.writeStringToFile(file, dto.getContent(), StandardCharsets.UTF_8);

        template.setModifyTime(file.lastModified());
        template.setUpdateBy(dto.getOperator());
        int res = cmsTemplateMapper.updateCmsTemplate(template);
        // 清理include缓存
        this.clearTemplateStaticContentCache(template);
        return res;
    }

    @Override
    public int renameTemplate(CmsTemplate template, TemplateRenameDTO dto)  throws IOException {
        String newPath = FileTypeUtils.normalizePath(dto.getPath());
        if (!template.getPath().equals(newPath)) {
            CmsSite site = this.cmsSiteMapper.selectCmsSiteBySiteId(template.getSiteId());
            String siteRoot = SiteUtils.getSiteRoot(site, template.getPublishPipeCode());
            File file = new File(siteRoot + ContentCoreConsts.TemplateDirectory + template.getPath());
            File dest = new File(siteRoot + ContentCoreConsts.TemplateDirectory + newPath);
            FileUtils.moveFile(file, dest);
            template.setPath(newPath);
        }
        template.setRemark(dto.getRemark());
        template.setUpdateBy(dto.getOperator());
        return cmsTemplateMapper.updateCmsTemplate(template);
    }

    @Override
    public void clearTemplateStaticContentCache(String templateKey) {
        this.redisService.deleteObject(TEMPLATE_STATIC_CONTENT_CACHE_KEY_PREFIX + templateKey);
    }

    @Override
    public File getTemplateFile(CmsTemplate template) {
        CmsSite site = this.cmsSiteMapper.selectCmsSiteBySiteId(template.getSiteId());
        String siteRoot = SiteUtils.getSiteRoot(site, template.getPublishPipeCode());
        File templateFile = new File(siteRoot + ContentCoreConsts.TemplateDirectory + template.getPath());
        if (templateFile.exists() && !templateFile.isFile()) {
            throw ContentCoreErrorCode.TEMPLATE_PATH_EXISTS.exception();
        }
        return templateFile;
    }

    private void clearTemplateStaticContentCache(CmsTemplate template) {
        CmsSite site = this.cmsSiteMapper.selectCmsSiteBySiteId(template.getSiteId());
        String templateKey = SiteUtils.getTemplateKey(site, template.getPublishPipeCode(), template.getPath());
        this.clearTemplateStaticContentCache(templateKey);
    }
}
