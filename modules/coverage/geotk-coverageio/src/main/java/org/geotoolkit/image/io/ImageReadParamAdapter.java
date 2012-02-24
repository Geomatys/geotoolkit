/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.image.io;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.IIOParamController;
import org.geotoolkit.lang.Decorator;


/**
 * The parameters for {@link SpatialImageReadParam}. Every call to methods in this class
 * delegates to the wrapped parameters.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
@Decorator(ImageReadParam.class)
final class ImageReadParamAdapter extends SpatialImageReadParam {
    /**
     * The wrapped parameters.
     */
    final ImageReadParam param;

    /**
     * Creates new parameters.
     *
     * @param adapter The {@link ImageReaderAdapter} instance which is creating this parameter.
     * @param param   The parameters created by the wrapped reader.
     */
    ImageReadParamAdapter(final ImageReader adapter, final ImageReadParam param) {
        super(adapter);
        this.param = param;
        destinationOffset = null; // Useless since we will use the point defined in the wrapped param.
    }

    /*
     * The first methods up to the ones related to the controller are inherited from IIOParam.
     * Those methods are copied verbatism in ImageWriteParamAdapter (we can not factor them out
     * in a super-class).
     */
    @Override public void setSourceRegion(Rectangle sourceRegion)              {param.setSourceRegion(sourceRegion);}
    @Override public void setSourceSubsampling(int sx, int sy, int ox, int oy) {param.setSourceSubsampling(sx, sy, ox, oy);}
    @Override public void setSourceBands(int[] sourceBands)                    {param.setSourceBands(sourceBands);}
    @Override public void setDestinationType(ImageTypeSpecifier type)          {param.setDestinationType(type);}
    @Override public void setDestinationOffset(Point destinationOffset)        {param.setDestinationOffset(destinationOffset);}
    @Override public void setController(IIOParamController controller)         {param.setController(controller);}
    @Override public void setDestination(BufferedImage destination)            {param.setDestination(destination);}
    @Override public void setDestinationBands(int[] destinationBands)          {param.setDestinationBands(destinationBands);}
    @Override public void setSourceRenderSize(Dimension size)                  {param.setSourceRenderSize(size);}
    @Override public void setSourceProgressivePasses(int min, int num)         {param.setSourceProgressivePasses(min, num);}

    @Override public Rectangle          getSourceRegion()               {return param.getSourceRegion();}
    @Override public int                getSourceXSubsampling()         {return param.getSourceXSubsampling();}
    @Override public int                getSourceYSubsampling()         {return param.getSourceYSubsampling();}
    @Override public int                getSubsamplingXOffset()         {return param.getSubsamplingXOffset();}
    @Override public int                getSubsamplingYOffset()         {return param.getSubsamplingYOffset();}
    @Override public int[]              getSourceBands()                {return param.getSourceBands();}
    @Override public ImageTypeSpecifier getDestinationType()            {return param.getDestinationType();}
    @Override public Point              getDestinationOffset()          {return param.getDestinationOffset();}
    @Override public IIOParamController getController()                 {return param.getController();}
    @Override public IIOParamController getDefaultController()          {return param.getDefaultController();}
    @Override public boolean            hasController()                 {return param.hasController();}
    @Override public boolean            activateController()            {return param.activateController();}
    @Override public BufferedImage      getDestination()                {return param.getDestination();}
    @Override public int[]              getDestinationBands()           {return param.getDestinationBands();}
    @Override public boolean            canSetSourceRenderSize()        {return param.canSetSourceRenderSize();}
    @Override public Dimension          getSourceRenderSize()           {return param.getSourceRenderSize();}
    @Override public int                getSourceMinProgressivePass()   {return param.getSourceMinProgressivePass();}
    @Override public int                getSourceMaxProgressivePass()   {return param.getSourceMaxProgressivePass();}
    @Override public int                getSourceNumProgressivePasses() {return param.getSourceNumProgressivePasses();}
}
