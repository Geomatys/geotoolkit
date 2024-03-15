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

package org.geotoolkit.display2d.ext.legend;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.apache.sis.map.MapItem;
import org.apache.sis.map.MapLayers;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.OutputDef;

/**
 * Render a complete legend of a mapcontext.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultLegendService {

    private DefaultLegendService(){}

    /**
     * Portray a context's legend.
     *
     * @param template : legend template
     * @param context : map context
     * @param dim : legend wished size or null
     * @return buffered image
     * @throws PortrayalException
     */
    public static BufferedImage portray(final LegendTemplate template, final MapItem context, Dimension dim)
            throws PortrayalException{
        if(dim == null){
            dim = legendPreferredSize(template, context);
        }

        final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = img.createGraphics();
        J2DLegendUtilities.paintLegend(context, g, new Rectangle(dim), template);
        g.dispose();
        return img;
    }

    /**
     * Portray a context's legend.
     *
     * @param template : legend template
     * @param context : map context
     * @param dim : legend wished size or null
     * @param outputDef : writing definition
     * @return buffered image
     * @throws PortrayalException
     */
    public static void portray(final LegendTemplate template, final MapLayers context, final Dimension dim, final OutputDef outputDef)
            throws PortrayalException{
        final BufferedImage image = portray(template,context,dim);

        if(image == null){
            throw new PortrayalException("No image created by the canvas.");
        }

        try {
            DefaultPortrayalService.writeImage(image, outputDef);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        }
    }

    /**
     * Get the most appropriate legend size.
     *
     * @param template : legend template
     * @param mapitem : map context
     * @return Dimension : legend preferred size
     */
    public static Dimension legendPreferredSize(final LegendTemplate template, final MapItem mapitem){
        final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return J2DLegendUtilities.estimate(img.createGraphics(), mapitem, template, true);
    }

}
