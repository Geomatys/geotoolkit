/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011 - 2014, Geomatys
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

import javax.swing.ImageIcon;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;

/**
 * Edition tool displaying a dialog to edit the geometry extracting geometry from the clipboard.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GeometryFromClipboardTool extends AbstractEditionTool {

    private static final ImageIcon ICON_PASTE = IconBuilder.createIcon(FontAwesomeIcons.ICON_CLIPBOARD, 16, FontAwesomeIcons.DEFAULT_COLOR);

    public GeometryFromClipboardTool() {
        super(120,"geometryFromClipboard", MessageBundle.formatInternational(MessageBundle.Keys.clipboard),
             new SimpleInternationalString("Tool to paste geometries from clipboard."),
             ICON_PASTE,FeatureMapLayer.class);
    }

    @Override
    public boolean canHandle(final Object candidate) {
        if(!super.canHandle(candidate)){
            return false;
        }

        //check the geometry type is type Point
        final FeatureMapLayer layer = (FeatureMapLayer) candidate;
        final FeatureType ft = layer.getCollection().getType();
        final AttributeType desc = FeatureExt.getDefaultGeometryAttribute(ft);
        return desc != null;
    }

    @Override
    public EditionDelegate createDelegate(final JMap2D map, final Object candidate) {
        return new GeometryFromClipboardDelegate(map, (FeatureMapLayer) candidate);
    }
}
