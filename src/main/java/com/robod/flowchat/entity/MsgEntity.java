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
}
