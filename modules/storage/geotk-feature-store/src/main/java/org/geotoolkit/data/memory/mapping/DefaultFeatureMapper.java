/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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
package org.geotoolkit.data.memory.mapping;

import org.geotoolkit.geometry.jts.JTSMapping;
import com.vividsolutions.jts.geom.Geometry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.util.Numbers;
import org.geotoolkit.geometry.jts.JTS;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.ObjectConverters;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.Utilities;


/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultFeatureMapper implements FeatureMapper {

    private final FeatureType typeSource;
    private final FeatureType typeTarget;
    private final Map<PropertyType, Object> defaults;
    private final Map<PropertyType, List<PropertyType>> mapping;
    private int id = 1;

    /**
     * Create a default mapping for properties with the same names.
     *
     * @param typeSource
     * @param typeTarget
     */
    public DefaultFeatureMapper(final FeatureType typeSource, final FeatureType typeTarget) {
        this.typeSource = typeSource;
        this.typeTarget = typeTarget;

        mapping = new HashMap<>();
        defaults = new HashMap<>();

        for(PropertyType desc : typeSource.getProperties(true)){
            try{
                final PropertyType targetDesc = typeTarget.getProperty(desc.getName().toString());
                if(desc instanceof AttributeType && targetDesc instanceof AttributeType){
                    mapping.put(desc, Collections.singletonList(targetDesc));
                }
            }catch(PropertyNotFoundException ex){
                //do nothing
            }
        }

    }

    public DefaultFeatureMapper(final FeatureType typeSource, final FeatureType typeTarget,
            final Map<PropertyType, List<PropertyType>> mapping,
            final Map<PropertyType, Object> defaults) {
        this.typeSource = typeSource;
        this.typeTarget = typeTarget;
        this.mapping = mapping;
        this.defaults = defaults;

    }

    @Override
    public FeatureType getSourceType() {
        return typeSource;
    }

    @Override
    public FeatureType getTargetType() {
        return typeTarget;
    }

    @Override
    public Feature transform(final Feature feature) {
        final Feature res = typeTarget.newInstance();

        //set all default values
        for (final PropertyType desc : typeTarget.getProperties(true)) {
            final AttributeType attType = (AttributeType) desc;

            Object val = defaults.get(desc);
            if (val == null) {
                val = ((AttributeType) desc).getDefaultValue();
            }
            try {
                res.setPropertyValue(desc.getName().toString(),
                        ObjectConverters.convert(val, attType.getValueClass()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        for (final PropertyType sourceDesc : mapping.keySet()) {

            final List<PropertyType> links = mapping.get(sourceDesc);
            if (links == null || links.isEmpty()) {
                continue;
            }

            final AttributeType srcType = (AttributeType) sourceDesc;
            final Object value = feature.getPropertyValue(sourceDesc.getName().toString());

            for (final PropertyType targetDesc : links) {
                Object converted = convert(value, srcType, (AttributeType)targetDesc);
                if (converted != null) {
                    res.setPropertyValue(targetDesc.getName().toString(),converted);
                }
            }
        }

        return res;
    }


    private static Object convert(final Object value, final AttributeType source, final AttributeType target){

        //special case for geometry attributs
        if (AttributeConvention.isGeometryAttribute(source)) {
            final AttributeType sourceGeomDesc = (AttributeType) source;
            Geometry candidateGeom = (Geometry) value;

            if (AttributeConvention.isGeometryAttribute(target)) {
                //must change geometry type and crs if needed
                final AttributeType targetGeomDesc = (AttributeType) target;

                final CoordinateReferenceSystem sourceCRS = FeatureExt.getCRS(sourceGeomDesc);
                final CoordinateReferenceSystem targetCRS = FeatureExt.getCRS(targetGeomDesc);
                if(!Utilities.equalsIgnoreMetadata(sourceCRS,targetCRS)){
                    //crs are different, reproject source geometry
                    try {
                        candidateGeom = JTS.transform(candidateGeom, CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }

                candidateGeom = JTSMapping.convertType(candidateGeom, (Class<Geometry>) targetGeomDesc.getValueClass());
                return candidateGeom;

            }else{
                //types doesnt match
                return null;
            }
        }else if(AttributeConvention.isGeometryAttribute(target)){
            //source attribut doesnt match
            return null;
        }

        //normal attributs type, string, numbers, dates ...
        try{
            return ObjectConverters.convert(value, Numbers.primitiveToWrapper(target.getValueClass()));
        }catch(Exception ex){
            ex.printStackTrace();
            //could not convert between types
            return null;
        }
    }

}
