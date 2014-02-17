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

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.ext.legend.DefaultLegendService;
import org.geotoolkit.display2d.ext.legend.LegendTemplate;
import org.geotoolkit.display2d.primitive.GraphicProbe;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.map.MapContext;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.container.GraphicContainer;

/**
 * Legend decoration placed on the right of the map pane.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class LegendDecoration extends AbstractMapDecoration implements GraphicProbe.ProbeMonitor {
            
    private static final Logger LOGGER = Logging.getLogger(LegendDecoration.class);
    
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel lbl = new JLabel();
    private final LegendTemplate template;

    public LegendDecoration(LegendTemplate template) {
        this.template = template;
        panel.setOpaque(false);

        lbl.setOpaque(false);
        lbl.setBorder(null);        
    }

    @Override
    public void refresh() {
    }

    @Override
    public void setMap2D(final JMap2D map) {
        super.setMap2D(map);

        if (map != null) {
            final GraphicProbe gp = new GraphicProbe(map.getCanvas(), this);
            map.getCanvas().getContainer().getRoot().getChildren().add(gp);
            update(map.getContainer().getContext());
        }

    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    private void update(MapContext context){
        try {
            final BufferedImage img = DefaultLegendService.portray(template, context, null);
            lbl.setIcon(new ImageIcon(img));

            panel.removeAll();
            final JScrollPane scroll = new JScrollPane(lbl);
            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setViewportBorder(null);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            panel.add(BorderLayout.EAST, scroll);
            panel.revalidate();
            panel.repaint();

        } catch (PortrayalException ex) {
            LOGGER.log(Level.INFO, ex.getMessage(),ex);
        }
    }
    
    @Override
    public void contextPaint(final RenderingContext2D context) {

        final GraphicContainer container = context.getCanvas().getContainer();
        if (container instanceof ContextContainer2D) {
            final ContextContainer2D cc = (ContextContainer2D) container;
            update(cc.getContext());
        }
    }
}
