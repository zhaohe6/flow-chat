package com.robod.flowchat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import javax.print.attribute.standard.MediaSize;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_message")
public class MsgEntity {
    public enum MsgType {
        CHAT_MESSAGE,  //聊天消息
        HEART_BEAT //心跳包
    }
    @TableId
    private String id = UUID.randomUUID().toString();

    @TableField("sender")
    private String sender = "匿名用户";

    @TableField("receiver")
    private String receiver = "匿名用户";

    @TableField("content")
    private String content ="空消息";

    @TableField("timestamp")
    private String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

    @TableField("type")
    private MsgType type = MsgType.CHAT_MESSAGE;


    public MsgEntity(String sender, String username, String s) {
        this.sender = sender;
        this.receiver = username;
        this.content = s;
    }
    public MsgEntity(String sender, String receiver, String content,MsgType type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
    }
}
