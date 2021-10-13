package org.geotoolkit.wps.xml.v200;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import org.apache.sis.util.iso.Types;
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

    private static final ArrayList<JobControlOptions> VALUES = new ArrayList<>(4);

    public static final JobControlOptions SYNC_EXECUTE = new JobControlOptions("sync-execute");
    public static final JobControlOptions ASYNC_EXECUTE = new JobControlOptions("async-execute");
    public static final JobControlOptions DISMISS = new JobControlOptions("dismiss");

    private JobControlOptions(String value) {
        super(value, VALUES);
    }

    public static JobControlOptions[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new JobControlOptions[VALUES.size()]);
        }
    }

    @Override
    public JobControlOptions[] family() {
        return values();
    }

    public static JobControlOptions valueOf(String code) {
        return Types.forCodeName(JobControlOptions.class, code, true);
    }
}
