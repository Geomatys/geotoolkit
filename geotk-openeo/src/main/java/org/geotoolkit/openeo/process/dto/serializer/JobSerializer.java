package org.geotoolkit.openeo.process.dto.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.geotoolkit.openeo.process.dto.Job;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class JobSerializer extends JsonSerializer<Job> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void serialize(Job job, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("id", job.getId());

        if (job.getStatus() != null) {
            jsonGenerator.writeStringField("status", job.getStatus().toString().split("\\.",2)[1]);
        }

        if (job.getCreated() != null) {
            jsonGenerator.writeStringField("created", DATE_TIME_FORMATTER.format(job.getCreated().toGregorianCalendar().toZonedDateTime().toLocalDateTime()));
        }

        if (job.getTitle() != null) {
            jsonGenerator.writeStringField("title", job.getTitle());
        }

        if (job.getDescription() != null) {
            jsonGenerator.writeStringField("description", job.getDescription());
        }

        if (job.getProcess() != null) {
            jsonGenerator.writeObjectField("process", job.getProcess());
        }

        jsonGenerator.writeNumberField("progress", job.getProgress());

        if (job.getPlan() != null) {
            jsonGenerator.writeStringField("plan", job.getPlan());
        }

        jsonGenerator.writeNumberField("costs", job.getCosts());
        jsonGenerator.writeNumberField("budget", job.getBudget());

        if (job.getLogLevel() != null) {
            jsonGenerator.writeStringField("log_level", job.getLogLevel());
        }

        if (job.getLinks() != null) {
            jsonGenerator.writeObjectField("links", job.getLinks());
        }

        jsonGenerator.writeEndObject();
    }
}
