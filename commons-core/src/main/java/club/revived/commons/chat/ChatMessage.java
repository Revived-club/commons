package club.revived.commons.chat;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import club.revived.commons.data.model.LogMetric;

@Measurement(name = "chat_message")
public record ChatMessage(
    @Column(name = "uuid", tag = true) String uuidString,
    @Column(name = "content") String content,
    @Column(name = "sent_at", timestamp = true) long sentAt,
    @Column(name = "blocked") boolean blocked) implements LogMetric {
}
