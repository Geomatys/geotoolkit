/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.renderer;

import org.geotoolkit.map.MapLayer;
import org.opengis.feature.Feature;

/**
 * When rendering resources, the first step is to generate the visual representations
 * of each feature, we call each one a Presentation.
 * <p>
 * A presentation object must be a simplistic description without any evaluation
 * or processing work remaining.
 * </p>
 * <p>
 * It is important be note that multiple presentations may be generated for the
 * same feature.
 * </p>
 *
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Presentation {


    /**
     * Returns the original map layer the feature comes from.
     *
     * @return MapLayer can be null if the presentation is not associated to a layer.
     */
    public MapLayer getLayer();

    /**
     * Returns the original feature having this presentation.
     *
     * @return Feature can be null if the presentation is not associated to a feature.
     */
    public Feature getFeature();

}
