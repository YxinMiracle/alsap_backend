package com.yxinmiracle;

/*
 * @author  YxinMiracle
 * @date  2024-09-23 18:47
 * @Gitee: https://gitee.com/yxinmiracle
 */

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.yxinmiracle.alsap.model.meta.TtpMeta;
import org.junit.jupiter.api.Test;

public class Main {

    @Test
    public void testMakeTemplateBug1() {
        // 1. 读取配置文件
        String configStr = ResourceUtil.readUtf8Str("meta/meta-init.json");
        TtpMeta ttpMeta = JSONUtil.toBean(configStr, TtpMeta.class);

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(ttpMeta), "E:\\code\\alsap\\alsap_backend\\src\\main\\resources\\test.json");

        System.out.println(ttpMeta);
    }

}
