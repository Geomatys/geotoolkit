/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2024, Geomatys
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
package org.geotoolkit.display2d.painter;

import java.awt.Image;
import java.awt.Shape;
import java.util.stream.Stream;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.Presentation;
import org.apache.sis.map.service.RenderingException;
import org.apache.sis.map.service.Scene2D;
import org.apache.sis.style.Style;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.style.MutableStyle;
import org.opengis.util.FactoryException;

/**
 * Adapter class to use GeotoolKit style in Apache SIS GraphicsPortrayer.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class StylePainter implements org.apache.sis.map.service.StylePainter {

    @Override
    public Class<? extends Style> getStyleClass() {
        return MutableStyle.class;
    }

    @Override
    public void paint(Scene2D scene, MapLayer layer) throws RenderingException {
        final J2DCanvas canvas = new J2DCanvas(scene.grid.getCoordinateReferenceSystem(), new Hints()) {
            @Override
            public boolean repaint(Shape area) {
                final RenderingContext2D context = prepareContext(scene.getGraphics());
                final GraphicContainer container = getContainer();
                boolean dataPainted = false;
                if (container != null){
                    dataPainted |= render(context, container.flatten(true));
                }
                return dataPainted;
            }

            @Override
            public Image getSnapShot() {
                throw new UnsupportedOperationException("Not supported ");
            }
        };
        try {
            canvas.setGridGeometry(scene.grid);
            canvas.repaint();
        } catch (FactoryException ex) {
            throw new RenderingException(ex);
        }
    }

    @Override
    public Stream<Presentation> present(Scene2D sd, MapLayer ml) {
        return new SEPortrayer().present(sd.grid, ml);
    }

    @Override
    public Stream<Presentation> intersects(Scene2D scene, MapLayer layer, Shape mask) {
        return Stream.empty();
    }

}
