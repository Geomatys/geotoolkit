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
package org.geotoolkit.display3d.scene.loader;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.measure.converter.ConversionException;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.map.MapContext;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapContextImageLoader implements ImageLoader {

    private final MapContext context;
    private CoordinateReferenceSystem outputCRS = null;

    public MapContextImageLoader(MapContext context) {
        this.context = context;
    }

    @Override
    public void setOutputCRS(CoordinateReferenceSystem outputCrs) throws FactoryException, ConversionException {
        this.outputCRS = outputCrs;
    }

    @Override
    public BufferedImage getBufferedImageOf(Envelope outputEnv, Dimension outputDimension)
            throws TransformException, FactoryException, ConversionException {

        final CanvasDef cdef = new CanvasDef(outputDimension, null);
        final ViewDef vdef = new ViewDef(outputEnv);
        final SceneDef sdef = new SceneDef(context);

        try {
            return DefaultPortrayalService.portray(cdef, sdef, vdef);
        } catch (PortrayalException ex) {
            throw new TransformException(ex.getMessage(), ex);
        }

    }

}
