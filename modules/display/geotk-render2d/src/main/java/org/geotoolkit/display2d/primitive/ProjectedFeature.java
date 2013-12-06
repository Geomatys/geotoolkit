/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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
package org.geotoolkit.display2d.primitive;

import java.util.Collections;
import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.display2d.GO2Utilities.FILTER_FACTORY;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import static org.geotoolkit.display2d.primitive.DefaultProjectedObject.DEFAULT_GEOM;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Convenient representation of a feature for rendering.
 *
 * Not thread safe.
 * Use it knowing you make clear cache operation in a synchronize way.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ProjectedFeature extends DefaultProjectedObject<Feature> {

    private final boolean fullFeature;

    public ProjectedFeature(final J2DCanvas canvas, final FeatureMapLayer layer, final Feature feature){
        super(new StatelessContextParams(canvas,layer),feature);
        final RenderingContext2D context = new RenderingContext2D(canvas);
        canvas.prepareContext(context, null, null);
        params.update(context);
        fullFeature = true;

    }

    public ProjectedFeature(final StatelessContextParams<FeatureMapLayer> params){
        this(params,null);
    }

    public ProjectedFeature(final StatelessContextParams<FeatureMapLayer> params,
            final Feature feature){
        super(params,feature);
        fullFeature = false;
    }

    /**
     * Get the feature itself.
     *
     * @return Feature
     */
    @Override
    public Feature getCandidate(){
        if(fullFeature){
            try {
                return getCompleteFeature(getFeatureId());
            } catch (DataStoreException ex) {
                Logging.getLogger(ProjectedFeature.class).log(Level.WARNING, null, ex);
            }
        }

        //worst case, return the partial feature
        return candidate;
    }

    @Override
    public ProjectedGeometry getGeometry(String name) {
        if(name == null) name = DEFAULT_GEOM;
        ProjectedGeometry proj = geometries.get(name);

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

            proj = new ProjectedGeometry(params);
            geometries.put(name, proj);
        }

        //check that the geometry is set
        if(!proj.isSet()){
            proj.setDataGeometry(GO2Utilities.getGeometry(candidate, name),dataCRS);
        }

        return proj;
    }

    /**
     * Get the original FeatureMapLayer from where the feature is from.
     *
     * @return FeatureMapLayer
     */
    @Override
    public FeatureMapLayer getLayer() {
        return (FeatureMapLayer) params.layer;
    }

    /**
     * Get the id of the feature.
     *
     * @return FeatureId
     */
    public FeatureId getFeatureId() {
        return candidate.getIdentifier();
    }

    private Feature getCompleteFeature(final FeatureId id)throws DataStoreException{

        final FeatureMapLayer fml = (FeatureMapLayer) params.layer;
        if(fml != null){
            final Filter filter = FILTER_FACTORY.id(Collections.singleton(id));
            Feature feature = null;

            final FeatureCollection<? extends Feature> collection =
                    fml.getCollection().subCollection(
                    QueryBuilder.filtered(fml.getCollection().getFeatureType().getName(), filter));

            if(!collection.isEmpty()){
                final FeatureIterator<? extends Feature> ite = collection.iterator();
                if(ite.hasNext()){
                    feature = ite.next();
                }
                ite.close();
            }

            if(feature == null){
                //worst case, return the partial feature
                return this.candidate;
            }

            return feature;
        }else{
            //worst case, return the partial feature
            return candidate;
        }
    }

}
