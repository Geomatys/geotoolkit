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

package org.geotoolkit.gui.swing.go2.decoration;

import javax.swing.JComponent;
import javax.swing.JPanel;
import org.geotoolkit.gui.swing.BufferLayout;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.lang.Static;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public final class MapDecorationStack extends AbstractMapDecoration implements MapDecoration{

    private final JPanel panel = new JPanel(new BufferLayout());
    private final MapDecoration[] decos;

    private MapDecorationStack(final MapDecoration ... decorations){
        this.decos = decorations.clone();
        panel.setOpaque(false);
        for(MapDecoration deco : decos){
            panel.add(deco.getComponent());
        }
    }

    @Override
    public void refresh() {
        for(MapDecoration deco : decos){
            deco.refresh();
        }
    }

    @Override
    public void dispose() {
        for(MapDecoration deco : decos){
            deco.dispose();
        }
    }

    @Override
    public void setMap2D(final JMap2D map) {
        super.setMap2D(map);
        for(MapDecoration deco : decos){
            deco.setMap2D(map);
        }
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    public static MapDecoration wrap(final MapDecoration ... decorations){
        if(decorations.length == 1){
            return decorations[0];
        }

        return new MapDecorationStack(decorations);
    }

}
