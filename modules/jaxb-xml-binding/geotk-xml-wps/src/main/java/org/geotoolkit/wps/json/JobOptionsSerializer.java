package org.geotoolkit.wps.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.geotoolkit.wps.xml.v200.JobControlOptions;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class JobOptionsSerializer extends StdSerializer<JobControlOptions> {

    JobOptionsSerializer() {
        super(JobControlOptions.class);
    }

    @Override
    public void serialize(JobControlOptions t, JsonGenerator jg, SerializerProvider sp) throws IOException {
        jg.writeString(t.name());
    }
}
