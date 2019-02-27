/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.metadata;

import org.apache.sis.internal.xml.LegacyNamespaces;
import org.apache.sis.xml.Namespaces;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public enum MetadataType {

    DUBLINCORE_CSW202(false, true, LegacyNamespaces.CSW),
    DUBLINCORE_CSW300(false, true, Namespaces.CSW),
    ISO_19115(true, false, LegacyNamespaces.GMD),
    EBRIM_250(false, false, "urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5"),
    EBRIM_300(false, false, "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"),
    SENSORML_100(false, false, "http://www.opengis.net/sensorML/1.0"),
    SENSORML_101(false, false, "http://www.opengis.net/sensorML/1.0.1"),
    SENSORML_200(false, false, "http://www.opengis.net/sensorml/2.0"),
    NATIVE(false, false, null),
    ISO_19110(false, false, LegacyNamespaces.GFC),
    CONTACT(false, false, LegacyNamespaces.GMD);

    public final boolean isDCtransformable;
    public final boolean isElementSetable;
    public final String namespace;

    private MetadataType(final boolean isDCtransformable, final boolean isElementSetable, final String namespace) {
        this.isDCtransformable = isDCtransformable;
        this.isElementSetable  = isElementSetable;
        this.namespace         = namespace;
    }


}
