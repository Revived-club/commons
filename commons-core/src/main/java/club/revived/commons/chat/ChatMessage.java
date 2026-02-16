package club.revived.commons.chat;

import java.time.Instant;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import club.revived.commons.data.model.LogMetric;

@Measurement(name = "chat_message")
public record ChatMessage(
    @Column(tag = true) String uuid,
    @Column String content,
    @Column(timestamp = true) Instant sentAt,
    @Column boolean blocked) implements LogMetric {
}
