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
package org.geotoolkit.ogcwxs.request.observation;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * @author Quentin Bialota (Geomatys)
 */
public class GetCapabilities extends RequestParameters {

    private SOSVersion version;

    public SOSVersion getVersion() {
        return version;
    }

    public void setVersion(SOSVersion version) {
        this.version = version;
    }

    public GetCapabilities version(SOSVersion version) {
        setVersion(version);
        return this;
    }
}
