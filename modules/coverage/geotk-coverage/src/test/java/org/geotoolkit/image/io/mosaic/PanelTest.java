/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.image.io.mosaic;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.RenderedImage;
import javax.swing.JPanel;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

/**
 *
 * @author rmarech
 */
public class PanelTest extends JPanel {

    private RenderedImage image;
    private int minx, miny, imgWidth, imgHeight;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setTransform(new AffineTransform2D(1, 0, 0, 1, (this.getWidth()-imgWidth)/2.0-minx, (this.getHeight()-imgHeight)/2.0-miny));
                g2.drawRenderedImage(image, new AffineTransform2D(1, 0, 0, 1, 0,0));
            }

            protected void setImage(RenderedImage image){
                this.image = image;
            }

            protected void setGraphicValues(int minx, int miny, int imgWidth, int imgHeight) {
                this.minx = minx;
                this.miny = miny;
                this.imgWidth = imgWidth;
                this.imgHeight = imgHeight;
            }

}
