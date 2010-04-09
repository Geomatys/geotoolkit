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

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicProbe;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.decoration.AbstractMapDecoration;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;
import org.geotoolkit.util.StringUtilities;

/**
 * Action that display a Text area on the right side on the map.
 * A graphic probe is added in the map canvas container an collected informations
 * are displayed in the text area.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DebugAction extends AbstractAction {

    private final DebugDecoration deco = new DebugDecoration();
    private Map2D map = null;

    @Override
    public void actionPerformed(ActionEvent arg0) {
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

    public Map2D getMap() {
        return map;
    }

    public void setMap(Map2D map) {
        if (map == this.map) {
            return;
        }

        if (this.map != null) {
            this.map.removeDecoration(deco);
        }

        this.map = map;
        setEnabled(map != null);
    }

    private static class DebugDecoration extends AbstractMapDecoration implements GraphicProbe.ProbeMonitor {

        private final JPanel pan = new JPanel(new BorderLayout());
        private final JEditorPane jta = new JEditorPane();

        public DebugDecoration() {
            pan.setOpaque(false);
            pan.setFocusable(false);
            jta.setEditorKit(new HTMLEditorKit());
            JScrollPane pane = new JScrollPane(jta);
            pane.setPreferredSize(new Dimension(320, 200));
            pan.add(BorderLayout.EAST, pane);
        }

        @Override
        public void refresh() {
        }

        @Override
        public void setMap2D(Map2D map) {
            super.setMap2D(map);

            if (map != null) {
                final GraphicProbe gp = new GraphicProbe(map.getCanvas(), this);
                gp.setZOrderHint(Double.MIN_VALUE);
                map.getCanvas().getContainer().add(gp);
            }

        }

        @Override
        public JComponent geComponent() {
            return pan;
        }

        @Override
        public void contextPaint(RenderingContext2D context) {
            final StringBuilder sb = new StringBuilder("<html><body bgcolor=\"white\"><code>");
            toHTML(context.toString(),sb);
            sb.append("</code></body></html>");
            jta.setText(sb.toString());
        }

        private static void toHTML(String text, StringBuilder sb){
            String[] parts = text.split("\n");
            for(String str : parts){

                int[] cnt = StringUtilities.getIndexes(str, '=');

                if(cnt.length == 1){
                    sb.append("<font color=\"blue\">");
                    sb.append(str.substring(0, cnt[0]+1));
                    sb.append("</font>");
                    sb.append(str.substring(cnt[0]+1));
                }else{
                    sb.append(str);
                }
                sb.append("<br/>");
            }
        }

    }
}
