/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;

import org.geotoolkit.data.DataSourceException;
import org.geotoolkit.data.ResourceInfo;
import org.geotoolkit.feature.FeatureTypes;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Information about the contents of a shapefile on disk.
 * @author Jody Garnett (Refractions Reserach Inc)
 */
public class ShapefileFileResourceInfo implements ResourceInfo {

    private ShapefileDataStore shapefile;

    public ShapefileFileResourceInfo( ShapefileDataStore shapefile ) {
        this.shapefile = shapefile;
    }
    
    public JTSEnvelope2D getBounds() {
        try {
            return shapefile.getBounds();
        } catch (DataSourceException e) {
            return new JTSEnvelope2D( getCRS() );
        }
    }

    public CoordinateReferenceSystem getCRS() {
        return shapefile.schema.getCoordinateReferenceSystem();
    }

    public String getDescription() {
        return "Contents of file";
    }

    public Set<String> getKeywords() {
        Set<String> words = new HashSet<String>();
        words.add( shapefile.getCurrentTypeName() );
        words.add( "features" );
        // it would be nice to list the geometry type here...
        return words;
    }

    public String getName() {
        return shapefile.getCurrentTypeName();
    }

    public URI getSchema() {
        return FeatureTypes.DEFAULT_NAMESPACE; // we have features?
        // url to shp (as the header is our schema)
    }

    public String getTitle() {
        return shapefile.getCurrentTypeName();
    }

}
