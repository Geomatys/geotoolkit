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

import org.apache.sis.storage.Resource;
import org.apache.sis.portrayal.MapLayer;
import org.opengis.feature.Feature;
import org.opengis.style.Symbolizer;

/**
 * A presentation build with a standard Symbology Encoding Symbolizer.
 *
 * <p>
 * NOTE: this class is a first draft subject to modifications.
 * </p>
 *
 * @author  Johann Sorel (Geomatys)
 */
public class SEPresentation extends AbstractPresentation {

    private Symbolizer symbolizer;

    public SEPresentation() {
    }

    public SEPresentation(MapLayer layer, Resource resource, Feature candidate, Symbolizer symbolizer) {
        super(layer, resource, candidate);
        this.symbolizer = symbolizer;
    }

    /**
     * @return Symbogy Encoding symbolizer
     */
    public Symbolizer getSymbolizer() {
        return symbolizer;
    }

    public void setSymbolizer(Symbolizer symbolizer) {
        this.symbolizer = symbolizer;
    }

}
