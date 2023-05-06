package com.lidachui.websocket.dal.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * BaseEntity
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/18 23:42
 */
@Data
@Accessors(chain = true)
public class BaseEntity<T> {

    /**
     * 主键id
     */
    private Long id;
}
