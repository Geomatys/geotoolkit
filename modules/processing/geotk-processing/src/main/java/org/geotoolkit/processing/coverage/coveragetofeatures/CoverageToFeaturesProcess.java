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
package org.geotoolkit.processing.coverage.coveragetofeatures;

import java.awt.geom.Point2D;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
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
 * @module
 */
public class CoverageToFeaturesProcess extends AbstractProcess {
    /**
     * Default constructor
     */
    public CoverageToFeaturesProcess(final ParameterValueGroup input) {
        super(CoverageToFeaturesDescriptor.INSTANCE,input);
    }

    /**
     *
     * @param reader source coverage reader
     */
    public CoverageToFeaturesProcess(GridCoverageReader reader){
        super(CoverageToFeaturesDescriptor.INSTANCE, asParameters(reader));
    }

    private static ParameterValueGroup asParameters(GridCoverageReader reader){
        final Parameters params = Parameters.castOrWrap(CoverageToFeaturesDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(CoverageToFeaturesDescriptor.READER_IN).setValue(reader);
        return params;
    }

    /**
     * Execute process now.
     */
    public FeatureCollection executeNow() throws ProcessException {
        execute();
        return (FeatureCollection) outputParameters.parameter(CoverageToFeaturesDescriptor.FEATURE_OUT.getName().getCode()).getValue();
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException{
        try {
            final GridCoverageReader reader = inputParameters.getValue(CoverageToFeaturesDescriptor.READER_IN);
            final GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
            final GridGeometry gridGeom = reader.getGridGeometry();

            final CoverageToFeatureCollection resultFeatureList =
                    new CoverageToFeatureCollection(reader, gridGeom.getExtent(), coverage, gridGeom);

            outputParameters.getOrCreate(CoverageToFeaturesDescriptor.FEATURE_OUT).setValue(resultFeatureList);
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

    /**
     * Create the new FeatureType from the coverage and the reader.
     *
     * @return the FeatureType of Features
     */
    static FeatureType createFeatureType(final GridCoverage2D coverage, final GridCoverageReader reader) throws DataStoreException {

        final int nbBand = coverage.getSampleDimensions().size();

        final FeatureTypeBuilder typeBuilder = new FeatureTypeBuilder();
        typeBuilder.setName("FeatureCoverage");
        typeBuilder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        typeBuilder.addAttribute(Point.class).setName("position").setCRS(reader.getGridGeometry().getCoordinateReferenceSystem()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        typeBuilder.addAttribute(Polygon.class).setName("cellgeom").setCRS(reader.getGridGeometry().getCoordinateReferenceSystem());
        typeBuilder.addAttribute(String.class).setName("orientation");

        for (int i = 0; i < nbBand; i++) {
            typeBuilder.addAttribute(Double.class).setName("band-" + i);
        }
        return typeBuilder.build();
    }

    /**
     * Create a Feature with a cell coordinate (x,y).
     *
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
    static Feature convertToFeature(FeatureType type, long x, long y, GridCoverage2D coverage, GridCoverageReader reader,
            GridGeometry gridGeom) throws DataStoreException, TransformException {

        final GeometryFactory geomFac = new GeometryFactory();
        //get the number of band contained in a cell
        final List<SampleDimension> dims = coverage.getSampleDimensions();
        final int nbBand = dims.size();

        final Georectified rep = reader.getCoverageMetadata().getInstanceForType(Georectified.class);

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

        final double[] resolution = gridGeom.getResolution(true);
        double gapX = resolution[0];
        double gapY = resolution[1];

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
        Feature myfeature = type.newInstance();
        myfeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-" + x + "-" + y);
        myfeature.setPropertyValue("cellgeom", geomFac.createPolygon(geomFac.createLinearRing(coord), null));
        myfeature.setPropertyValue("position", geomFac.createPoint(new Coordinate(pt2[0], pt2[1])));
        myfeature.setPropertyValue("orientation", posPix.name());
        for (int att = 0; att < nbBand; att++) {
            myfeature.setPropertyValue("band-" + att, infoBand[att]);
        }
        return myfeature;
    }
}
