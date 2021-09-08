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

import java.util.logging.Level;
import java.util.stream.Stream;
import org.apache.sis.internal.storage.query.FeatureQuery;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import static org.geotoolkit.display2d.primitive.DefaultProjectedObject.DEFAULT_GEOM;
import org.geotoolkit.feature.FeatureExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Expression;
import org.opengis.filter.ValueReference;
import org.opengis.filter.ResourceId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Convenient representation of a feature for rendering.
 *
 * Not thread safe.
 * Use it knowing you make clear cache operation in a synchronize way.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ProjectedFeature extends DefaultProjectedObject<Feature> {

    private final boolean fullFeature;

    public ProjectedFeature(RenderingContext2D params) {
        this(params,null);
    }

    public ProjectedFeature(RenderingContext2D params, final Feature feature) {
        super(params,feature);
        fullFeature = false;
        layer = null;
    }

    /**
     * Get the feature itself.
     *
     * @return Feature
     */
    @Override
    public Feature getCandidate(){
        if (fullFeature) {
            try {
                return getCompleteFeature(getFeatureId());
            } catch (DataStoreException ex) {
                Logging.getLogger("org.geotoolkit.display2d.primitive").log(Level.WARNING, null, ex);
            }
        }

        //worst case, return the partial feature
        return candidate;
    }

    @Override
    public ProjectedGeometry getGeometry(Expression geomExp) {
        if (geomExp == null) geomExp = DEFAULT_GEOM;
        ProjectedGeometry proj = geometries.get(geomExp);

        CoordinateReferenceSystem dataCRS = null;
        if (proj == null) {

            final FeatureType featuretype = candidate.getType();
            PropertyType prop = null;

            if (!isNullorEmpty(geomExp)) {
                if (geomExp instanceof ValueReference) {
                    prop = featuretype.getProperty(((ValueReference)geomExp).getXPath());
                } else {
                    //calculated geometry
                }
            } else if (featuretype != null) {
                try {
                    prop = FeatureExt.getDefaultGeometry(featuretype);
                } catch (PropertyNotFoundException ex) {
                }
            }

            if (prop != null) {
                dataCRS = FeatureExt.getCRS(prop);
            }

            proj = new ProjectedGeometry(params);
            geometries.put(geomExp, proj);
        }

        //check that the geometry is set
        if (!proj.isSet()) {
            proj.setDataGeometry(GO2Utilities.getGeometry(candidate, geomExp), dataCRS);
        }
        return proj;
    }

    private static boolean isNullorEmpty(Expression exp){
        if (exp == null) {
            return true;
        } else if (exp instanceof ValueReference) {
            final ValueReference pn = (ValueReference) exp;
            final String str = pn.getXPath();
            if (str == null || str.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the id of the feature.
     *
     * @return ResourceId
     */
    public ResourceId getFeatureId() {
        return FeatureExt.getId(candidate);
    }

    private Feature getCompleteFeature(final ResourceId id)throws DataStoreException{

        final MapLayer fml = getLayer();
        if (fml != null && fml.getData() instanceof FeatureSet) {
            Feature feature;

            final FeatureQuery query = new FeatureQuery();
            query.setSelection(id);
            final FeatureSet collection = ((FeatureSet) fml.getData()).subset(query);
            try (Stream<Feature> features = collection.features(false)) {
                feature = features.findAny().orElse(null);
            }
            if (feature == null) {
                //worst case, return the partial feature
                return this.candidate;
            }
            return feature;
        } else {
            //worst case, return the partial feature
            return candidate;
        }
    }
}
