/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.edition;

import com.vividsolutions.jts.geom.Coordinate;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.javafx.render2d.AbstractNavigationHandler;
import org.geotoolkit.gui.javafx.render2d.shape.FXGeometryLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractEditionTool extends AbstractNavigationHandler implements EditionTool{

    protected static final int CROSS_SIZE = 5;
    private final Spi spi;

    public AbstractEditionTool(Spi spi) {
        ArgumentChecks.ensureNonNull("spi", spi);
        this.spi = spi;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Spi getSpi() {
        return spi;
    }


    protected static final class EditionLayer extends FXGeometryLayer{

        @Override
        protected Node createVerticeNode(Coordinate c, boolean selected){
            final Line h = new Line(c.x-CROSS_SIZE, c.y, c.x+CROSS_SIZE, c.y);
            final Line v = new Line(c.x, c.y-CROSS_SIZE, c.x, c.y+CROSS_SIZE);
            h.setStroke(Color.RED);
            v.setStroke(Color.RED);

            if(selected){
                h.setStrokeWidth(3);
                v.setStrokeWidth(3);
            }
            return new Group(h,v);
        }
    };

}
