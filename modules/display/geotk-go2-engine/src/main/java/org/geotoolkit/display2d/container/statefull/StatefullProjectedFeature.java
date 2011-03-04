/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.container.statefull;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.DefaultProjectedObject;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.map.FeatureMapLayer;

import org.opengis.feature.Feature;
import org.opengis.filter.identity.FeatureId;

/**
 * Not thread safe.
 * Use it knowing you make clear cache operation in a synchronize way.
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullProjectedFeature extends DefaultProjectedObject<Feature> implements ProjectedFeature {

    public StatefullProjectedFeature(final StatefullContextParams<FeatureMapLayer> params){
        this(params,null);
    }

    public StatefullProjectedFeature(final StatefullContextParams<FeatureMapLayer> params, 
            final Feature feature){
        super(params,feature);
    }

    @Override
    public ProjectedGeometry getGeometry(String name) {
        if(name == null) name = DEFAULT_GEOM;
        StatefullProjectedGeometry proj = geometries.get(name);
        if(proj == null){
            Geometry geom = GO2Utilities.getGeometry(candidate, name);
            if(geom != null){
                final Class geomClass = GO2Utilities.getGeometryClass(candidate.getType(), name);
                final StatefullProjectedGeometry projectedGeom = new StatefullProjectedGeometry(params, geomClass, geom);
                geometries.put(name, projectedGeom);
                return projectedGeom;
            }
        }else{
            //check that the geometry is set
            if(proj.getObjectiveGeometryJTS() == null){
                proj.setObjectiveGeometry(GO2Utilities.getGeometry(candidate, name));
            }
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
