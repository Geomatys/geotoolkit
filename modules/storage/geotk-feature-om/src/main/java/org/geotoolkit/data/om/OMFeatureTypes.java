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
import java.util.HashMap;
import java.util.Map;
import static org.geotoolkit.data.AbstractFeatureStore.GML_311_NAMESPACE;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OMFeatureTypes {
    
    private static final String OM_NAMESPACE = "http://www.opengis.net/sampling/1.0";
    //protected static final Name OM_TN_SAMPLINGPOINT = new DefaultName(OM_NAMESPACE, "SamplingPoint");

    protected static final GenericName ATT_DESC     = DefaultName.create(GML_311_NAMESPACE, "description");
    protected static final GenericName ATT_NAME     = DefaultName.create(GML_311_NAMESPACE, "name");
    protected static final GenericName ATT_SAMPLED  = DefaultName.create(OM_NAMESPACE, "sampledFeature");
    protected static final GenericName ATT_POSITION = DefaultName.create(OM_NAMESPACE, "position");

    public static Map<GenericName, FeatureType> getFeatureTypes(final String name) {
        final Map<GenericName, FeatureType> types = new HashMap<>();
        
        final GenericName OM_TN_SAMPLINGPOINT = DefaultName.create(OM_NAMESPACE, name);
        final FeatureTypeBuilder featureTypeBuilder = new FeatureTypeBuilder();
        featureTypeBuilder.setName(OM_TN_SAMPLINGPOINT);
        featureTypeBuilder.add(ATT_DESC,String.class,0,1,true,null);
        featureTypeBuilder.add(ATT_NAME,String.class,1,Integer.MAX_VALUE,false,null);
        featureTypeBuilder.add(ATT_SAMPLED,String.class,0,Integer.MAX_VALUE,true,null);
        featureTypeBuilder.add(ATT_POSITION,Geometry.class,1,1,false,null);
        featureTypeBuilder.setDefaultGeometry(ATT_POSITION);
        types.put(OM_TN_SAMPLINGPOINT, featureTypeBuilder.buildFeatureType());
        
        return types;
    }
}
