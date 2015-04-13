/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.coverage.kriging;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3d;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;


import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.process.coverage.kriging.KrigingDescriptor.*;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;

/**
 *
 * @author Johann Sorel (geomatys)
 * @module pending
 */
public class KrigingProcess extends AbstractProcess {

    KrigingProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    @Override
    protected void execute() throws ProcessException{
        final CoordinateReferenceSystem crs = value(IN_CRS, inputParameters);
//        final double step                   = value(IN_STEP, inputParameters);
        double step                   = value(IN_STEP, inputParameters);
        final DirectPosition[] coords       = value(IN_POINTS, inputParameters);
        final Dimension maxDim              = value(IN_DIMENSION, inputParameters);

        //calculate the envelope
        double minx = Double.POSITIVE_INFINITY;
        double miny = Double.POSITIVE_INFINITY;
        double minz = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY;
        double maxy = Double.NEGATIVE_INFINITY;
        double maxz = Double.NEGATIVE_INFINITY;

        //organise values in a table
        final int s = coords.length;
        final double[] x = new double[s];
        final double[] y = new double[s];
        final double[] z = new double[s];

        for(int i=0;i<s;i++) {
            final double cx = coords[i].getOrdinate(0);
            final double cy = coords[i].getOrdinate(1);
            final double cz = coords[i].getOrdinate(2);
            x[i] = cx;
            y[i] = cy;
            z[i] = cz;

            if(cx<minx) minx = cx;
            if(cx>maxx) maxx = cx;
            if(cy<miny) miny = cy;
            if(cy>maxy) maxy = cy;
            if(cz<minz) minz = cz;
            if(cz>maxz) maxz = cz;
        }

        final Rectangle2D rect = new Rectangle2D.Double(minx,miny,maxx-minx,maxy-miny);

        final Dimension dim = new Dimension(s*s, s*s);
        //limit size to 200
        if (maxDim != null) {
            if (dim.height > maxDim.height) dim.height = maxDim.height;
            if (dim.width > maxDim.width)   dim.width  = maxDim.width;
        }

//        final ObjectiveAnalysis ob = new ObjectiveAnalysis(rect, dim);
        final BDHObjectiveAnalysis ob = new BDHObjectiveAnalysis(rect, dim);
        if (crs instanceof ProjectedCRS) {
            // The default ObjectiveAnalysis algorithm is designed for GeographicCRS.
            // In case of ProjectedCRS, we need to apply a scale factor that convert
            // metres to some approximation of angles of longitude/latitude.
            ob.setScaleFactor(1. / (60*1852)); // Use standard length of nautical mile.
        }

//        double[] computed;
//        try {
////            computed = ob.interpole(x, y, z);
////            computed = ob.interpolate(null);
//            computed = ob.interpolate((double[])null);
//        } catch (Exception ex) {
//            throw new ProcessException(null, this, ex);
//        }
//        final double[] cx = ob.getXs();
//        final double[] cy = ob.getYs();
        ob.setInputs(x, y, z);
        RenderedImage renderedImage = ob.createImage();
        final int outLength = ob.getOutputLength();
        final int rIWidth = renderedImage.getWidth();
        final int rIHeight = renderedImage.getHeight();
        final double[] cx = new double[rIWidth];
        final double[] cy = new double[rIHeight];
        final double[] cz = new double[outLength];
        final PixelIterator it = PixelIteratorFactory.createRowMajorIterator(renderedImage);
        int comp = 0;
        while (it.next()) {
            cz[comp++] = it.getSampleDouble();
        }
        final double x0 = Math.min(cx[0], cx[cx.length-1]);
        final double y0 = Math.min(cy[0], cy[cy.length-1]);
        final double spanX = Math.abs((x[x.length-1]-x[0])/rIWidth);
        final double spanY = Math.abs((y[y.length-1]-y[0])/rIHeight);
        for (int i = 0; i<rIWidth; i++) {
            cx[i] = x0 + spanX*i;
        }
        for (int i = 0; i<rIHeight; i++) {
            cy[i] = y0 + spanY*i;
        }


        //create the coverage //////////////////////////////////////////////////
//        final GeneralEnvelope env = new GeneralEnvelope(
//                new Rectangle2D.Double(x[0], y[0], x[x.length-1]-x[0], y[y.length-1]-y[0]));
//        env.setCoordinateReferenceSystem(crs);
//        final GeneralEnvelope env = new GeneralEnvelope(
//                new Rectangle2D.Double(renderedImage.getMinX(), renderedImage.getMinY(), renderedImage.getWidth(), renderedImage.getHeight()));
//        env.setCoordinateReferenceSystem(crs);
        final GeneralEnvelope env = new GeneralEnvelope(
                new double[] {x0, y0},
                new double[] {x0 + spanX, y0 + spanY});
        env.setCoordinateReferenceSystem(crs);

        final GridCoverage2D coverage = toCoverage(cz, cx, cy, env);
        getOrCreate(OUT_COVERAGE, outputParameters).setValue(coverage);

        //test
        renderedImage = coverage.getRenderedImage();

        //create the isolines //////////////////////////////////////////////////
        if (step <= 0) {
            //do not generate isolines
            return;
        }

        step = 2.0;

        final double[] palier = new double[(int)((maxz-minz)/step)];
        for(int i=0;i<palier.length;i++) {
            palier[i] = minz + i*step;
        }

        final IsolineCreator isolineCreator = new IsolineCreator(renderedImage, palier);

        final Map<Point3d,List<Coordinate>> steps;
        try {
//            steps = ob.doContouring(cx, cy, computed, palier);
            steps = isolineCreator.createIsolines();
        } catch(Exception ex) {
            //this task rais some IllegalStateExceptio
            //TODO, fix objective analysis
            throw new ProcessException("Creating isolines geometries failed", this, ex);
        }

        final GeometryFactory GF = new GeometryFactory();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("isoline");
        ftb.add("geometry", LineString.class, crs);
        ftb.add("value", Double.class);
        ftb.setDefaultGeometry("geometry");
        final FeatureType type = ftb.buildFeatureType();

        final FeatureCollection col = FeatureStoreUtilities.collection("id", type);
        int inc = 0;

        for (final Point3d p : steps.keySet()) {
            final List<Coordinate> cshps = steps.get(p);

            if (cshps.get(0).x > cshps.get(cshps.size()-1).x) {
                //the coordinates are going left, reverse order
                Collections.reverse(cshps);
            }

            final LineString geometry = GF.createLineString(cshps.toArray(new Coordinate[cshps.size()]));
            final double value = p.z;

            final Feature f = FeatureUtilities.defaultFeature(type, String.valueOf(inc++));
            f.getProperty("geometry").setValue(geometry);
            f.getProperty("value").setValue(value);
            col.add(f);
        }

        ///////////////  debug///////////////////////
        FeatureIterator featIter = col.iterator();

        final List<Shape> shapes = new ArrayList<Shape>();


        while (featIter.hasNext()) {
            Feature  feaTemp = featIter.next();
            LineString lineS = (LineString)feaTemp.getProperty("geometry").getValue();
            Coordinate[] coordst = lineS.getCoordinates();
            GeneralPath isoline = null;
            for(final Coordinate coord : coordst) {
                if(isoline == null){
                    isoline = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                    isoline.moveTo(coord.x, coord.y);
                }else{
                    isoline.lineTo(coord.x, coord.y);
                }
            }
            shapes.add(isoline);
        }

        final JFrame frm = new JFrame();
        JPanel jp = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setTransform(new AffineTransform2D(1, 0, 0, 1, this.getWidth()/2.0, this.getHeight()/2.0));
//                g2.drawRenderedImage(renderImage, new AffineTransform2D(1, 0, 0, 1, 0,0));
                g2.setColor(Color.BLACK);
                for(Shape shape : shapes){
                    g2.draw(shape);
                }
            }
        };
////////////////////////////////////FIN ISOLINE////////////////////////////////

        frm.setTitle("isoline");
        frm.setSize(1200, 1200);
        frm.setLocationRelativeTo(null);
        frm.add(jp);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);



        /////////////////////////////////////////////




        getOrCreate(OUT_LINES, outputParameters).setValue(col);
    }

    private static GridCoverage2D toCoverage(final double[] computed, final double[] xs, final double[] ys,
            final Envelope env) {

        final float[][] matrix = new float[xs.length][ys.length];

        //TODO find why the matrice is inverted. the envelope ? lines are corrects
        //flip the matrix on y axi
        for (int column=0;column<xs.length;column++) {
            for (int row=0;row<ys.length;row++) {
                //matrix[row][column] = (float)computed[column + row * xs.length];
                matrix[ (ys.length-row-1) ][column] = (float)computed[column + row * xs.length];
            }
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(env);
        gcb.setRenderedImage(matrix);
        return gcb.getGridCoverage2D();
    }

}
