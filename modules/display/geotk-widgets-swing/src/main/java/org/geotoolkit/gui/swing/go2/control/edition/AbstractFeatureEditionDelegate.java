/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.go2.control.edition;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;
import org.geotoolkit.gui.swing.go2.decoration.MapDecorationStack;
import org.geotoolkit.map.FeatureMapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class AbstractFeatureEditionDelegate extends AbstractEditionDelegate{

    protected final EditionDecoration decoration = new EditionDecoration();
    protected final EditionHelper helper;

    public AbstractFeatureEditionDelegate(final JMap2D map, FeatureMapLayer layer) {
        super(map);
        this.helper = new EditionHelper(map, layer);
        decoration.setMap2D(map);
    }

    @Override
    public MapDecoration getDecoration() {
        final MapDecoration moveDeco = super.getDecoration();
        return MapDecorationStack.wrap(moveDeco,decoration);
    }

}
