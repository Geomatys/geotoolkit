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

import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.symbolizer.S52Graphic;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class RESTRN01 extends Procedure{

    public RESTRN01() {
        super("RESTRN01");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {

        final String[] values = (String[]) ((graphic.feature.getProperty("RESTRN")==null) ? null : graphic.feature.getProperty("RESTRN").getValue());

        if(values != null){
            final RESCSP02 proc = (RESCSP02) context.getProcedure("RESCSP02");
            proc.render(ctx, context, colorTable, all, graphic, values);
        }

    }

}
