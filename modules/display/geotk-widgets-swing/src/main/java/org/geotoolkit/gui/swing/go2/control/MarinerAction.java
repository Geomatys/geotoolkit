/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.go2.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.painter.BackgroundPainter;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.misc.JOptionDialog;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.style.s52.JS52MarinerPane;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Utilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MarinerAction extends AbstractMapAction {

    public MarinerAction() {
        this(null);
    }

    public MarinerAction(final JMap2D map) {
        super(map);
        putValue(SMALL_ICON, IconBuilder.createIcon(FontAwesomeIcons.ICON_ANCHOR, 16, Color.BLACK));
        putValue(NAME, "S-52");
        putValue(SHORT_DESCRIPTION, "S-52");
    }

    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (map != null ) {
            try{
                final S52Context context = S52Utilities.getS52Context(map.getCanvas());
                final JS52MarinerPane pane = new JS52MarinerPane(context);
                final int result = JOptionDialog.show(null, pane, JOptionPane.OK_CANCEL_OPTION);
                if(result == JOptionPane.OK_OPTION){
                    pane.apply();
                    map.getCanvas().getController().repaint();
                    //change the map background
                    final Color color = context.getPalette().getColor("NODTA");
                    if(color!=null){
                        final BackgroundPainter deco = new SolidColorPainter(color);
                        map.getCanvas().setBackgroundPainter(deco);
                    }
                }
            }catch(PortrayalException ex){
                ex.printStackTrace();
            }
        }
    }

}
