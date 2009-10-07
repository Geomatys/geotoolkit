/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.data.ServiceInfo;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStore;
import org.geotoolkit.feature.FeatureTypeUtilities;

/**
 * ServiceInfo for ShapefileDataStore.
 *
 * @author Jody Garnett (Refractions Research)
 * @author Johann Sorel (Geomatys)
 */
public class ShapefileServiceInfo implements ServiceInfo {
    private final ShapefileDataStore shapefile;

    ShapefileServiceInfo(ShapefileDataStore shapefile) {
        this. shapefile = shapefile;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URI getSchema() {
        // consider URI of the shapefile specification?
        return FeatureTypeUtilities.DEFAULT_NAMESPACE;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URI getPublisher() {
        if(shapefile.isLocal()){
            String user = System.getProperty("user.name");
            try {
                return new URI( user );
            } catch (URISyntaxException e) {
                return null;
            }
        }else{
            return null; // current user? last person to modify the file
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        final StringBuffer buf = new StringBuffer();
        buf.append( shapefile.getCurrentTypeName() );
        if(shapefile.isLocal()){
            buf.append( " local shapefile" );
        }else{
            buf.append( " non local shapefile." );
        }

        return buf.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getTitle() {
        return shapefile.getCurrentTypeName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URI getSource() {
        String url = shapefile.shpFiles.get( ShpFileType.SHP );
        try {
            return new URI( url );
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<String> getKeywords() {
        Set<String> words = new HashSet<String>();
        words.add( shapefile.getCurrentTypeName() );
        words.add( "shp" );
        words.add( "dbf" );
        words.add( "shx" );
        if( shapefile instanceof IndexedShapefileDataStore ){
            IndexedShapefileDataStore indexed = (IndexedShapefileDataStore) shapefile;
            if( indexed.indexUseable( ShpFileType.QIX ) ){
                words.add( "qix" );
            }
        }
        words.add( "shapefile" );

        return words;
    }
}
