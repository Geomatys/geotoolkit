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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.canvas.painter.BackgroundPainter;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.misc.JOptionDialog;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.s52.JS52LookupTablePane;
import org.geotoolkit.gui.swing.style.s52.JS52MarinerPane;
import org.geotoolkit.gui.swing.style.s52.JS52PalettePane;
import org.geotoolkit.gui.swing.style.s52.JS52SymbolPane;
import org.geotoolkit.gui.swing.style.s52.JS52ViewingGroupPane;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.instruction.PatternFill;

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
                final JS52MarinerPane basepane = new JS52MarinerPane(context);
                final JS52ViewingGroupPane viewpane = new JS52ViewingGroupPane(context);
                final JS52PalettePane palettePane = new JS52PalettePane(context);
                final JS52SymbolPane stylePane = new JS52SymbolPane(context);
                final JS52LookupTablePane lookupsPane = new JS52LookupTablePane(context);
                final JTabbedPane tabs = new JTabbedPane();
                tabs.add(MessageBundle.getString("s52.global"),basepane);
                tabs.add(MessageBundle.getString("s52.filter"),viewpane);
                tabs.add(MessageBundle.getString("s52.palettes"), palettePane);
                tabs.add(MessageBundle.getString("s52.symbols"), stylePane);
                tabs.add(MessageBundle.getString("s52.rules"), lookupsPane);


                final int result = JOptionDialog.show(null, tabs, JOptionPane.OK_CANCEL_OPTION);
                if(result == JOptionPane.OK_OPTION){
                    basepane.apply();
                    viewpane.apply();
                    map.getCanvas().getController().repaint();
                    //change the map background
                    //S-52 Annex A part I p.143 (12.2.2)
                    map.getCanvas().setBackgroundPainter(new S52Background());
                }
            }catch(PortrayalException ex){
                ex.printStackTrace();
            }
        }
    }

    private static class S52Background implements BackgroundPainter{

        @Override
        public void paint(RenderingContext2D rc) {
            try{
                final Graphics2D g = rc.getGraphics();
                final S52Context context = S52Utilities.getS52Context(rc.getCanvas());
                final Color color = context.getPalette().getColor("NODTA");
                final Rectangle rect = rc.getCanvasDisplayBounds();
                g.setPaint(color);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
                final PatternFill pf = new PatternFill("NODATA03");
                pf.render(rc, context, context.getPalette(), rect);

            }catch(PortrayalException ex){
                ex.printStackTrace();
            }
        }

        @Override
        public boolean isOpaque() {
            return true;
        }

    }

}
