package com.yxinmiracle.alsap.model.vo.item;

/*
 * @author  YxinMiracle
 * @date  2024-05-12 11:39
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.entity.Item;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 除了首页展示之外，item需要在其他地方所展示的vo
 */
@Data
public class ItemVo implements Serializable {
    private Long id;
    /**
     * 实体的名称
     */
    private String itemName;

    /**
     * 实体的解释
     */
    private String itemContent;

    /**
     * 实体类型，2是sdo,1是sco
     */
    private Integer itemType;

    /**
     * 对itemType的一个解释
     */
    private String itemTypeContent;

    /**
     * 实体标注时前端展示的背景颜色
     */
    private String backgroundColor;

    /**
     * 实体标注时前端展示的字体颜色
     */
    private String textColor;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param itemVo
     * @return
     */
    public static Item voToObj(ItemVo itemVo) {
        if (itemVo == null) {
            return null;
        }
        Item item = new Item();
        BeanUtils.copyProperties(itemVo, item);
        return item;
    }

    /**
     * 对象转包装类
     *
     * @param item
     * @return
     */
    public static ItemVo objToVo(Item item) {
        if (item == null) {
            return null;
        }
        ItemVo itemVo = new ItemVo();
        BeanUtils.copyProperties(item, itemVo);
        return itemVo;
    }


}
