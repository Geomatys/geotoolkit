/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.container.stateless;

import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.DefaultProjectedObject;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Not thread safe.
 * Use it knowing you make clear cache operation in a synchronize way.
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultProjectedFeature extends DefaultProjectedObject<Feature> implements ProjectedFeature {

    public DefaultProjectedFeature(final StatelessContextParams<FeatureMapLayer> params){
        this(params,null);
    }

    public DefaultProjectedFeature(final StatelessContextParams<FeatureMapLayer> params, 
            final Feature feature){
        super(params,feature);
    }

    @Override
    public ProjectedGeometry getGeometry(String name) {
        if(name == null) name = DEFAULT_GEOM;
        DefaultProjectedGeometry proj = geometries.get(name);
        
        CoordinateReferenceSystem dataCRS = null;
        if(proj == null){
            
            final FeatureType featuretype = candidate.getType();
            final PropertyDescriptor prop;
            if (name != null && !name.trim().isEmpty()) {
                prop = featuretype.getDescriptor(name);
            }else if(featuretype != null){
                prop = featuretype.getGeometryDescriptor();
            }else{
                prop = null;
            }

            if(prop != null){
                dataCRS = ((GeometryDescriptor)prop).getCoordinateReferenceSystem();
            } 
            
            proj = new DefaultProjectedGeometry(params);
            geometries.put(name, proj);
        }
        
        //check that the geometry is set
        if(!proj.isSet()){
            proj.setDataGeometry(GO2Utilities.getGeometry(candidate, name),dataCRS);
        }
        
        return proj;
    }

    @Override
    public FeatureMapLayer getLayer() {
        return (FeatureMapLayer) params.layer;
    }

    @Override
    public FeatureId getFeatureId() {
        return candidate.getIdentifier();
    }

}
