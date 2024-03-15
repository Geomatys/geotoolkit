/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2024, Geomatys
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
package org.geotoolkit.display2d.painter;

import java.util.Objects;
import org.apache.sis.map.Presentation;
import org.apache.sis.map.MapLayer;
import org.apache.sis.storage.Resource;
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
 * @version 1.2
 * @since   1.2
 */
public final class SEPresentation extends Presentation {

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.symbolizer);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SEPresentation other = (SEPresentation) obj;
        if (!Objects.equals(this.symbolizer, other.symbolizer)) {
            return false;
        }
        return super.equals(obj);
    }

}
