package org.geotoolkit.wps.xml.v200;

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

    public static final Status ACCEPTED = valueOf("Accepted");
    public static final Status RUNNING = valueOf("Running");
    public static final Status FAILED = valueOf("Failed");
    public static final Status SUCCEEDED = valueOf("Succeeded");
    public static final Status DISMISS = valueOf("Dismissed");
    // Added this new values  TODO review if really needed
    @Deprecated
    public static final Status STARTED = valueOf("Started");
    @Deprecated
    public static final Status PAUSED = valueOf("Paused");


    private Status(String value) {
        super(value);
    }

    @Override
    public Status[] family() {
        return values(Status.class);
    }

    public static Status valueOf(String code) {
        return valueOf(Status.class, code, Status::new).get();
    }
}
