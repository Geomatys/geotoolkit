/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.map;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureSource;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.style.MutableStyle;

import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Utility class to create MapLayers, MapContexts and Elevation models from different sources.
 * This class is thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class MapBuilder {

    private MapBuilder(){}

    /**
     * Create a Default Mapcontext object.
     */
    public static MapContext createContext(final CoordinateReferenceSystem crs){
        return new DefaultMapContext(crs);
    }

    /**
     * Create an empty map layer without any datas. It can be usefull in different
     * kind of applications, like holding a space in the mapcontext for a layer
     * when a datastore is unavailable.
     */
    public static EmptyMapLayer createEmptyMapLayer(){
        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        final MutableStyleFactory factory = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        return new EmptyMapLayer(factory.style());
    }
    /**
     * Create a default feature maplayer with a featuresource and a style.
     */
    public static FeatureMapLayer createFeatureLayer(final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource, final MutableStyle style){
        return new DefaultFeatureMapLayer(featureSource, style);
    }

    /**
     * Create a default feature maplayer with a featurecollection and a style.
     */
    public static FeatureMapLayer createFeatureLayer(final FeatureCollection<SimpleFeatureType, SimpleFeature> collection, final MutableStyle style){
        return createFeatureLayer( DataUtilities.source(collection), style );
    }

    /**
     * Create a default coverage maplayer with a gridCoverage, a style and the grid name.
     */
    public static CoverageMapLayer createCoverageLayer(final GridCoverage2D grid, final MutableStyle style, final String name){
        if(name == null){
            throw new NullPointerException("Name can not be null");
        }
        return new DefaultCoverageMapLayer(new SimpleCoverageReader(grid), style, new DefaultName(name) );
    }

    /**
     * Create a default coverage maplayer with a coverageReader, a style and the grid name.
     */
    public static CoverageMapLayer createCoverageLayer(final CoverageReader reader, final MutableStyle style, final String name){
         if(name == null){
            throw new NullPointerException("Name can not be null");
        }
        return new DefaultCoverageMapLayer(reader, style, new DefaultName(name) );
    }

    /**
     * Create a default elevation model based on a grid coverage reader.
     *
     * @param grid : Coverage reader holding elevation values
     * @return ElevationModel
     */
    public static ElevationModel createElevationModel(final CoverageReader grid){
        FilterFactory FF = FactoryFinder.getFilterFactory(null);
        return new DefaultElevationModel(grid, FF.literal(0),FF.literal(1));
    }

    /**
     * Create a default elevation model based on a grid coverage reader.
     *
     * @param grid : Coverage reader holding elevation values
     * @param offset : expression used to modified on the fly the elevation value
     * @param scale : a multiplication factor to use on the coverage values
     * @return ElevationModel
     */
    public static ElevationModel createElevationModel(final CoverageReader grid, Expression offset, Expression scale){
        return new DefaultElevationModel(grid, offset,scale);
    }

}
