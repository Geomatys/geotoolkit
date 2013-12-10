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
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.List;
import javax.measure.converter.ConversionException;
import org.apache.sis.storage.DataStoreException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Create a stack of elevation models.
 *
 * It will takes values from the first elevation loader.
 * If it contains NaN values then the next elevation loader is used to fill those NaN.
 * And so on until there are not more NaN or no more elevation loaders.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StackElevationLoader implements ElevationLoader{

    private final List<ElevationLoader> stack;
    private double min;
    private double max;

    public StackElevationLoader(List<ElevationLoader> stack) {
        this.stack = stack;
        min = stack.get(0).getMinimumElevation();
        max = stack.get(0).getMaximumElevation();
        for(int i=1,n=stack.size();i<n;i++){
            min = Math.min(min, stack.get(i).getMinimumElevation());
            max = Math.max(max, stack.get(i).getMaximumElevation());
        }
    }

    @Override
    public double getMinimumElevation() {
        return min;
    }

    @Override
    public double getMaximumElevation() {
        return max;
    }

    @Override
    public void setOutputCRS(CoordinateReferenceSystem outputCrs) throws FactoryException, ConversionException {
        for(ElevationLoader e : stack){
            e.setOutputCRS(outputCrs);
        }
    }

    @Override
    public BufferedImage getBufferedImageOf(Envelope outputEnv, Dimension outputDimension) throws TransformException, FactoryException, ConversionException {
        BufferedImage image = stack.get(0).getBufferedImageOf(outputEnv, outputDimension);

        final boolean[][] mask = new boolean[outputDimension.height][outputDimension.width];
        for(int i=1,n=stack.size();i<n;i++){
            if(hasNaN(image, mask)){
                //get next elevation image and merge them
                final BufferedImage nextimg = stack.get(i).getBufferedImageOf(outputEnv, outputDimension);
                merge(image,nextimg,mask);
            }
        }

        return image;
    }

    @Override
    public double getSmoothValueOf(DirectPosition position, double scale) throws FactoryException, ConversionException, TransformException {
        return stack.get(0).getSmoothValueOf(position, scale);
    }

    @Override
    public double getValueOf(DirectPosition position, double scale) throws DataStoreException, TransformException, IOException, ConversionException, FactoryException {
        return stack.get(0).getValueOf(position, scale);
    }

    private static boolean hasNaN(BufferedImage buffer, boolean[][] mask){
        final WritableRaster raster = buffer.getRaster();
        final double[] sample = new double[raster.getNumBands()];
        boolean hasnan = false;

        for(int y=0;y<mask.length;y++){
            for(int x=0;x<mask[0].length;x++){
                raster.getPixel(x, y, sample);
                mask[y][x] = Double.isNaN(sample[0]);
                hasnan = hasnan || mask[y][x];
            }
        }

        return hasnan;
    }

    private static void merge(BufferedImage base, BufferedImage next, boolean[][] mask){
        final WritableRaster baseRaster = base.getRaster();
        final WritableRaster nextRaster = next.getRaster();
        final double[] sample = new double[nextRaster.getNumBands()];

        for(int y=0;y<mask.length;y++){
            for(int x=0;x<mask[0].length;x++){
                if(mask[y][x]){
                    nextRaster.getPixel(x, y, sample);
                    baseRaster.setPixel(x, y, sample);
                }
            }
        }
    }

}
