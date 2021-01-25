/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.portrayal.MapLayer;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultGroupPresentation extends AbstractPresentation implements GroupPresentation {

    public final List<Presentation> elements = new ArrayList<>();

    public DefaultGroupPresentation(MapLayer layer, Feature feature) {
        super(layer, feature);
    }

    @Override
    public List<Presentation> elements() {
        return elements;
    }
}
