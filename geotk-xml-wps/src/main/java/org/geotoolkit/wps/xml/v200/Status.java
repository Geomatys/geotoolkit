package org.geotoolkit.wps.xml.v200;

import java.util.List;
import org.opengis.util.CodeList;

/**
 * Basic status set to communicate the status of a server-side job to the
 * client. Extensions of this specification may introduce additional states for
 * fine-grained monitoring or domain-specific purposes.
 *
 * See http://schemas.opengis.net/wps/2.0/wpsCommon.xsd for official definition.
 *
 * @author Alexis Manin (Geomatys)
 */
public class Status extends CodeList<Status> {

    public static final Status ACCEPTED;
    public static final Status RUNNING;
    public static final Status FAILED;
    public static final Status SUCCEEDED;
    public static final Status DISMISS;
    // Added this new values  TODO review if really needed
    @Deprecated
    public static final Status STARTED;
    @Deprecated
    public static final Status PAUSED;

    /**
     * All code list values created in the currently running <abbr>JVM</abbr>.
     */
    private static final List<Status> VALUES = initialValues(
        // Inline assignments for getting compiler error if a field is missing or duplicated.
        ACCEPTED  = new Status("Accepted"),
        RUNNING   = new Status("Running"),
        FAILED    = new Status("Failed"),
        SUCCEEDED = new Status("Succeeded"),
        DISMISS   = new Status("Dismissed"),
        STARTED   = new Status("Started"),
        PAUSED    = new Status("Paused"));


    private Status(String value) {
        super(value);
    }

    @Override
    public Status[] family() {
        return VALUES.toArray(Status[]::new);
    }

    public static Status valueOf(String code) {
        return valueOf(VALUES, code, Status::new);
    }
}
