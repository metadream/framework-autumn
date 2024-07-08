package com.arraywork.springforce.channel;

import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web Socket Message Entity
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/07/06
 */
@Data
@NoArgsConstructor
public class Message {

    private static final String DEFAULT_EVENT = "message";

    private String event;
    private Object data;

    public Message(Object data) {
        this.data = data;
    }

    public String getEvent() {
        return StringUtils.isBlank(event) ? DEFAULT_EVENT : event;
    }

}