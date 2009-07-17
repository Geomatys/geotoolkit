/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile;

import java.io.IOException;
import java.util.Set;

import org.geotoolkit.data.AbstractFeatureLocking;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.FeatureListener;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.ResourceInfo;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.feature.simple.SimpleFeatureType;

public class ShapefileFeatureLocking extends AbstractFeatureLocking {
    /**
     * 
     */
    private final ShapefileDataStore shapefile;
    private final SimpleFeatureType featureType;
    public ShapefileFeatureLocking( ShapefileDataStore shapefileDataStore, Set hints, SimpleFeatureType featureType ) {
        super(hints);
        shapefile = shapefileDataStore;
        this.featureType = featureType;
    }
    public DataStore getDataStore() {
        return shapefile;
    }
    public void addFeatureListener(FeatureListener listener) {
        shapefile.listenerManager.addFeatureListener(this, listener);
    }
    public void removeFeatureListener(FeatureListener listener) {
        shapefile.listenerManager.removeFeatureListener(this, listener);
    }
    public SimpleFeatureType getSchema() {
        return featureType;
    }
    public JTSEnvelope2D getBounds(Query query)
            throws IOException {
        return shapefile.getBounds(query);
    }
    public ResourceInfo getInfo(){
        return shapefile.getInfo( featureType.getTypeName() );
    }
}
