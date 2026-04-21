/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.openeo.process.dto;

import java.util.List;
import org.opengis.util.CodeList;

public class Status extends CodeList<Status> {
    public static final Status CREATED;
    public static final Status QUEUED;
    public static final Status RUNNING;
    public static final Status CANCELED;
    public static final Status FINISHED;
    public static final Status ERROR;

    /**
     * All code list values created in the currently running <abbr>JVM</abbr>.
     */
    private static final List<Status> VALUES = initialValues(
        // Inline assignments for getting compiler error if a field is missing or duplicated.
        CREATED  = new Status("created"),
        QUEUED   = new Status("queued"),
        RUNNING  = new Status("running"),
        CANCELED = new Status("canceled"),
        FINISHED = new Status("finished"),
        ERROR    = new Status("error"));

    private Status(String value) {
        super(value);
    }

    public static Status[] values() {
        return VALUES.toArray(Status[]::new);
    }

    public Status[] family() {
        return values();
    }

    public static Status valueOf(String code) {
        return valueOf(VALUES, code, Status::new);
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
