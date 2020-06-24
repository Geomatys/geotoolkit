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

import java.util.Collection;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.RandomStyleBuilder;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.Description;
import org.opengis.style.StyleFactory;

/**
 * Utility class to create MapLayers, MapContexts and Elevation models from different sources.
 * This class is thread safe.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class MapBuilder {

    private MapBuilder(){}

    /**
     * Create a Default Mapcontext object using coordinate reference system : CRS:84.
     * @return MapContext
     */
    public static MapContext createContext(){
        return createContext(CommonCRS.WGS84.normalizedGeographic());
    }

    /**
     * Create a Default Mapcontext object with the given coordinate reference system.
     * The crs is not used for rendering, it is only used when calling the getEnvelope
     * method.
     * @param crs : mapcontext CoordinateReferenceSystem
     * @return MapContext
     */
    public static MapContext createContext(final CoordinateReferenceSystem crs){
        return new MapContext(crs);
    }

    /**
     * Create a Default MapItem object. It can be used to group layers.
     * @return MapItem
     */
    public static MapItem createItem(){
        return new MapContext(CommonCRS.WGS84.normalizedGeographic());
    }

    /**
     * Create an empty map layer without any datas. It can be useful in different
     * kind of applications, like holding a space in the map context for a layer
     * when a featurestore is unavailable.
     * @return MapLayer
     */
    public static MapLayer createEmptyMapLayer(){
        return new AbstractMapLayer(null);
    }

    /**
     * Create a map layer with a resource.
     * @param resource layer resource
     * @return MapLayer
     */
    public static MapLayer createLayer(final Resource resource){
        if (resource instanceof FeatureSet) {
            return createFeatureLayer((FeatureSet) resource);
        } else if (resource instanceof GridCoverageResource) {
            return createCoverageLayer((GridCoverageResource) resource);
        } else {
            throw new IllegalArgumentException("Unsupported resource");
        }
    }

    /**
     * Create a default feature map layer with a feature collection and a style.
     * @param collection layer data collection
     * @return FeatureMapLayer
     * @deprecated use createLayer method instead
     */
    @Deprecated
    public static FeatureMapLayer createFeatureLayer(final FeatureSet collection){
        MutableStyle style;
        String name = "";
        Description description = null;
        try {
            final FeatureType type = collection.getType();
            name = type.getName().tip().toString();
            description = new DefaultDescription(
                    new SimpleInternationalString(name),
                    new SimpleInternationalString(type.getName().toString()));
            style = RandomStyleBuilder.createDefaultVectorStyle(type);
        } catch (DataStoreException ex) {
            style = ((MutableStyleFactory)DefaultFactories.forBuildin(StyleFactory.class)).style(RandomStyleBuilder.createRandomPointSymbolizer());
        }
        final FeatureMapLayer maplayer = new FeatureMapLayer(collection, style);
        maplayer.setName(name);
        if (description != null) maplayer.setDescription(description);
        return maplayer;
    }

    /**
     * Create a default feature map layer with a feature collection and a style.
     * @param collection layer data collection
     * @param style layer style
     * @return FeatureMapLayer
     * @deprecated use createLayer method instead
     */
    @Deprecated
    public static FeatureMapLayer createFeatureLayer(final FeatureSet collection, final MutableStyle style) {
        return new FeatureMapLayer(collection, style);
    }

    /**
     * Create a default coverage map layer with a gridCoverage, a style and the grid name.
     * @param grid GridCoverage2D
     * @param style layer style
     * @return  CoverageMapLayer
     * @deprecated use createLayer method instead
     */
    @Deprecated
    public static CoverageMapLayer createCoverageLayer(final GridCoverage grid, final MutableStyle style, final String name) {
        final GridCoverageResource ref = new InMemoryGridCoverageResource(NamesExt.create(name), grid);
        return createCoverageLayer(ref, style);
    }

    /**
     * Create a default coverage map layer with a image input.
     * Default style is used.
     *
     * @param ref input
     * @return  CoverageMapLayer
     * @deprecated use createLayer method instead
     */
    @Deprecated
    public static CoverageMapLayer createCoverageLayer(final Object input) {
        final GridCoverageResource resource;
        if (input instanceof GridCoverageResource) {
            resource = (GridCoverageResource) input;
        } else if (input instanceof GridCoverage) {
            resource = new InMemoryGridCoverageResource((GridCoverage) input);
        } else {
            try {
                DataStore store = DataStores.open(input);
                Collection<GridCoverageResource> lst = org.geotoolkit.storage.DataStores.flatten(store, true, GridCoverageResource.class);
                if (!lst.isEmpty()) {
                    resource = lst.iterator().next();
                } else {
                    throw new IllegalArgumentException("Given input could not be resolved as a coverage.");
                }
            } catch (DataStoreException ex) {
                throw new IllegalArgumentException("Given input could not be resolved as a coverage."+ex.getMessage(), ex);
            }
        }
        return createCoverageLayer(resource);
    }

    /**
     * Create a default coverage map layer with a coveragrReference.
     * Default style is used.
     *
     * @param ref CoverageResource
     * @return  CoverageMapLayer
     * @deprecated use createLayer method instead
     */
    @Deprecated
    public static CoverageMapLayer createCoverageLayer(final GridCoverageResource ref){
        return new CoverageMapLayer(ref, RandomStyleBuilder.createDefaultRasterStyle());
    }

    /**
     * Create a default coverage map layer with a coveragrReference, a style and the grid name.
     * @param ref CoverageResource
     * @param style layer style
     * @return  CoverageMapLayer
     * @deprecated use createLayer method instead
     */
    @Deprecated
    public static CoverageMapLayer createCoverageLayer(final GridCoverageResource ref, final MutableStyle style){
        return new CoverageMapLayer(ref, style);
    }

    /**
     * Create a default coverage map layer with a coveragrReference, a style and the grid name.
     * @param input CoverageResource or input
     * @param style layer style
     * @return  CoverageMapLayer
     * @deprecated use createLayer method instead
     */
    @Deprecated
    public static CoverageMapLayer createCoverageLayer(final Object input, final MutableStyle style){
        CoverageMapLayer layer = createCoverageLayer(input);
        if (style != null) layer.setStyle(style);
        return layer;
    }
 }
