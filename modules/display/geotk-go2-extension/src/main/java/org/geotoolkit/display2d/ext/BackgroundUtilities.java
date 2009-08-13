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

package org.geotoolkit.display2d.ext;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BackgroundUtilities {

    private BackgroundUtilities() {
    }

    public static void paint(Graphics2D g, Rectangle area, BackgroundTemplate template){

        int correction = 0;

        final Stroke stroke = template.getBackgroundStroke();
        if(stroke instanceof BasicStroke){
            final BasicStroke strk = (BasicStroke) stroke;
            correction = (int) (strk.getLineWidth() / 2);
        }


        //the fill--------------------------------------------------------------
        Paint paint = template.getBackgroundPaint();
        final int round = template.getRoundBorder();

        if(paint != null){
            g.setPaint(paint);
            g.fillRoundRect(area.x +correction, area.y +correction,
                    area.width - 2*correction -1,
                    area.height - 2*correction -1,
                    round, round);
        }

        //the border------------------------------------------------------------
        
        paint = template.getBackgroundStrokePaint();

        if(stroke != null && paint != null){

            g.setStroke(stroke);
            g.setPaint(paint);
            g.drawRoundRect(area.x +correction, area.y +correction,
                    area.width - 2*correction -1,
                    area.height - 2*correction -1,
                    round, round);
        }

    }
}
