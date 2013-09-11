/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.procedure;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * S-52 Annex A Part I p.207 (12.2.22a)
 *
 * @author Johann Sorel (Geomatys)
 */
public class SOUNDG02 extends Procedure{

    private static final GeometryFactory GF = new GeometryFactory();

    public SOUNDG02() {
        super("SOUNDG02");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {

        MathTransform dataToDisplay;
        try {
            dataToDisplay = CRS.findMathTransform(
                    graphic.feature.getDefaultGeometryProperty().getType().getCoordinateReferenceSystem(), ctx.getDisplayCRS());

            final SNDFRM03 sndfrm03 = (SNDFRM03) context.getProcedure("SNDFRM03");

            final Geometry geom = (Geometry)graphic.feature.getDefaultGeometryProperty().getValue();
            final Coordinate[] coords = geom.getCoordinates();
            for(Coordinate c : coords){
                final Symbol[] ss = sndfrm03.render(ctx, context, colorTable, all, graphic, c.z);
                if(ss==null || ss.length==0) continue;

                Geometry pt = GF.createPoint(c);
                pt = JTS.transform(pt, dataToDisplay);
                for(Symbol s : ss){
                    s.renderGeometry(ctx, context, colorTable, pt, 0f);
                }
            }
        } catch (FactoryException | TransformException ex) {
            throw new PortrayalException(ex);
        }

    }

}
