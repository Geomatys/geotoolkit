/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.data.om;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.IllegalNameException;
import static org.geotoolkit.data.AbstractFeatureStore.GML_311_NAMESPACE;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.internal.GenericNameIndex;
import org.geotoolkit.feature.xml.GMLConvention;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OMFeatureTypes {

    public static final String OM_NAMESPACE = "http://www.opengis.net/sampling/1.0";
    //protected static final Name OM_TN_SAMPLINGPOINT = new DefaultName(OM_NAMESPACE, "SamplingPoint");

    public static final GenericName ATT_ID       = NamesExt.create(GML_311_NAMESPACE, "@id");
    public static final GenericName ATT_DESC     = NamesExt.create(GML_311_NAMESPACE, "description");
    public static final GenericName ATT_NAME     = NamesExt.create(GML_311_NAMESPACE, "name");
    public static final GenericName ATT_SAMPLED  = NamesExt.create(OM_NAMESPACE, "sampledFeature");
    public static final GenericName ATT_POSITION = NamesExt.create(OM_NAMESPACE, "position");

    public static GenericNameIndex<FeatureType> getFeatureTypes(final String name) {
        final GenericNameIndex<FeatureType> types = new GenericNameIndex<>();

        final GenericName OM_TN_SAMPLINGPOINT = NamesExt.create(OM_NAMESPACE, name);
        final FeatureTypeBuilder featureTypeBuilder = new FeatureTypeBuilder();
        featureTypeBuilder.setName(OM_TN_SAMPLINGPOINT);
        featureTypeBuilder.setSuperTypes(GMLConvention.ABSTRACTFEATURETYPE_31);
        featureTypeBuilder.addAttribute(String.class).setName(ATT_DESC).setMinimumOccurs(0).setMaximumOccurs(1);
        featureTypeBuilder.addAttribute(String.class).setName(ATT_NAME).setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE);
        featureTypeBuilder.addAttribute(String.class).setName(ATT_SAMPLED)
                .setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(GMLConvention.NILLABLE_CHARACTERISTIC);
        featureTypeBuilder.addAttribute(Geometry.class).setName(ATT_POSITION).addRole(AttributeRole.DEFAULT_GEOMETRY);
        try {
            types.add(null, OM_TN_SAMPLINGPOINT, featureTypeBuilder.build());
        } catch (IllegalNameException ex) {
            //won't happen
            throw new FeatureStoreRuntimeException(ex);
        }

        return types;
    }
}
