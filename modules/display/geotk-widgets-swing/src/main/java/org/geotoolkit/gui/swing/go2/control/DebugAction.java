/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.decoration.AbstractMapDecoration;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;
import org.jdesktop.jxlayer.JXLayer;
import org.opengis.display.primitive.Graphic;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DebugAction extends AbstractAction {

    private final DebugDecoration deco = new DebugDecoration();
    private Map2D map = null;


    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null ) {
            for(MapDecoration dec : map.getDecorations()){
                if(dec.equals(deco)){
                    map.removeDecoration(deco);
                    return;
                }
            }
            map.addDecoration(deco);
        }
    }

    public Map2D getMap() {
        return map;
    }

    public void setMap(Map2D map) {
        if(map == this.map) return;

        if(this.map != null){
            this.map.removeDecoration(deco);
        }

        this.map = map;
        setEnabled(map != null);
    }

    private static class DebugDecoration extends AbstractMapDecoration{

        private final JPanel pan = new JPanel(new BorderLayout());
        private final JTextArea jta = new JTextArea();

        public DebugDecoration(){
            pan.setOpaque(false);
            pan.setFocusable(false);
            JScrollPane pane = new JScrollPane(jta);
            pane.setPreferredSize(new Dimension(320, 200));
            pan.add(BorderLayout.EAST,pane);
        }

        @Override
        public void refresh() {
        }

        @Override
        public void setMap2D(Map2D map) {
            super.setMap2D(map);

            if(map != null){
                map.getCanvas().getContainer().add(new DebugGraphic(map.getCanvas(), this));
            }

        }

        @Override
        public JComponent geComponent() {
            return pan;
        }

    }

    public static class DebugGraphic extends AbstractGraphicJ2D{

        private DebugDecoration deco;

        public DebugGraphic(ReferencedCanvas2D canvas, DebugDecoration deco){
            super(canvas,canvas.getObjectiveCRS2D());
            this.deco = deco;
        }

        @Override
        public void paint(RenderingContext2D context) {
            deco.jta.setText(context.toString());
        }

        @Override
        public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
            return graphics;
        }

    }


}
