/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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
package org.geotoolkit.display2d.canvas.painter;

import java.util.List;
import org.geotoolkit.display2d.canvas.RenderingContext2D;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class BackgroundPainterGroup implements BackgroundPainter{

    private final BackgroundPainter[] painters;

    private BackgroundPainterGroup(BackgroundPainter ... painters){
        this.painters = painters.clone();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void paint(RenderingContext2D context) {
        for(BackgroundPainter painter : painters){
            painter.paint(context);
        }
    }

    public static BackgroundPainterGroup wrap(BackgroundPainter ... painters){
        return new BackgroundPainterGroup(painters);
    }

    public static BackgroundPainterGroup wrap(List<BackgroundPainter> painters){
        return new BackgroundPainterGroup(painters.toArray(new BackgroundPainter[painters.size()]));
    }

    @Override
    public boolean isOpaque() {
        for(BackgroundPainter bgp : painters){
            if(bgp.isOpaque()){
                return true;
            }
        }
        return false;
    }

}
