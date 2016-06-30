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
import org.apache.sis.util.Numbers;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.util.Utilities;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultFeatureMapper implements FeatureMapper {

    private final FeatureType typeSource;
    private final FeatureType typeTarget;
    private final Map<PropertyDescriptor, Object> defaults;
    private final Map<PropertyDescriptor, List<PropertyDescriptor>> mapping;
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

        for(PropertyDescriptor desc : typeSource.getDescriptors()){
            final PropertyDescriptor targetDesc = typeTarget.getDescriptor(desc.getName());
            if(targetDesc!=null){
                mapping.put(desc, Collections.singletonList(targetDesc));
            }
        }

    }

    public DefaultFeatureMapper(final FeatureType typeSource, final FeatureType typeTarget,
            final Map<PropertyDescriptor, List<PropertyDescriptor>> mapping,
            final Map<PropertyDescriptor, Object> defaults) {
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
        final Feature res = FeatureUtilities.defaultFeature(typeTarget, ""+id++);

        //set all default values
        for (final PropertyDescriptor desc : typeTarget.getDescriptors()) {
            Object val = defaults.get(desc);
            if (val == null) {
                val = ((AttributeDescriptor) desc).getDefaultValue();
            }
            try {
                res.getProperty(desc.getName()).setValue(ObjectConverters.convert(val, desc.getType().getBinding()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        for (final PropertyDescriptor sourceDesc : mapping.keySet()) {
            final Object value = feature.getProperty(sourceDesc.getName()).getValue();

            final List<PropertyDescriptor> links = mapping.get(sourceDesc);
            if (links == null || links.isEmpty()) {
                continue;
            }

            for (final PropertyDescriptor targetDesc : links) {
                Object converted = convert(value, sourceDesc, targetDesc);
                if (converted != null) {
                    res.getProperty(targetDesc.getName()).setValue(converted);
                }
            }
        }

        res.getUserData().putAll(feature.getUserData());
        return res;
    }


    private static Object convert(final Object value, final PropertyDescriptor source, final PropertyDescriptor target){

        //special case for geometry attributs
        if(source instanceof GeometryDescriptor){
            final GeometryDescriptor sourceGeomDesc = (GeometryDescriptor) source;
            Geometry candidateGeom = (Geometry) value;

            if(target instanceof GeometryDescriptor){
                //must change geometry type and crs if needed
                final GeometryDescriptor targetGeomDesc = (GeometryDescriptor) target;

                final CoordinateReferenceSystem sourceCRS = sourceGeomDesc.getCoordinateReferenceSystem();
                final CoordinateReferenceSystem targetCRS = targetGeomDesc.getCoordinateReferenceSystem();
                if(!Utilities.equalsIgnoreMetadata(sourceCRS,targetCRS)){
                    //crs are different, reproject source geometry
                    try {
                        candidateGeom = JTS.transform(candidateGeom, CRS.findMathTransform(sourceCRS, targetCRS, true));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }

                candidateGeom = JTSMapping.convertType(candidateGeom, (Class<Geometry>) targetGeomDesc.getType().getBinding());
                return candidateGeom;

            }else{
                //types doesnt match
                return null;
            }
        }else if(target instanceof GeometryDescriptor){
            //source attribut doesnt match
            return null;
        }

        //normal attributs type, string, numbers, dates ...
        try{
            return ObjectConverters.convert(value, Numbers.primitiveToWrapper(target.getType().getBinding()));
        }catch(Exception ex){
            ex.printStackTrace();
            //could not convert between types
            return null;
        }
    }

}
