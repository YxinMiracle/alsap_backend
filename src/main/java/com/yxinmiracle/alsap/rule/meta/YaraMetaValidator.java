package com.yxinmiracle.alsap.rule.meta;

/*
 * @author  YxinMiracle
 * @date  2024-11-01 10:35
 * @Gitee: https://gitee.com/yxinmiracle
 */

import cn.hutool.core.util.StrUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class YaraMetaValidator {

    public static void doValidAndFill(YaraMate yaraMate) {
        validAndFillMetaRoot(yaraMate);
    }


    public static void validAndFillMetaRoot(YaraMate yaraMate) {
        // 校验并填充默认值
        String author = StrUtil.blankToDefault(yaraMate.getAuthor(), "yxinmiracle");
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        String createDate = StrUtil.blankToDefault(yaraMate.getCreateDate(), formattedDate);
        yaraMate.setAuthor(author);
        yaraMate.setCreateDate(createDate);
    }


}
