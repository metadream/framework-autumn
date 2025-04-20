package com.arraywork.autumn.channel;

import com.arraywork.autumn.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.micrometer.common.util.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Channel Message Entity for WebSocket
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/07/06
 */
@NoArgsConstructor
@Setter // Since the Getter is not defined, json serialization field must be explicitly declared by @JsonProperty
@EqualsAndHashCode
@ToString
public class ChannelMessage {

    private static final String DEFAULT_EVENT = "message";
    @JsonProperty
    private String event;
    @JsonProperty
    private Object data;

    public ChannelMessage(Object data) {
        this.data = data;
    }

    @JsonGetter
    public String event() {
        return StringUtils.isBlank(event) ? DEFAULT_EVENT : event;
    }

    public Object data() {
        return data;
    }

    /** Inner Class for Encoding Channel Message */
    public static class Encoder implements jakarta.websocket.Encoder.Text<Object> {
        @Override
        public String encode(Object message) {
            return JsonUtils.stringify(message);
        }
    }

}