package org.geotoolkit.wps.xml.v200;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.geotoolkit.wps.json.JobOptionsDeserializer;
import org.geotoolkit.wps.json.JobOptionsSerializer;
import org.opengis.util.CodeList;

/**
 * Set of options allowed to configure a process execution. Allowed values can
 * be found in
 * <a href="http://docs.opengeospatial.org/is/14-065/14-065.html#47">WPS 2
 * official documentation, section 9.4</a> (see table 30).
 *
 * @author Alexis Manin (Geomatys)
 */
@JsonDeserialize(using = JobOptionsDeserializer.class)
@JsonSerialize(using = JobOptionsSerializer.class)
public class JobControlOptions extends CodeList<JobControlOptions> {

    public static final JobControlOptions SYNC_EXECUTE;
    public static final JobControlOptions ASYNC_EXECUTE;
    public static final JobControlOptions DISMISS;

    /**
     * All code list values created in the currently running <abbr>JVM</abbr>.
     */
    private static final List<JobControlOptions> VALUES = initialValues(
        // Inline assignments for getting compiler error if a field is missing or duplicated.
        SYNC_EXECUTE  = new JobControlOptions("sync-execute"),
        ASYNC_EXECUTE = new JobControlOptions("async-execute"),
        DISMISS       = new JobControlOptions("dismiss"));

    private JobControlOptions(String value) {
        super(value);
    }

    public static JobControlOptions[] values() {
        return VALUES.toArray(JobControlOptions[]::new);
    }

    @Override
    public JobControlOptions[] family() {
        return values();
    }

    public static JobControlOptions valueOf(String code) {
        return valueOf(VALUES, code, JobControlOptions::new);
    }
}
