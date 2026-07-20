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
package org.geotoolkit.ogcapi.dto.processes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Execution control options a process supports, as listed in
 * {@link ProcessSummary#getJobControlOptions()}.
 */
@XmlType(name = "JobControlOptionsEnum")
@XmlEnum(value = String.class)
public enum JobControlOption {
    @XmlEnumValue("sync-execute")
    SYNC_EXECUTE("sync-execute"),
    @XmlEnumValue("async-execute")
    ASYNC_EXECUTE("async-execute"),
    @XmlEnumValue("dismiss")
    DISMISS("dismiss");

    private final String value;

    JobControlOption(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static JobControlOption fromValue(String value) {
        for (JobControlOption b : JobControlOption.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
