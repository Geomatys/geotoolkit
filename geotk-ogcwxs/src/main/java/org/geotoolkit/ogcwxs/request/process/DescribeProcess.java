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
package org.geotoolkit.ogcwxs.request.process;

import java.util.List;
import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve a full description of one or more processes offered by the server.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DescribeProcess extends RequestParameters {

    private String version;
    private List<String> identifiers;

    /**
     * The WPS version to request. Defaults to {@code "2.0.0"} if left {@code null}.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public DescribeProcess version(String version) {
        setVersion(version);
        return this;
    }

    /**
     * Identifiers of the processes to describe. (required)
     *
     * @return the identifiers
     */
    public List<String> getIdentifiers() {
        return identifiers;
    }

    /**
     * @param identifiers the identifiers to set
     * @see #getIdentifiers()
     */
    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    /**
     * @param identifiers the identifiers to set
     * @see #getIdentifiers()
     */
    public DescribeProcess identifiers(List<String> identifiers) {
        setIdentifiers(identifiers);
        return this;
    }

}
