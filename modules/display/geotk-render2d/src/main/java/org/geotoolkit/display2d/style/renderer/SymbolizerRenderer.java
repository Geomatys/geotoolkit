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
package org.geotoolkit.display2d.style.renderer;

import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.renderer.Presentation;
import org.opengis.feature.Feature;


/**
 * A symbolizer renderer is capable to paint a given symbolizer on a java2d
 * canvas.
 *
 * To perform some visual intersection test using the hit methods.
 *
 * And you can generate glyphs using the glyph method.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface SymbolizerRenderer {

    /**
     * Original SymbolizerRendererService.
     *
     * @return SymbolizerRendererService
     */
    SymbolizerRendererService getService();

    /**
     * Get Rendering context this renderer is associated to.
     * @return RenderingContext2D, not null
     */
    RenderingContext2D getRenderingContext();

    /**
     * Obtain the presentation for given resource.Default implementation loops on each feature if resource is a FeatureSet.
     * If resource is an Aggregate, loops on each components and concatenate streams.
     *
     * @param layer
     * @param resource
     * @return Stream never null, can be empty
     * @throws PortrayalException
     * @throws BackingStoreException in stream iteration
     */
    default Stream<Presentation> presentations(MapLayer layer, Resource resource) throws PortrayalException {
        if (resource instanceof FeatureSet) {
            final FeatureSet fs = (FeatureSet) resource;

            try {
                return fs.features(false).flatMap(new Function<Feature, Stream<Presentation>>() {
                    @Override
                    public Stream<Presentation> apply(Feature t) {
                        try {
                            return presentations(layer, t);
                        } catch (PortrayalException ex) {
                            throw new BackingStoreException(ex);
                        }
                    }
                });
            } catch (DataStoreException ex) {
                throw new PortrayalException(ex);
            }

        } else if (resource instanceof Aggregate) {
            final Aggregate agg = (Aggregate) resource;
            try {
                return agg.components().stream().flatMap(new Function<Resource, Stream<Presentation>>() {
                    @Override
                    public Stream<Presentation> apply(Resource t) {
                        try {
                            return presentations(layer, t);
                        } catch (PortrayalException ex) {
                            throw new BackingStoreException(ex);
                        }
                    }
                });
            } catch (DataStoreException ex) {
                throw new PortrayalException(ex);
            }
        }
        return Stream.empty();
    }

    /**
     * Obtain the presentation for given graphic.
     *
     * @param layer
     * @param feature
     * @return Stream never null, can be empty
     * @throws PortrayalException
     * @throws BackingStoreException in stream iteration
     */
    default Stream<Presentation> presentations(MapLayer layer, Feature feature) throws PortrayalException {
        return Stream.empty();
    }
}
