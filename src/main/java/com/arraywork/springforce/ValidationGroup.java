package com.arraywork.springforce;

/**
 * 实体字段校验分组（配合spring的@Validated分组校验功能）
 * @author AiChen
 * @created 2024/03/07
 */
public class ValidationGroup {

    public interface Insert {}

    public interface Update {}

    public interface Delete {}

}