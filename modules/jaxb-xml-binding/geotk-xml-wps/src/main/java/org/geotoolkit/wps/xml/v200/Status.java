package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import org.apache.sis.util.iso.Types;
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

    private static final ArrayList<Status> VALUES = new ArrayList<>(4);

    public static final Status ACCEPTED = new Status("Accepted");
    public static final Status RUNNING = new Status("Running");
    public static final Status FAILED = new Status("Failed");
    public static final Status SUCCEEDED = new Status("Succeeded");

    private Status(String value) {
        super(value, VALUES);
    }

    public static Status[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new Status[VALUES.size()]);
        }
    }

    @Override
    public Status[] family() {
        return values();
    }

    public static Status valueOf(String code) {
        return Types.forCodeName(Status.class, code, true);
    }
}
