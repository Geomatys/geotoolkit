/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.feature.xml;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.xml.bind.annotation.XmlAttribute;
import org.geotoolkit.wfs.xml.WFSResponse;

/**
 *
 * @author Rohan FERRE (Géomatys)
 */
public abstract class FeatureResponse implements WFSResponse {

    @XmlAttribute
    private final String service = "OGCAPI-Features";
    @XmlAttribute
    private final String version = "1.0.0";

    @Override
    @JsonIgnore
    public String getVersion() {
        return "feat-1.0.0";
    }
}
