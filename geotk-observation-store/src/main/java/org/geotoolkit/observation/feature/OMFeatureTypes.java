/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.observation.feature;

import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import static org.geotoolkit.storage.feature.AbstractFeatureStore.GML_311_NAMESPACE;
import org.geotoolkit.util.NamesExt;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OMFeatureTypes {

    public static final String GEOTK_NAMESPACE = "http://geotoolkit.org/";
    public static final GenericName SENSOR_TN = NamesExt.create(GEOTK_NAMESPACE, "Sensor");
    public static final GenericName SENSOR_ATT_ID = NamesExt.create(GEOTK_NAMESPACE, "id");
    public static final GenericName SENSOR_ATT_POSITION = NamesExt.create(GEOTK_NAMESPACE, "position");

    public static final String OM_NAMESPACE = "http://www.opengis.net/sampling/1.0";
    public static final GenericName SAMPLINGPOINT_TN = NamesExt.create(OM_NAMESPACE, "SamplingPoint");
    public static final GenericName SF_ATT_ID       = NamesExt.create(GML_311_NAMESPACE, "@id");
    public static final GenericName SF_ATT_DESC     = NamesExt.create(GML_311_NAMESPACE, "description");
    public static final GenericName SF_ATT_NAME     = NamesExt.create(GML_311_NAMESPACE, "name");
    public static final GenericName SF_ATT_SAMPLED  = NamesExt.create(OM_NAMESPACE, "sampledFeature");
    public static final GenericName SF_ATT_POSITION = NamesExt.create(OM_NAMESPACE, "position");

    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

    public static FeatureType buildSensorFeatureType() {
        return buildSensorFeatureType(SENSOR_TN, null);
    }

    public static FeatureType buildSensorFeatureType(String name) {
        return buildSensorFeatureType(NamesExt.create(name), null);
    }

    public static FeatureType buildSensorFeatureType(GenericName name) {
        return buildSamplingFeatureFeatureType(name, null);
    }

    public static FeatureType buildSensorFeatureType(GenericName name, CoordinateReferenceSystem crs) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.addAttribute(String.class).setName(SENSOR_ATT_ID).addRole(AttributeRole.IDENTIFIER_COMPONENT);
        if (crs != null) {
            ftb.addAttribute(Geometry.class).setName(SENSOR_ATT_POSITION).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        } else {
            ftb.addAttribute(Geometry.class).setName(SENSOR_ATT_POSITION).addRole(AttributeRole.DEFAULT_GEOMETRY);
        }
        return ftb.build();
    }

    public static FeatureType buildSamplingFeatureFeatureType() {
        return buildSamplingFeatureFeatureType(SAMPLINGPOINT_TN, null);
    }

    public static FeatureType buildSamplingFeatureFeatureType(String name) {
        return buildSamplingFeatureFeatureType(NamesExt.create(name), null);
    }

    public static FeatureType buildSamplingFeatureFeatureType(final GenericName name) {
        return buildSamplingFeatureFeatureType(name, null);
    }

    public static FeatureType buildSamplingFeatureFeatureType(final GenericName name, CoordinateReferenceSystem crs) {

        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"AbstractGMLType");
        ftb.addAttribute(String.class).setName(GML_311_NAMESPACE,"@id").setMinimumOccurs(0).setMaximumOccurs(1).addRole(AttributeRole.IDENTIFIER_COMPONENT);
        FeatureType abstractGMLType = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"AbstractFeatureType");
        ftb.setSuperTypes(abstractGMLType);
        FeatureType abstractFeatureType = ftb.build();


        ftb = new FeatureTypeBuilder();
        AttributeType<Boolean> nillable_Characteristic = ftb
                .addAttribute(Boolean.class)
                .setName(NamesExt.create(XSI_NAMESPACE, "@nil"))
                .setMinimumOccurs(0)
                .setMaximumOccurs(1)
                .setDefaultValue(Boolean.TRUE)
                .build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.setSuperTypes(abstractFeatureType);
        ftb.addAttribute(String.class).setName(SF_ATT_DESC).setMinimumOccurs(0).setMaximumOccurs(1);
        ftb.addAttribute(String.class).setName(SF_ATT_NAME).setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE);
        ftb.addAttribute(String.class).setName(SF_ATT_SAMPLED)
                .setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(nillable_Characteristic);
        if (crs != null) {
            ftb.addAttribute(Geometry.class).setName(SF_ATT_POSITION).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        } else {
            ftb.addAttribute(Geometry.class).setName(SF_ATT_POSITION).addRole(AttributeRole.DEFAULT_GEOMETRY);
        }
        return  ftb.build();
    }
}
