package org.geotoolkit.wps.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.geotoolkit.wps.xml.v200.JobControlOptions;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class JobOptionsDeserializer extends StdDeserializer<JobControlOptions> {

    JobOptionsDeserializer() {
        super(JobControlOptions.class);
    }

    @Override
    public JobControlOptions deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        final String opt = jp.getText();
        if (opt == null || opt.isEmpty()) {
            return null;
        }

        return JobControlOptions.valueOf(opt);
    }
}
