/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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

import java.util.Arrays;
import java.util.Collection;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.DefaultCoverageReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.coverage.memory.MemoryCoverageReader;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.StyleConstants;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
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
     * Create a Default Mapcontext object using coordinate reference system : CRS:84.
     * @return MapContext
     */
    public static MapContext createContext(){
        return createContext(DefaultGeographicCRS.WGS84);
    }

    /**
     * Create a Default Mapcontext object with the given coordinate reference system.
     * The crs is not used for rendering, it is only used when calling the getEnvelope
     * method.
     * @param crs : mapcontext CoordinateReferenceSystem
     * @return MapContext 
     */
    public static MapContext createContext(final CoordinateReferenceSystem crs){
        return new DefaultMapContext(crs);
    }

    /**
     * Create a Default MapItem object. It can be used to group layers.
     * @return MapItem
     */
    public static MapItem createItem(){
        return new DefaultMapItem();
    }

    /**
     * Create an empty map layer without any datas. It can be useful in different
     * kind of applications, like holding a space in the map context for a layer
     * when a featurestore is unavailable.
     * @return EmptyMapLayer
     */
    public static EmptyMapLayer createEmptyMapLayer(){
        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        final MutableStyleFactory factory = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        return new EmptyMapLayer(factory.style());
    }

    //geometryType
    /**
     * Create a default collection map layer with a collection.
     * The style expect the default geometry property to be named 'Geometry'
     * 
     * @param collection layer data collection
     * @param style layer style
     * @return CollectionMapLayer
     */
    public static CollectionMapLayer createCollectionLayer(final Collection<?> collection){
        final MutableStyleFactory sf = new DefaultStyleFactory();
        final FilterFactory ff = FactoryFinder.getFilterFactory(null);
        final MutableStyle style = sf.style();
        final MutableFeatureTypeStyle fts = sf.featureTypeStyle();
        
        final MutableRule rulePoint = sf.rule(StyleConstants.DEFAULT_POINT_SYMBOLIZER);
        rulePoint.setFilter(ff.or(
                                ff.equals(ff.function("geometryType", ff.property("geometry")), ff.literal("Point")),
                                ff.equals(ff.function("geometryType", ff.property("geometry")), ff.literal("MultiPoint"))
                            ));
        final MutableRule ruleLine = sf.rule(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
        ruleLine.setFilter(ff.or(
                                ff.equals(ff.function("geometryType", ff.property("geometry")), ff.literal("LineString")),
                                ff.equals(ff.function("geometryType", ff.property("geometry")), ff.literal("MultiLineString"))
                            ));
        final MutableRule rulePolygon = sf.rule(StyleConstants.DEFAULT_POLYGON_SYMBOLIZER);
        rulePolygon.setFilter(ff.or(
                                ff.equals(ff.function("geometryType", ff.property("geometry")), ff.literal("Polygon")),
                                ff.equals(ff.function("geometryType", ff.property("geometry")), ff.literal("MultiPolygon"))
                            ));
        
        fts.rules().add(rulePoint);
        fts.rules().add(ruleLine);
        fts.rules().add(rulePolygon);
        style.featureTypeStyles().add(fts);
        
        return createCollectionLayer(collection, style);
    }
    
    /**
     * Create a default collection map layer with a collection and a style.
     * @param collection layer data collection
     * @param style layer style
     * @return CollectionMapLayer
     */
    public static CollectionMapLayer createCollectionLayer(final Collection<?> collection, final MutableStyle style){
        return new DefaultCollectionMapLayer(collection, style);
    }

    /**
     * Create a default feature map layer with a feature collection and a style.
     * @param collection layer data collection
     * @param style layer style
     * @return FeatureMapLayer
     */
    public static FeatureMapLayer createFeatureLayer(final FeatureCollection<? extends Feature> collection, final MutableStyle style){
        return new DefaultFeatureMapLayer(collection, style);
    }

    /**
     * Create a default coverage map layer with a gridCoverage, a style and the grid name.
     * @param grid GridCoverage2D
     * @param style layer style
     * @param name layer name
     * @return  CoverageMapLayer
     */
    public static CoverageMapLayer createCoverageLayer(final GridCoverage2D grid, final MutableStyle style, final String name){
        return createCoverageLayer(new MemoryCoverageReader(grid), 0, style, name);
    }

    /**
     * Create a default coverage map layer with a coverageReader, a style and the grid name.
     * @param reader GridCoverageReader
     * @param imageIndex image index in the reader
     * @param style layer style
     * @param name layer name
     * @return  CoverageMapLayer
     */
    public static CoverageMapLayer createCoverageLayer(final GridCoverageReader reader, 
            final int imageIndex, final MutableStyle style, final String name){
        final CoverageReference ref = new DefaultCoverageReference(reader, imageIndex);
        return createCoverageLayer(ref, style, name);
    }

    /**
     * Create a default coverage map layer with a coveragrReference, a style and the grid name.
     * @param ref CoverageReference
     * @param style layer style
     * @param name layer name
     * @return  CoverageMapLayer
     */
    public static CoverageMapLayer createCoverageLayer(final CoverageReference ref, final MutableStyle style, final String name){
        ensureNonNull("name", name);
        final CoverageMapLayer layer = new DefaultCoverageMapLayer(ref, style, new DefaultName(name) );
        layer.setDescription(new DefaultDescription(new SimpleInternationalString(name), new SimpleInternationalString(name)));
        return layer;
    }

    /**
     * Create a default elevation model based on a grid coverage reader.
     *
     * @param grid : Coverage reader holding elevation values
     * @return ElevationModel
     */
    public static ElevationModel createElevationModel(final GridCoverageReader grid){
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
    public static ElevationModel createElevationModel(final GridCoverageReader grid, final Expression offset, final Expression scale){
        return new DefaultElevationModel(grid, offset,scale);
    }

}
