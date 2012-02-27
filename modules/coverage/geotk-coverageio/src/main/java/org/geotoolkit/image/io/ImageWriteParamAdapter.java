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
import java.util.Locale;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.IIOParamController;
import org.geotoolkit.lang.Decorator;


/**
 * The parameters for {@link SpatialImageWriteParam}. Every call to methods in this class
 * delegates to the wrapped parameters.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
@Decorator(ImageWriteParam.class)
final class ImageWriteParamAdapter extends SpatialImageWriteParam {
    /**
     * The wrapped parameters.
     */
    final ImageWriteParam param;

    /**
     * Creates new parameters.
     *
     * @param adapter The {@link ImageWriterAdapter} instance which is creating this parameter.
     * @param param   The parameters created by the wrapped writer.
     */
    ImageWriteParamAdapter(final ImageWriter adapter, final ImageWriteParam param) {
        super(adapter);
        this.param = param;
        destinationOffset = null; // Useless since we will use the point defined in the wrapped param.
    }

    /*
     * The first methods up to the ones related to the controller are inherited from IIOParam.
     * Those methods are copied verbatism in ImageReadParamAdapter (we can not factor them out
     * in a super-class).
     */
    @Override public void setSourceRegion(Rectangle sourceRegion)              {param.setSourceRegion(sourceRegion);}
    @Override public void setSourceSubsampling(int sx, int sy, int ox, int oy) {param.setSourceSubsampling(sx, sy, ox, oy);}
    @Override public void setSourceBands(int[] sourceBands)                    {param.setSourceBands(sourceBands);}
    @Override public void setDestinationType(ImageTypeSpecifier type)          {param.setDestinationType(type);}
    @Override public void setDestinationOffset(Point destinationOffset)        {param.setDestinationOffset(destinationOffset);}
    @Override public void setController(IIOParamController controller)         {param.setController(controller);}
    @Override public void setTilingMode(int mode)                              {param.setTilingMode(mode);}
    @Override public void setTiling(int dx, int dy, int ox, int oy)            {param.setTiling(dx, dy, ox, oy);}
    @Override public void unsetTiling()                                        {param.unsetTiling();}
    @Override public void setProgressiveMode(int mode)                         {param.setProgressiveMode(mode);}
    @Override public void setCompressionMode(int mode)                         {param.setCompressionMode(mode);}
    @Override public void setCompressionType(String type)                      {param.setCompressionType(type);}
    @Override public void unsetCompression()                                   {param.unsetCompression();}
    @Override public void setCompressionQuality(float quality)                 {param.setCompressionQuality(quality);}

    @Override public Rectangle          getSourceRegion()         {return param.getSourceRegion();}
    @Override public int                getSourceXSubsampling()   {return param.getSourceXSubsampling();}
    @Override public int                getSourceYSubsampling()   {return param.getSourceYSubsampling();}
    @Override public int                getSubsamplingXOffset()   {return param.getSubsamplingXOffset();}
    @Override public int                getSubsamplingYOffset()   {return param.getSubsamplingYOffset();}
    @Override public int[]              getSourceBands()          {return param.getSourceBands();}
    @Override public ImageTypeSpecifier getDestinationType()      {return param.getDestinationType();}
    @Override public Point              getDestinationOffset()    {return param.getDestinationOffset();}
    @Override public IIOParamController getController()           {return param.getController();}
    @Override public IIOParamController getDefaultController()    {return param.getDefaultController();}
    @Override public boolean            hasController()           {return param.hasController();}
    @Override public boolean            activateController()      {return param.activateController();}
    @Override public Locale             getLocale()               {return param.getLocale();}
    @Override public boolean            canWriteTiles()           {return param.canWriteTiles();}
    @Override public boolean            canOffsetTiles()          {return param.canOffsetTiles();}
    @Override public int                getTilingMode()           {return param.getTilingMode();}
    @Override public Dimension[]        getPreferredTileSizes()   {return param.getPreferredTileSizes();}
    @Override public int                getTileWidth()            {return param.getTileWidth();}
    @Override public int                getTileHeight()           {return param.getTileHeight();}
    @Override public int                getTileGridXOffset()      {return param.getTileGridXOffset();}
    @Override public int                getTileGridYOffset()      {return param.getTileGridYOffset();}
    @Override public boolean            canWriteProgressive()     {return param.canWriteProgressive();}
    @Override public int                getProgressiveMode()      {return param.getProgressiveMode();}
    @Override public boolean            canWriteCompressed()      {return param.canWriteCompressed();}
    @Override public int                getCompressionMode()      {return param.getCompressionMode();}
    @Override public String[]           getCompressionTypes()     {return param.getCompressionTypes();}
    @Override public String             getCompressionType()      {return param.getCompressionType();}
    @Override public String getLocalizedCompressionTypeName()     {return param.getLocalizedCompressionTypeName();}
    @Override public boolean            isCompressionLossless()   {return param.isCompressionLossless();}
    @Override public float              getCompressionQuality()   {return param.getCompressionQuality();}
    @Override public float              getBitRate(float quality) {return param.getBitRate(quality);}
    @Override public String[] getCompressionQualityDescriptions() {return param.getCompressionQualityDescriptions();}
    @Override public float[]  getCompressionQualityValues()       {return param.getCompressionQualityValues();}
}
