package org.example.demo.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * Created by Tom.
 */
@Entity
@Table(name="t_order")
@Data
public class Order implements Serializable {
    private Long id;
    @Column(name="mid")
    private Long memberId;
    private String detail;
    private Long createTime;
    private String createTimeFmt;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", detail='" + detail + '\'' +
                ", createTime=" + createTime +
                ", createTimeFmt='" + createTimeFmt + '\'' +
                '}';
    }
}
