/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.primitive;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.map.FeatureMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.identity.FeatureId;

/**
 * Convinient representation of a feature for rendering.
 * We expect the sub classes to cache information for more efficient rendering.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ProjectedFeature extends Graphic {

    /**
     * Get the id of the feature.
     *
     * @return FeatureId
     */
    FeatureId getFeatureId();

    /**
     * Get the original FeatureMapLayer from where the feature is from.
     *
     * @return FeatureMapLayer
     */
    FeatureMapLayer getFeatureLayer();

    /**
     * Get the feature itself.
     *
     * @return SimpleFeature
     */
    SimpleFeature getFeature();

    /**
     * Get a Projected geometry for rendering purpose.
     *
     * @param name of the wanted geometry.
     * @return ProjectedGeometry or null if the named geometry attribut doesn't exist
     */
    ProjectedGeometry getGeometry(String name);

    /**
     * @return original canvas of this graphic
     */
    ReferencedCanvas2D getCanvas();

}
