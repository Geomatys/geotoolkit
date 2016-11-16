/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gui.swing.render2d.control.edition;

import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.apache.sis.util.iso.SimpleInternationalString;

/**
 * Edition tool displaying a dialog to edit the geometry using Well Known Text.
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FeatureEditTool extends AbstractEditionTool {

    public FeatureEditTool() {
        super(2000,"featureEdit", MessageBundle.formatInternational(MessageBundle.Keys.editor),
             new SimpleInternationalString("editor"), 
             null,FeatureMapLayer.class);
    }

    @Override
    public EditionDelegate createDelegate(final JMap2D map, final Object candidate) {
        return new FeatureEditTDelegate(map, (FeatureMapLayer) candidate);
    }
}
