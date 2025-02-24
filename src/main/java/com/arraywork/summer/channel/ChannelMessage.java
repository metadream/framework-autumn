package com.arraywork.summer.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.util.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

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

    public String event() {
        return StringUtils.isBlank(event) ? DEFAULT_EVENT : event;
    }

    public Object data() {
        return data;
    }

    /**
     * Inner Class for Encoding Channel Message
     */
    @Slf4j
    public static class Encoder implements jakarta.websocket.Encoder.Text<Object> {
        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String encode(Object message) {
            try {
                return mapper.writeValueAsString(message);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }

    }

}