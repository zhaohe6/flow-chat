package com.robod.flowchat.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Builder
@Data
@NoArgsConstructor
public class ResponseBody {
    private int code; // 响应状态码
    private String message; // 响应消息
    private Object data; // 响应数据

    public ResponseBody(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null; // 默认data为null
    }

    public ResponseBody(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
