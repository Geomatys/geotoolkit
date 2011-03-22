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
package org.geotoolkit.process.raster.coveragetofeatures;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;

import org.opengis.coverage.SampleDimensionType;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Process CoverageToFeature create a collection of Feature based on a coverage layer.
 * Each features refers to a coverage's cell. The resulting feature type is :
 * <ul>
 *      <li>name : FeatureCoverage</li>
 *      <li>position : cell position defines with a Point</li>
 *      <li>cellgeom : cell geometry defines with a polygon </li>
 *      <li>orientation : the PixelInCell position (CELL_CENTER or CELL_CORNER)</li>
 *      <li>band-(0-n) : each bands contained in the cell</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public class CoverageToFeatures extends AbstractProcess {

    ParameterValueGroup result;

    /**
     * Default constructor
     */
    public CoverageToFeatures() {
        super(CoverageToFeaturesDescriptor.INSTANCE);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterValueGroup getOutput() {
        return result;
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public void run() {
        try {
            GridCoverageReader reader = Parameters.value(CoverageToFeaturesDescriptor.READER_IN, inputParameters);
            GridCoverage2D coverage = (GridCoverage2D) reader.read(0, null);
            GeneralGridGeometry gridGeom = reader.getGridGeometry(0);

            final CoverageToFeatureCollection resultFeatureList =
                    new CoverageToFeatureCollection(reader, gridGeom.getGridRange(), coverage, gridGeom);

            result = super.getOutput();
            result.parameter(CoverageToFeaturesDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);

        } catch (CoverageStoreException ex) {
            getMonitor().failed(new ProcessEvent(this, 0, null, ex));
            Logger.getLogger(CoverageToFeatures.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create the new FeatureType from the coverage and the reader.
     * @param coverage
     * @param reader
     * @return the FeatureType of Features
     * @throws CoverageStoreException
     */
    static FeatureType createFeatureType(final GridCoverage2D coverage, final GridCoverageReader reader) throws CoverageStoreException {

        final int nbBand = coverage.getNumSampleDimensions();

        final FeatureTypeBuilder typeBuilder = new FeatureTypeBuilder();
        typeBuilder.setName("FeatureCoverage");
        typeBuilder.add("position", Point.class, reader.getGridGeometry(0).getCoordinateReferenceSystem());
        typeBuilder.add("cellgeom", Polygon.class, reader.getGridGeometry(0).getCoordinateReferenceSystem());
        typeBuilder.add("orientation", String.class);

        int type = 1;
        if (coverage.getSampleDimension(0).getSampleDimensionType() == SampleDimensionType.REAL_32BITS) {
            type = 1; //Float
        } else if (coverage.getSampleDimension(0).getSampleDimensionType() == SampleDimensionType.REAL_64BITS) {
            type = 2; //double
        } else {
            type = 3; //Int
        }
        for (int i = 0; i < nbBand; i++) {
            switch (type) {
                case 1:
                    typeBuilder.add("band-" + i, Float.class);
                    break;
                case 2:
                    typeBuilder.add("band-" + i, Double.class);
                    break;
                case 3:
                    typeBuilder.add("band-" + i, Integer.class);
                    break;
                default:
                    typeBuilder.add("band-" + i, Float.class);
                    break;
            }
        }
        typeBuilder.setDefaultGeometry("position");
        return typeBuilder.buildFeatureType();
    }

    /**
     * Create a Feature with a cell coordinate (x,y).
     * @param type the FeatureType
     * @param x
     * @param y
     * @param coverage
     * @param reader
     * @param gridGeom
     * @return the cell Feature
     * @throws CoverageStoreException
     * @throws TransformException
     */
    static Feature convertToFeature(FeatureType type, int x, int y, GridCoverage2D coverage, GridCoverageReader reader,
            GeneralGridGeometry gridGeom) throws CoverageStoreException, TransformException {

        final GeometryFactory geomFac = new GeometryFactory();
        //get the number of band contained in a cell
        final int nbBand = coverage.getNumSampleDimensions();

        final Georectified rep = reader.getCoverageMetadata(0).getInstanceForType(Georectified.class);

        //Define the pixel position in cells
        PixelInCell posPix = PixelInCell.CELL_CENTER;
        final PixelOrientation pixOr = rep.getPointInPixel();
        if (pixOr == PixelOrientation.CENTER) {
            posPix = PixelInCell.CELL_CENTER;
        } else if (pixOr == PixelOrientation.UPPER_LEFT) {
            posPix = PixelInCell.CELL_CORNER;
        }

        //get the MathTransform frome grid to the CRS
        final MathTransform transfo = gridGeom.getGridToCRS(posPix);

        double[] pt1 = new double[]{x, y};
        double[] pt2 = new double[2];
        //transform x,y cell coordinate with the gridToCRS MathTransform
        transfo.transform(pt1, 0, pt2, 0, 1);

        //make a Point2D with transformed coordinates in order to get cell bands
        Point2D point2d = new Point2D.Double();
        point2d.setLocation(pt2[0], pt2[1]);

        double[] infoBand = new double[nbBand];
        coverage.evaluate(point2d, infoBand);

        double gapX = gridGeom.getResolution()[0];
        double gapY = gridGeom.getResolution()[1];

        //compute the cell geometry from transform coordinates
        Coordinate[] coord;
        if (posPix == PixelInCell.CELL_CENTER) {

            coord = new Coordinate[]{
                        new Coordinate(pt2[0] - gapX / 2, pt2[1] + gapY / 2),
                        new Coordinate(pt2[0] + gapX / 2, pt2[1] + gapY / 2),
                        new Coordinate(pt2[0] + gapX / 2, pt2[1] - gapY / 2),
                        new Coordinate(pt2[0] - gapX / 2, pt2[1] - gapY / 2),
                        new Coordinate(pt2[0] - gapX / 2, pt2[1] + gapY / 2)
                    };
        } else {
            coord = new Coordinate[]{
                        new Coordinate(pt2[0], pt2[1]),
                        new Coordinate(pt2[0] + gapX, pt2[1]),
                        new Coordinate(pt2[0] + gapX, pt2[1] - gapY),
                        new Coordinate(pt2[0], pt2[1] - gapY),
                        new Coordinate(pt2[0], pt2[1])
                    };
        }
        //create the Feature
        Feature myfeature = FeatureUtilities.defaultFeature(type, "id-" + x + "-" + y);
        myfeature.getProperty("cellgeom").setValue(geomFac.createPolygon(geomFac.createLinearRing(coord), null));
        myfeature.getProperty("position").setValue(geomFac.createPoint(new Coordinate(pt2[0], pt2[1])));
        myfeature.getProperty("orientation").setValue(posPix.name());
        for (int att = 0; att < nbBand; att++) {
            myfeature.getProperty("band-" + att).setValue(infoBand[att]);
        }
        return myfeature;
    }
}
