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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.sis.xml.util.LegacyNamespaces;
import org.apache.sis.xml.Namespaces;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public enum MetadataType {

    DUBLINCORE_CSW202(false, true, LegacyNamespaces.CSW, TypeNames.DC_TYPE_NAMES),
    DUBLINCORE_CSW300(false, true, Namespaces.CSW, TypeNames.DC_TYPE_NAMES),
    ISO_19115(true, false, LegacyNamespaces.GMD, TypeNames.ISO_TYPE_NAMES),
    EBRIM_250(false, false, "urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", TypeNames.EBRIM25_TYPE_NAMES),
    EBRIM_300(false, false, "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", TypeNames.EBRIM30_TYPE_NAMES),
    SENSORML_100(false, false, "http://www.opengis.net/sensorML/1.0", TypeNames.SML100_TYPE_NAMES),
    SENSORML_101(false, false, "http://www.opengis.net/sensorML/1.0.1", TypeNames.SML101_TYPE_NAMES),
    SENSORML_200(false, false, "http://www.opengis.net/sensorml/2.0", TypeNames.SML200_TYPE_NAMES),
    ISO_19110(false, false, LegacyNamespaces.GFC, TypeNames.FC_TYPE_NAMES),
    DIF(true, false, "http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/", TypeNames.DIF_TYPE_NAMES),

    CONTACT(false, false, LegacyNamespaces.GMD, Arrays.asList(TypeNames.CONTACT_QNAME)),
    NATIVE(false, false, null, new ArrayList<>());

    public final boolean isDCtransformable;
    public final boolean isElementSetable;
    public final String namespace;
    public final List<QName> typeNames;

    private MetadataType(final boolean isDCtransformable, final boolean isElementSetable, final String namespace, final List<QName> typeNames) {
        this.isDCtransformable = isDCtransformable;
        this.isElementSetable  = isElementSetable;
        this.namespace         = namespace;
        this.typeNames         = typeNames;
    }


    public static MetadataType getFromNamespace(String namespace) {
        for (MetadataType value : values()) {
            if (namespace.equals(value.namespace)) {
                return value;
            }
        }
        throw new IllegalArgumentException("undefined outputSchema");
    }

    public static MetadataType getFromTypeName(QName rootName) {
        for (MetadataType value : values()) {
            if (value.typeNames.contains(rootName)) {
                return value;
            }
        }
        return null;
    }
}
