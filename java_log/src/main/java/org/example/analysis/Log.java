package org.example.analysis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("analysis_log")
@Data
public class Log {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer ms;
    private String content;
    private String params;
    private String category;
    private String interfaceName;

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", ms=" + ms +
                ", content='" + content + '\'' +
                ", params='" + params + '\'' +
                ", category='" + category + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                '}';
    }
}
