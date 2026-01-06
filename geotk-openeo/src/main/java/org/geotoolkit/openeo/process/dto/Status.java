package org.geotoolkit.openeo.process.dto;

import org.opengis.util.CodeList;

public class Status extends CodeList<Status> {
    public static final Status CREATED = valueOf("created");
    public static final Status QUEUED = valueOf("queued");
    public static final Status RUNNING = valueOf("running");
    public static final Status CANCELED = valueOf("canceled");
    public static final Status FINISHED = valueOf("finished");
    public static final Status ERROR = valueOf("error");

    private Status(String value) {
        super(value);
    }

    public Status[] family() {
        return (Status[])values(Status.class);
    }

    public static Status valueOf(String code) {
        return (Status)valueOf(Status.class, code, Status::new).get();
    }

    public static Status wpsStatusEquivalentTo(org.geotoolkit.wps.xml.v200.Status wpsStatus) {
        if (wpsStatus.equals(org.geotoolkit.wps.xml.v200.Status.DISMISS)) {
            return Status.CANCELED;
        } else if (wpsStatus.equals(org.geotoolkit.wps.xml.v200.Status.RUNNING)) {
            return Status.RUNNING;
        } else if (wpsStatus.equals(org.geotoolkit.wps.xml.v200.Status.ACCEPTED)) {
            return Status.CREATED;
        } else if (wpsStatus.equals(org.geotoolkit.wps.xml.v200.Status.FAILED)) {
            return Status.ERROR;
        } else if (wpsStatus.equals(org.geotoolkit.wps.xml.v200.Status.SUCCEEDED)) {
            return Status.FINISHED;
        } else {
            return null; //No equivalent to "QUEUED" in wps
        }
    }
}
