/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.ext.image;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.ext.PositionedGraphic2D;

import static javax.swing.SwingConstants.*;

/**
 * Graphic decoration to paint an Image.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GraphicImageJ2D extends PositionedGraphic2D{

    private final BufferedImage image;

    public GraphicImageJ2D(final ReferencedCanvas2D canvas, final BufferedImage image){
        this(canvas,image,null);
    }

    public GraphicImageJ2D(final ReferencedCanvas2D canvas, final BufferedImage image, final Dimension resize){
        super(canvas);

        if(resize != null){
            final BufferedImage img = new BufferedImage(resize.width, resize.height, BufferedImage.TYPE_INT_ARGB);
            img.getGraphics().drawImage(image, 0, 0, resize.width, resize.height, null);
            this.image = img;
        }else{
            this.image = image;
        }

    }

    @Override
    protected void paint(final RenderingContext2D context, final int position, final int[] offset) {
        
        if(image == null) return;

        final Graphics2D g = context.getGraphics();
        context.switchToDisplayCRS();

        Rectangle bounds = context.getCanvasDisplayBounds();


        final int imgHeight = image.getHeight();
        final int imgWidth  = image.getWidth();
        int x = 0;
        int y = 0;

        switch(position){
            case NORTH :
                x = (bounds.width - imgWidth) / 2 + offset[0];
                y = offset[1];
                break;
            case NORTH_EAST : 
                x = (bounds.width - imgWidth)  - offset[0];
                y = offset[1];
                break;
            case NORTH_WEST :
                x = offset[0];
                y = offset[1];
                break;
            case SOUTH :
                x = (bounds.width - imgWidth) / 2 + offset[0];
                y = (bounds.height - imgHeight) - offset[1];
                break;
            case SOUTH_EAST :
                x = (bounds.width - imgWidth) - offset[0];
                y = (bounds.height - imgHeight) - offset[1];
                break;
            case SOUTH_WEST :
                x = offset[0];
                y = (bounds.height - imgHeight) - offset[1];
                break;
            case CENTER :
                x = (bounds.width - imgWidth) / 2 + offset[0];
                y = (bounds.height - imgHeight) / 2 + offset[1];
                break;
            case EAST :
                x = (bounds.width - imgWidth) - offset[0];
                y = (bounds.height - imgHeight) / 2 + offset[1];
                break;
            case WEST :
                x = offset[0];
                y = (bounds.height - imgHeight) / 2 + offset[1];
                break;
        }

        g.drawImage(image, x, y, null);
    }

}
