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

import com.vividsolutions.jts.geom.Geometry;
import java.util.List;
import java.util.Map;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.Converters;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultFeatureMapper implements FeatureMapper {

    private final SimpleFeatureBuilder builder;
    private final SimpleFeatureType typeSource;
    private final SimpleFeatureType typeTarget;
    private final Map<PropertyDescriptor, Object> defaults;
    private final Map<PropertyDescriptor, List<PropertyDescriptor>> mapping;
    private int id = 1;

    public DefaultFeatureMapper(final SimpleFeatureType typeSource, final SimpleFeatureType typeTarget,
            final Map<PropertyDescriptor, List<PropertyDescriptor>> mapping,
            final Map<PropertyDescriptor, Object> defaults) {
        this.typeSource = typeSource;
        this.typeTarget = typeTarget;
        this.mapping = mapping;
        this.defaults = defaults;

        this.builder = new SimpleFeatureBuilder(typeTarget);
    }

    @Override
    public Feature transform(final Feature feature) {
        builder.reset();

        //set all default values
        for (final PropertyDescriptor desc : typeTarget.getAttributeDescriptors()) {
            Object val = defaults.get(desc);
            if (val == null) {
                val = ((AttributeDescriptor) desc).getDefaultValue();
            }
            try {
                builder.set(desc.getName(), Converters.convert(val, desc.getType().getBinding()));
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
                    builder.set(targetDesc.getName(), converted);
                }
            }
        }

        return builder.buildFeature("" + id++);
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
                if(!CRS.equalsIgnoreMetadata(sourceCRS,targetCRS)){
                    //crs are different, reproject source geometry
                    try {
                        candidateGeom = JTS.transform(candidateGeom, CRS.findMathTransform(sourceCRS, targetCRS, true));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }

                candidateGeom = MappingUtils.convertType(candidateGeom,targetGeomDesc.getType().getBinding());
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
            return Converters.convert(value, target.getType().getBinding());
        }catch(Exception ex){
            ex.printStackTrace();
            //could not convert between types
            return null;
        }
    }

}
