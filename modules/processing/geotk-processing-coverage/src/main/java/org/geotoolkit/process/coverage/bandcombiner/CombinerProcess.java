/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.process.coverage.bandcombiner;

import java.awt.Graphics2D;
import java.awt.image.*;
import javax.media.jai.JAI;
import javax.media.jai.RasterFactory;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.process.coverage.bandcombiner.CombinerDescriptor.*;
import org.opengis.coverage.Coverage;
import sun.awt.image.ByteBandedRaster;
import sun.awt.image.ByteComponentRaster;

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
        
        final Object inputR = Parameters.getOrCreate(IN_RED, inputParameters);
        final Object inputG = Parameters.getOrCreate(IN_GREEN, inputParameters);
        final Object inputB = Parameters.getOrCreate(IN_BLUE, inputParameters);
        
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
        Raster rasterRGB = new ByteBandedRaster(null, buffer, null);
        BufferedImage result = new BufferedImage(rasterInfo.getWidth(), rasterInfo.getHeight(), 0);
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
