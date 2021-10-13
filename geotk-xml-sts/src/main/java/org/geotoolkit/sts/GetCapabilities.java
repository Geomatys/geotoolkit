/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sts;

import org.apache.sis.util.Version;
import org.geotoolkit.ows.xml.RequestBase;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class GetCapabilities implements RequestBase {

    @Override
    public String getService() {
        return "STS";
    }

    @Override
    public void setService(String value) {
        // hard coded
    }

    @Override
    public Version getVersion() {
        return new Version("1.0.0");
    }

    @Override
    public void setVersion(String version) {
        // hard coded
    }
}
