package org.geotoolkit.wps.xml;

import org.apache.sis.util.Static;
import org.geotoolkit.wps.xml.v200.Execute;
import org.geotoolkit.wps.xml.v200.JobControlOptions;
import org.geotoolkit.wps.xml.v200.ProcessOffering;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class WPSUtilities extends Static {

    public static final String CDATA_START_TAG = "<![CDATA[";
    public static final String CDATA_END_TAG = "]]>";

    /**
     * Verify that given execution mode is accepted by input process description.
     * @param process The description of the process, gives available execution
     * options via {@link ProcessOffering#getJobControlOptions() }.
     * @param mode The execution mode to test against given process.
     * @return True if given mode can be used for process execution, false if the
     * given process does not accept input mode.
     */
    public static boolean testCompatibility(final ProcessOffering process, Execute.Mode mode) {
        switch (mode) {
            case auto:
                return true;
            case sync:
                return process.getJobControlOptions().contains(JobControlOptions.SYNC_EXECUTE);
            case async:
                return process.getJobControlOptions().contains(JobControlOptions.ASYNC_EXECUTE);
            default:
                return false;
        }
    }
}
