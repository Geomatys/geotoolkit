/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.process.coverage.bandcombiner;

import java.awt.Point;
import java.awt.image.*;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.process.coverage.bandcombiner.CombinerDescriptor.*;
import org.opengis.coverage.Coverage;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class CombinerProcess extends AbstractProcess {

    CombinerProcess(final ParameterValueGroup input) {
    super(INSTANCE, input);
    }

     @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        final Object inputR = Parameters.getOrCreate(IN_RED, inputParameters).getValue();
        final Object inputG = Parameters.getOrCreate(IN_GREEN, inputParameters).getValue();
        final Object inputB = Parameters.getOrCreate(IN_BLUE, inputParameters).getValue();

        if(!(inputR instanceof Coverage) || !(inputG instanceof Coverage) || !(inputB instanceof Coverage)){
            throw new ProcessException("One of the input is not a coverage", this, new IllegalArgumentException());
        }

        Raster rasterInfo = ((Coverage)inputR).getRenderableImage(0, 1).createDefaultRendering().getData();
        //CHECK FOR BAND COMBINER IN JAI
        DataBuffer red   = ((Coverage)inputR).getRenderableImage(0, 1).createDefaultRendering().getData().getDataBuffer();
        DataBuffer green = ((Coverage)inputG).getRenderableImage(0, 1).createDefaultRendering().getData().getDataBuffer();
        DataBuffer blue  = ((Coverage)inputB).getRenderableImage(0, 1).createDefaultRendering().getData().getDataBuffer();
        if(red.getDataType() != DataBuffer.TYPE_BYTE || green.getDataType() != DataBuffer.TYPE_BYTE || blue.getDataType() != DataBuffer.TYPE_BYTE){
            throw new ProcessException("Image format is not supported", this, new IllegalArgumentException());
        }

        byte[][] rgbTable = new byte[3][red.getSize()];
        rgbTable[0] = ((DataBufferByte)red).getData();
        rgbTable[1] = ((DataBufferByte)green).getData();
        rgbTable[2] = ((DataBufferByte)blue).getData();
        DataBuffer buffer = new DataBufferByte(rgbTable, red.getSize());
        Raster rasterRGB = WritableRaster.createBandedRaster(buffer, rasterInfo.getWidth(), rasterInfo.getHeight(), rasterInfo.getWidth(), new int[]{0,1,2}, new int[]{0,0,0}, new Point(0, 0));
        BufferedImage result = new BufferedImage(rasterInfo.getWidth(), rasterInfo.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        result.setData(rasterRGB);

        //create the coverage
        final GridCoverageBuilder GB = new GridCoverageBuilder();
        GB.setRenderedImage(result);
        GB.setEnvelope(((Coverage)inputR).getEnvelope());
        GB.setCoordinateReferenceSystem(((Coverage)inputR).getCoordinateReferenceSystem());
        final GridCoverage2D output = GB.getGridCoverage2D();

        Parameters.getOrCreate(OUT_BAND, outputParameters).setValue(output);

    }
}
