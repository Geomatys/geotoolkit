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

import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.system.DefaultFactories;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
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
    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

    public static final FeatureType ABSTRACTGMLTYPE_31;
    public static final FeatureType ABSTRACTGMLTYPE_32;
    public static final FeatureType ABSTRACTFEATURETYPE_31;
    public static final FeatureType ABSTRACTFEATURETYPE_32;

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
    public static final GenericName NILLABLE_PROPERTY;
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
        NILLABLE_PROPERTY       = NamesExt.create(XSI_NAMESPACE, "@nil");
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
                .setDefaultValue(Boolean.TRUE)
                .build();
        SUBTYPE_CHARACTERISTIC = new FeatureTypeBuilder()
                .addAttribute(GenericName.class)
                .setName(SUBTYPE_PROPERTY)
                .setMinimumOccurs(0)
                .setMaximumOccurs(1)
                .build();



        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"AbstractGMLType");
        ftb.addAttribute(String.class).setName(GML_311_NAMESPACE,"@id").setMinimumOccurs(0).setMaximumOccurs(1).addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ABSTRACTGMLTYPE_31 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_321_NAMESPACE,"AbstractGMLType");
        AttributeTypeBuilder<String> atb = ftb.addAttribute(String.class).setName(GML_321_NAMESPACE,"@id").setMinimumOccurs(1).setMaximumOccurs(1);
        atb.addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ABSTRACTGMLTYPE_32 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"AbstractFeatureType");
        ftb.setSuperTypes(ABSTRACTGMLTYPE_31);
        ABSTRACTFEATURETYPE_31 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_321_NAMESPACE,"AbstractFeatureType");
        ftb.setSuperTypes(ABSTRACTGMLTYPE_32);
        ABSTRACTFEATURETYPE_32 = ftb.build();
    }
}
