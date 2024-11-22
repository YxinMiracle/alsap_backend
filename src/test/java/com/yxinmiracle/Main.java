package com.yxinmiracle;

/*
 * @author  YxinMiracle
 * @date  2024-09-23 18:47
 * @Gitee: https://gitee.com/yxinmiracle
 */

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import com.yxinmiracle.alsap.manager.TtpManager;
import com.yxinmiracle.alsap.model.meta.TtpMeta;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    @Test
    public void testMakeTemplateBug1() {
        // 1. 读取配置文件
        String configStr = ResourceUtil.readUtf8Str("meta/meta-init.json");
        TtpMeta ttpMeta = JSONUtil.toBean(configStr, TtpMeta.class);

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(ttpMeta), "E:\\code\\alsap\\alsap_backend\\src\\main\\resources\\test.json");

        System.out.println(ttpMeta);
    }

    @Test
    public void testTechnique(){
        try {
            String techniquesConfigListStr = "xxxxx";
            List<TtpMeta.TechniquesConfig> techniquesConfigList = JSONUtil.toList(techniquesConfigListStr, TtpMeta.TechniquesConfig.class);
            System.out.println(techniquesConfigList);
        }catch (JSONException jsonException){
            log.error("json解析报错",jsonException);
        }
    }


    @Test
    public void testTechnique2(){
        TtpManager ttpManager = new TtpManager();
        TtpMeta.TechniquesConfig build1 = new TtpMeta.TechniquesConfig.Builder().techniqueID("Txxx1").tactic("cc1").build();
        TtpMeta.TechniquesConfig build2 = new TtpMeta.TechniquesConfig.Builder().techniqueID("Txxx2").tactic("cc2").build();
        TtpMeta.TechniquesConfig build3 = new TtpMeta.TechniquesConfig.Builder().techniqueID("Txxx3").tactic("cc3").build();
        List<TtpMeta.TechniquesConfig> techniquesConfigList = Arrays.asList(build1, build2, build3);
        String s = ttpManager.buildTtpConfigData(techniquesConfigList, 11111111L);
        System.out.println(s);
    }

}
