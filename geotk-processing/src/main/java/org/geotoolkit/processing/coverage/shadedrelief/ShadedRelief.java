/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.processing.coverage.shadedrelief;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.geometries.math.Maths;
import org.apache.sis.geometries.math.Vector3D;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ShadedRelief extends AbstractProcess {

    private static CoordinateReferenceSystem MERCATOR;
    static {
        try {
            MERCATOR = CRS.forCode("EPSG:3395");
        } catch (FactoryException ex) {
            Logger.getLogger("org.geotoolkit.processing.coverage.shadedrelief").log(Level.SEVERE, null, ex);
        }
    }

    public ShadedRelief(GridCoverage coverage, GridCoverage elevation, MathTransform1D eleConv){
        this(ShadedReliefDescriptor.INSTANCE,toParameters(coverage, elevation, eleConv));
    }

    public ShadedRelief(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    private static final ParameterValueGroup toParameters(GridCoverage coverage, GridCoverage elevation, MathTransform1D eleConv){
        final Parameters params = Parameters.castOrWrap(ShadedReliefDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(ShadedReliefDescriptor.COVERAGE).setValue(coverage);
        params.getOrCreate(ShadedReliefDescriptor.ELEVATION).setValue(elevation);
        params.getOrCreate(ShadedReliefDescriptor.ELECONV).setValue(eleConv);
        return params;
    }

    @Override
    protected void execute() throws ProcessException {
        GridCoverage coverage = inputParameters.getValue(ShadedReliefDescriptor.COVERAGE);
        GridCoverage elevation = inputParameters.getValue(ShadedReliefDescriptor.ELEVATION);
        MathTransform1D eleConv = inputParameters.getValue(ShadedReliefDescriptor.ELECONV);
        //prepare coverage for the expected work
        coverage = coverage.forConvertedValues(false);
        elevation = elevation.forConvertedValues(true);

        //light informations
        final Vector3D.Float lightDirection = new Vector3D.Float(1, 1, 1);
        final Vector3D.Float fragToEye = new Vector3D.Float(0, 0, 1);
        lightDirection.normalize();

        final RenderedImage baseImage = coverage.render(null);
        final ColorModel cm = baseImage.getColorModel();
        final Raster baseRaster = getData(baseImage);

        final Raster eleImage = getData(elevation.render(null));
        final int width = baseImage.getWidth();
        final int height = baseImage.getHeight();
        final BufferedImage resImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        //list all coordinates
        //we need 1 extract point for the last row and col triangles
        final float[] coords = new float[(width+1)*(height+1)*2];
        int k=-1;
        for(int y=0;y<height+1;y++){
            for(int x=0;x<width+1;x++){
                coords[++k]=x;
                coords[++k]=y;
            }
        }

        //we convert everything to meters
        final MathTransform gridToData = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
        final MathTransform dataToMercator;
        try {
            dataToMercator = CRS.findOperation(coverage.getCoordinateReferenceSystem(), MERCATOR, null).getMathTransform();
            final MathTransform gridToMercator = MathTransforms.concatenate(gridToData, dataToMercator);
            gridToMercator.transform(coords, 0, coords, 0, coords.length/2);


            //loop on each pixel, create 2 triangles and calculate shaded color
            final int lineLength = (width+1)*2;
            final float[] fa = new float[3];
            final float[] fb = new float[3];
            final float[] fc = new float[3];
            final float[] fd = new float[3];
            final Vector3D.Float v1 = new Vector3D.Float();
            final Vector3D.Float v2 = new Vector3D.Float();
            final Vector3D.Float v3 = new Vector3D.Float();
            final Vector3D.Float n1 = new Vector3D.Float();
            final Vector3D.Float n2 = new Vector3D.Float();
            final Vector3D.Float n = new Vector3D.Float();
            for(int y=0;y<height;y++){
                for(int x=0;x<width;x++){
                    //get 4 corner coordinates
                    int offset1 = lineLength*y     + x*2;
                    int offset2 = lineLength*(y+1) + x*2;
                    int ex = (x== width-1) ? x : x+1;
                    int ey = (y==height-1) ? y : y+1;
                    fa[0]=coords[offset1+0]; fa[1]=coords[offset1+1]; fa[2]=(float)eleConv.transform(eleImage.getSampleFloat(x,  y,  0));
                    fb[0]=coords[offset1+2]; fb[1]=coords[offset1+3]; fb[2]=(float)eleConv.transform(eleImage.getSampleFloat(ex, y,  0));
                    fc[0]=coords[offset2+0]; fc[1]=coords[offset2+1]; fc[2]=(float)eleConv.transform(eleImage.getSampleFloat(x,  ey, 0));
                    fd[0]=coords[offset2+2]; fd[1]=coords[offset2+3]; fd[2]=(float)eleConv.transform(eleImage.getSampleFloat(ex, ey, 0));

                    boolean flipx = (fa[0] > fb[0]);
                    boolean flipy = (fa[1] < fc[1]);
                    boolean invert = (flipx || flipy) && !(flipx && flipy);

                    //calculate average normal of the triangles
                    v1.x = fa[0]; v1.y = fa[1]; v1.z = fa[2];
                    v2.x = fb[0]; v2.y = fb[1]; v2.z = fb[2];
                    v3.x = fc[0]; v3.y = fc[1]; v3.z = fc[2];

                    n1.set(Maths.calculateNormal(v1, v3, v2));

                    v1.x = fb[0]; v1.y = fb[1]; v1.z = fb[2];
                    v2.x = fc[0]; v2.y = fc[1]; v2.z = fc[2];
                    v3.x = fd[0]; v3.y = fd[1]; v3.z = fd[2];

                    n2.set(Maths.calculateNormal(v1, v2, v3));
                    n.set(n1);
                    n.add(n2);
                    n.normalize();

                    if(invert){
                        n.scale(-1f);
                    }

                    int argb = cm.getRGB(baseRaster.getDataElements(x, y, null));
                    float cr = (float)((argb>>16) & 0xFF) / 255f;
                    float cg = (float)((argb>>8) & 0xFF) / 255f;
                    float cb = (float)((argb>>0) & 0xFF) / 255f;
                    float ca = (float)((argb>>24) & 0xFF) / 255f;
                    float ratio = 1f;

                    //if we have an NaN in the normal we skip shading for this cell
                    //the elevation model has a hole in the grid
                    if(!Float.isNaN(n.x) && !Float.isNaN(n.y) && !Float.isNaN(n.z)){
                        //calculate shaded color
                        ratio = (float) Math.max(lightDirection.dot(n),0.0f);
                        //next line is to indensify average colors, lights darken flat areas so we compensate a little
                        ratio = ratio + (float) (Math.sin(ratio*Math.PI)*0.20);
                    }

                    argb = toARGB(cr*ratio, cg*ratio, cb*ratio, ca);

//                    float r = XMath.clamp( (fa[2] * 0.0001f), 0f, 1f);
//                    argb = toARGB(r, r, r, 1f);

                    resImage.setRGB(x, y, argb);

                }
            }
        } catch (FactoryException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        } catch (TransformException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(coverage.getGridGeometry());
        gcb.setValues(resImage);
        final GridCoverage result = gcb.build();
        outputParameters.getOrCreate(ShadedReliefDescriptor.OUTCOVERAGE).setValue(result);
    }

    private static int toARGB(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b ;
    }

    private static int toARGB(float r, float g, float b, float a) {
        return toARGB((int)(a*255), (int)(r*255), (int)(g*255), (int)(b*255));
    }

    private static Raster getData(RenderedImage ri){
        if(ri instanceof BufferedImage){
            return ((BufferedImage)ri).getRaster();
        }

        if(ri.getNumXTiles()==1 && ri.getNumYTiles()==1){
            return ri.getTile(0, 0);
        }

        return ri.getData();
    }

}
