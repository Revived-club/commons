package club.revived.commons.logging.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

@Measurement(name = "commands")
public record CommandLog(
    @Column(tag = true, name = "uuid") String uuidString,
    @Column(name = "name") String name,
    @Column(name = "payload") String payload,
    @Column(timestamp = true, name = "timestamp") long timestamp) {
}
