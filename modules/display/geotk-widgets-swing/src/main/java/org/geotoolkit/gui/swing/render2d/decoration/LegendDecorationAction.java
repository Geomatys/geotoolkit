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
package org.geotoolkit.gui.swing.render2d.decoration;

import java.awt.event.ActionEvent;
import org.geotoolkit.display2d.ext.legend.LegendTemplate;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 * Action that display a scroll pan with the legend.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LegendDecorationAction extends AbstractMapAction {

    private final LegendDecoration deco;

    public LegendDecorationAction(final JMap2D map, final LegendTemplate template) {
        super(MessageBundle.format("legend"), null,map);
        deco = new LegendDecoration(template);
        putValue(SHORT_DESCRIPTION, MessageBundle.format("legend"));
        setMap(map);
    }

    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (map != null) {
            for (MapDecoration dec : map.getDecorations()) {
                if (dec.equals(deco)) {
                    map.removeDecoration(deco);
                    return;
                }
            }
            map.addDecoration(deco);
        }
    }

    @Override
    public void setMap(final JMap2D map) {
        if (map == this.map) {
            return;
        }
        if (this.map != null) {
            this.map.removeDecoration(deco);
        }
        super.setMap(map);
    }

}
