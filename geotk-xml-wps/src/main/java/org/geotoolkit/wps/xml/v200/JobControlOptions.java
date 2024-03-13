package org.geotoolkit.wps.xml.v200;

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

    public static final JobControlOptions SYNC_EXECUTE = valueOf("sync-execute");
    public static final JobControlOptions ASYNC_EXECUTE = valueOf("async-execute");
    public static final JobControlOptions DISMISS = valueOf("dismiss");

    private JobControlOptions(String value) {
        super(value);
    }

    public static JobControlOptions[] values() {
        return CodeList.values(JobControlOptions.class);
    }

    @Override
    public JobControlOptions[] family() {
        return values(JobControlOptions.class);
    }

    public static JobControlOptions valueOf(String code) {
        return valueOf(JobControlOptions.class, code, JobControlOptions::new).get();
    }
}
