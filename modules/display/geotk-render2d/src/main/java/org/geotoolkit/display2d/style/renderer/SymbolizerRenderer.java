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

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.presentation.Presentation;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;


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
     * Paint the graphic object using the cached symbolizer and the rendering parameters.
     *
     * @param graphic : cached graphic representation of a feature
     * @return true if any data has been rendered
     * @throws PortrayalException
     */
    boolean portray(ProjectedObject graphic) throws PortrayalException;

    /**
     * Paint in one iteration a complete set of features.
     *
     * @param resource resource to portray
     * @return true if any data has been rendered
     * @throws PortrayalException
     */
    default boolean portray(Resource resource) throws PortrayalException {
        final CanvasMonitor monitor = getRenderingContext().getMonitor();
        if (resource instanceof FeatureSet) {
            final FeatureSet fs = (FeatureSet) resource;
            try (RenderingRoutines.GraphicIterator graphics = RenderingRoutines.getIterator(fs, getRenderingContext())) {
                boolean dataRendered = false;
                while (graphics.hasNext()) {
                    if (monitor.stopRequested()) return dataRendered;
                    dataRendered |= portray(graphics.next());
                }
                return dataRendered;
            } catch (IOException | DataStoreException ex) {
                throw new PortrayalException(ex.getMessage(), ex);
            }
        } else {
            throw new PortrayalException("Unsupported resource : " + resource);
        }
    }

    /**
     * Test if the graphic object hit the given search area.
     *
     * @param graphic : cached graphic representation of a feature
     * @param mask : search area, it can represent a mouse position or a particular shape
     * @param filter : the type of searching, intersect or within
     * @return true if the searcharea hit this graphic object, false otherwise.
     */
    boolean hit(ProjectedObject graphic, SearchAreaJ2D mask, VisitFilter filter);

    /**
     * Paint the graphic object using the cached symbolizer and the rendering parameters.
     *
     * @param graphic : cached graphic representation of a coverage
     * @return true if any data has been rendered
     * @throws PortrayalException
     */
    boolean portray(ProjectedCoverage graphic) throws PortrayalException;

    /**
     * Test if the graphic object hit the given search area.
     *
     * @param graphic : cached graphic representation of a coverage
     * @param mask : search area, it can represent a mouse position or a particular shape
     * @param filter : the type of searching, intersect or within
     * @return true if the searcharea hit this graphic object, false otherwise.
     */
    boolean hit(ProjectedCoverage graphic, SearchAreaJ2D mask, VisitFilter filter);

    /**
     * Obtain the presentation for given graphic.
     *
     * @param resource : cached graphic object
     * @return Spliterator never null
     * @throws PortrayalException
     */
    default Spliterator<Presentation> presentation(Resource resource) throws PortrayalException {
        throw new UnsupportedOperationException();
    }

    /**
     * Obtain the presentation for given graphic.
     *
     * @param graphic : cached graphic object
     * @return Spliterator never null
     * @throws PortrayalException
     */
    default Spliterator<Presentation> presentation(ProjectedObject graphic) throws PortrayalException {
        return presentation(Collections.singleton(graphic).iterator());
    }

    /**
     * Obtain the presentation for given graphics.
     *
     * @param graphics : iterator over all graphics to render
     * @return Spliterator never null
     * @throws PortrayalException
     */
    default Spliterator<Presentation> presentation(Iterator<? extends ProjectedObject> graphics) throws PortrayalException {
        return Spliterators.emptySpliterator();
    }

}
