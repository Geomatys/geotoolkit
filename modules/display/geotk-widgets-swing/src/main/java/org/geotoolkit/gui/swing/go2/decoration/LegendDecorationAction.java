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
package org.geotoolkit.gui.swing.go2.decoration;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingConstants;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.ext.legend.DefaultLegendService;
import org.geotoolkit.display2d.ext.legend.LegendTemplate;
import org.geotoolkit.display2d.primitive.GraphicProbe;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.AbstractMapAction;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapContext;

/**
 * Action that display a scroll pan with the legend.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LegendDecorationAction extends AbstractMapAction {

    private final LegendDecoration deco = new LegendDecoration();
    private final LegendTemplate template;

    public LegendDecorationAction(final JMap2D map, final LegendTemplate template) {
        super(MessageBundle.getString("legend"), null,map);
        this.template = template;
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("legend"));
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

    private class LegendDecoration extends AbstractMapDecoration implements GraphicProbe.ProbeMonitor {

        private final JPanel pan = new JPanel(new BorderLayout());
        private final JLabel lbl = new JLabel();
        private final JScrollPane scroll = new JScrollPane(lbl);

        public LegendDecoration() {
            lbl.setOpaque(false);
            lbl.setBorder(null);
            
            pan.setOpaque(false);
            pan.setFocusable(false);
            scroll.setLayout(new ScrollPaneLayout() {
                @Override
                public void layoutContainer(Container parent) {
                    JScrollPane scrollPane = (JScrollPane) parent;
                    scrollPane.setComponentOrientation(
                    ComponentOrientation.LEFT_TO_RIGHT);
                    super.layoutContainer(parent);
                    scrollPane.setComponentOrientation(
                    ComponentOrientation.RIGHT_TO_LEFT);
                }
            });
            scroll.setBorder(null);            
            scroll.getViewport().setOpaque(false);
            scroll.setViewportBorder(null);
            scroll.setOpaque(false);
            scroll.setPreferredSize(new Dimension(320, 200));
            pan.add(BorderLayout.EAST, scroll);
        }

        @Override
        public void refresh() {
        }

        @Override
        public void setMap2D(final JMap2D map) {
            super.setMap2D(map);

            if (map != null) {
                final GraphicProbe gp = new GraphicProbe(map.getCanvas(), this);
                gp.setZOrderHint(Double.MIN_VALUE);
                map.getCanvas().getContainer().add(gp);
            }

        }

        @Override
        public JComponent getComponent() {
            return pan;
        }

        @Override
        public void contextPaint(final RenderingContext2D context) {
            
            final AbstractContainer2D container = context.getCanvas().getContainer();
            if(!(container instanceof ContextContainer2D)) return;

            final ContextContainer2D cc = (ContextContainer2D) container;
            final MapContext mapContext = cc.getContext();

            try {
                final BufferedImage img = DefaultLegendService.portray(template, mapContext, null);
                lbl.setIcon(new ImageIcon(img));
//                scroll.setLayout(new ScrollPaneLayout() {
//                @Override
//                public void layoutContainer(Container parent) {
//                    JScrollPane scrollPane = (JScrollPane) parent;
//                    scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
//                    super.layoutContainer(parent);
//                    scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//                    }
//                });
                scroll.setPreferredSize(new Dimension(img.getWidth()+25, 200));
                scroll.setSize(new Dimension(img.getWidth()+25, 200));
                scroll.revalidate();
                scroll.repaint();
                pan.revalidate();
                pan.repaint();
            } catch (PortrayalException ex) {
                context.getMonitor().exceptionOccured(ex, Level.WARNING);
            }
        }

    }
}
