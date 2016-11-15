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
package org.geotoolkit.feature.xml;

import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.system.DefaultFactories;
import org.opengis.feature.AttributeType;
import org.opengis.util.GenericName;
import org.opengis.util.LocalName;
import org.opengis.util.NameFactory;
import org.opengis.util.NameSpace;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GMLConvention {
    
    public static final String GML_311_NAMESPACE = "http://www.opengis.net/gml";
    public static final String GML_321_NAMESPACE = "http://www.opengis.net/gml/3.2";

    /**
     * Namespace of all names defined by GML convention.
     */
    private static final GenericName NAMESPACE;

    /**
     * XSD type id of the property.
     */
    public static final LocalName XSD_TYPE_ID_PROPERTY;
    public static final AttributeType<String> XSD_TYPE_ID_CHARACTERISTIC;

    /**
     * XSD type is nillable.
     */
    public static final LocalName NILLABLE_PROPERTY;
    public static final AttributeType<Boolean> NILLABLE_CHARACTERISTIC;

    /**
     * GML encapsulate sub types in a FeatureType.
     * If the characteristic is not defined then the normal OGC convention is used
     * If a name is defined it should replace the propertype name
     * If name equals NO_SUBTYPE then no property type should be generated
     */
    public static final LocalName SUBTYPE_PROPERTY;
    public static final LocalName NO_SUBTYPE;
    public static final AttributeType<GenericName> SUBTYPE_CHARACTERISTIC;

    static {
        final NameFactory factory = DefaultFactories.forBuildin(NameFactory.class);
        NAMESPACE               = factory.createGenericName(null, "Geotk", "GML");
        NameSpace ns            = factory.createNameSpace(NAMESPACE, null);
        XSD_TYPE_ID_PROPERTY    = factory.createLocalName(ns, "xsdTypeId");
        NILLABLE_PROPERTY       = factory.createLocalName(ns, "nillable");
        SUBTYPE_PROPERTY        = factory.createLocalName(ns, "gmlPropertyType");
        NO_SUBTYPE              = factory.createLocalName(ns, "noPropertyType");

        XSD_TYPE_ID_CHARACTERISTIC = new FeatureTypeBuilder()
                .addAttribute(String.class)
                .setName(XSD_TYPE_ID_PROPERTY)
                .setMinimumOccurs(0)
                .setMaximumOccurs(1)
                .build();
        NILLABLE_CHARACTERISTIC = new FeatureTypeBuilder()
                .addAttribute(Boolean.class)
                .setName(NILLABLE_PROPERTY)
                .setMinimumOccurs(0)
                .setMaximumOccurs(1)
                .build();
        SUBTYPE_CHARACTERISTIC = new FeatureTypeBuilder()
                .addAttribute(GenericName.class)
                .setName(SUBTYPE_PROPERTY)
                .setMinimumOccurs(0)
                .setMaximumOccurs(1)
                .build();
    }
}
