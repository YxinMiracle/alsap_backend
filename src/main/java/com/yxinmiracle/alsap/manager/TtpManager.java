package com.yxinmiracle.alsap.manager;

/*
 * @author  YxinMiracle
 * @date  2024-09-23 19:17
 * @Gitee: https://gitee.com/yxinmiracle
 */


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.model.enums.FileUploadBizEnum;
import com.yxinmiracle.alsap.model.meta.TtpMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Component
@Slf4j
public class TtpManager {

    @Resource
    private CosManager cosManager;

    public String buildTtpConfigData(List<TtpMeta.TechniquesConfig> aiServerTtpResList, Long ctiId) {
        // 1. 读取配置文件，获取模板配置文件中的对应数据
        String configStr = ResourceUtil.readUtf8Str("meta/meta-init.json");
        TtpMeta ttpMetaTemplate = JSONUtil.toBean(configStr, TtpMeta.class);

        // 2. 将q技术数据放入ttpMetaTemplate
        for (TtpMeta.TechniquesConfig aiServerTtpRes : aiServerTtpResList) {
            TtpMeta.TechniquesConfig singleTechniqueConfig = new TtpMeta.TechniquesConfig.Builder()
                    .techniqueID(aiServerTtpRes.getTechniqueID())
                    .tactic(aiServerTtpRes.getTactic())
                    .build();
            ttpMetaTemplate.getTechniques().add(singleTechniqueConfig);
        }

        // 定义一个工作空间，存放用户临时文件
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/jsonConfig/%s", projectPath, ctiId);

        // 定义Json文件的位置，进行存放
        String saveJsonFileName = "config.json";
        saveJsonFileName = RandomStringUtils.randomAlphanumeric(8) + "-" + saveJsonFileName;
        String saveJsonFilePath = tempDirPath + File.separator + saveJsonFileName;

        if (!FileUtil.exist(saveJsonFilePath)) {
            FileUtil.touch(saveJsonFilePath);
        }

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(ttpMetaTemplate), saveJsonFilePath);

        // 将json文件存放在cos中
        File techniqueConfigFile = new File(saveJsonFilePath);
        String uploadFilePath = String.format("/%s/%s/%s", FileUploadBizEnum.TECHNIQUE_CONFIG_FILE.getValue(), ctiId, saveJsonFileName);

        try {
            cosManager.putObject(uploadFilePath, techniqueConfigFile);
            // 返回可访问地址
            return uploadFilePath;
        } catch (Exception e) {
            log.error("file upload error, local filepath = " + saveJsonFilePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 删除临时文件
            boolean delete = FileUtil.del(tempDirPath);
            if (!delete) {
                log.error("file delete error, filepath = {}", saveJsonFilePath);
            }
        }
    }
}
