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

package org.geotoolkit.wms.map;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import javax.imageio.ImageIO;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.Canvas;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.stateless.StatelessCoverageLayerJ2D;
import org.geotoolkit.display2d.container.stateless.StatelessPyramidalCoverageLayerJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.geotoolkit.wms.GetLegendRequest;
import org.geotoolkit.wms.WMSCoverageResource;
import org.geotoolkit.wms.WebMapClient;

/**
 * Render WMS layer in default geotoolkit rendering engine.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSGraphicBuilder implements GraphicBuilder<GraphicJ2D>{

    /**
     * One instance for all WMS map layers. Object is concurrent.
     */
    public static final WMSGraphicBuilder INSTANCE = new WMSGraphicBuilder();

    protected WMSGraphicBuilder(){};

    @Override
    public Collection<GraphicJ2D> createGraphics(final MapLayer layer, final Canvas canvas) {

        Resource resource = layer.getResource();
        if(!(resource instanceof GridCoverageResource) || !(canvas instanceof J2DCanvas)){
            return Collections.emptyList();
        }

        final GridCoverageResource cr = (GridCoverageResource) resource;
        final GraphicJ2D gra;
        if(cr instanceof PyramidalCoverageResource){
            gra = new StatelessPyramidalCoverageLayerJ2D((J2DCanvas)canvas, layer);
        }else{
            gra = new StatelessCoverageLayerJ2D((J2DCanvas)canvas, layer, true);
        }

        return Collections.<GraphicJ2D>singleton(gra);
    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

    @Override
    public Image getLegend(final MapLayer layer) throws PortrayalException {
        Resource resource = layer.getResource();

        if(!(resource instanceof WMSCoverageResource)){
            return null;
        }

        final WMSCoverageResource reference = (WMSCoverageResource) resource;
        final WebMapClient server = (WebMapClient)reference.getOriginator();

        final GetLegendRequest request = server.createGetLegend();
        request.setLayer(reference.getLayerNames()[0]);

        final BufferedImage buffer;
        try {
            buffer = ImageIO.read(request.getURL());
        } catch (MalformedURLException ex) {
            throw new PortrayalException(ex);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        }

        return buffer;
    }

}
