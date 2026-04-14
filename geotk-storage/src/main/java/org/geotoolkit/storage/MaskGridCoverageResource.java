/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.storage;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.apache.sis.coverage.RegionOfInterest;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.wrapper.jts.JTS;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.GeodeticCalculator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * Decorate a GridCoverageResource applying a masking operation on all read operations.
 *
 * @author Johann Sorel (Geomatys)
 */
final class MaskGridCoverageResource extends DerivedGridCoverageResource {

    private final FeatureSet mask;
    private final boolean maskInside;
    private final String maskGeometryPropertyName;

    MaskGridCoverageResource(GridCoverageResource base, FeatureSet mask, boolean maskInside) {
        super(null, base);
        this.mask = mask;
        this.maskInside = maskInside;
        try {
            final PropertyType maskGeometryProperty = FeatureExt.getDefaultGeometry(mask.getType());
            maskGeometryPropertyName = maskGeometryProperty.getName().toString();
        } catch (DataStoreException | PropertyNotFoundException | IllegalStateException e) {
            throw new IllegalArgumentException("Cannot determine geometry property to use as mask", e);
        }
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return source.getIdentifier();
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return source.getEnvelope();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return source.getGridGeometry();
    }

    @Override
    public List<double[]> getResolutions() throws DataStoreException {
        return source.getResolutions();
    }

    @Override
    public GridCoverage read(GridGeometry gg, int... ints) throws DataStoreException {

        //read the base coverage
        final GridCoverage inCoverage = source.read(gg, ints);
        final GridGeometry gridGeometry = inCoverage.getGridGeometry();

        Quantity<Length> linearResolution = null;
        if (gridGeometry.isDefined(GridGeometry.RESOLUTION) && gridGeometry.isDefined(GridGeometry.CRS)) {
            final double[] resolution = gridGeometry.getResolution(true);
            final CoordinateReferenceSystem crs = gridGeometry.getCoordinateReferenceSystem();
            final Quantity<?> hres = CRSUtilities.getHorizontalResolution(crs, resolution);
            if (hres != null) {
                if (hres.getUnit().isCompatible(Units.METRE)) {
                    linearResolution = hres.asType(Length.class);
                } else if (gridGeometry.isDefined(GridGeometry.GRID_TO_CRS)){
                    final MathTransform gridToCRS = gridGeometry.getGridToCRS(PixelInCell.CELL_CENTER);
                    final double[] poi = gridGeometry.getExtent().getPointOfInterest(PixelInCell.CELL_CENTER);
                    final int hAxe = CRSUtilities.firstHorizontalAxis(crs);
                    try {
                        gridToCRS.transform(poi, 0, poi, 0, 1);
                        final GeodeticCalculator calc = GeodeticCalculator.create(crs);
                        final GeneralDirectPosition start = new GeneralDirectPosition(crs);
                        final GeneralDirectPosition end = new GeneralDirectPosition(crs);
                        start.setCoordinates(poi);
                        poi[hAxe] += resolution[hAxe];
                        end.setCoordinates(poi);
                        calc.setStartPoint(start);
                        calc.setEndPoint(end);
                        final double distance = calc.getGeodesicDistance();
                        linearResolution = Quantities.create(distance, calc.getDistanceUnit());
                    } catch (TransformException ex) {
                        //we have try
                    }
                }
            }
        }

        //compute the masking area at matching resolution and envelope
        final FeatureQuery query = new FeatureQuery();
        query.setLinearResolution(linearResolution);
        query.setSelection(gridGeometry.getEnvelope());
        query.setProjection(maskGeometryPropertyName);
        final FeatureSet subset = mask.subset(query);

        long nbFeatures = 0, nbNulls = 0, nbUnsupported = 0;
        final java.awt.geom.Area shape = new java.awt.geom.Area();
        try (Stream<Feature> stream = subset.features(true)) {
            final Iterator<Feature> iterator = stream.iterator();
            while (iterator.hasNext()) {
                final Feature f = iterator.next();
                nbFeatures++;
                final Object geometryRawValue = f.getPropertyValue(maskGeometryPropertyName);
                if (geometryRawValue == null) {
                    nbNulls++;
                } else if (geometryRawValue instanceof Geometry geom) {
                    shape.add(new java.awt.geom.Area(JTS.asShape(geom)));
                } else if (geometryRawValue instanceof java.awt.Shape geomShape) {
                    shape.add(new java.awt.geom.Area(geomShape));
                } else {
                    nbUnsupported++;
                }
            }
        }

        if (nbUnsupported != 0) {
            throw new DataStoreException("Raster masking failed because unsupported geometries where found. Statistics: " + nbFeatures + " features read, " + nbNulls + " null geometries and " + nbUnsupported + " unsupported geometries");
        }

        final RegionOfInterest roi = new RegionOfInterest(shape, FeatureExt.getCRS(subset.getType()));

        //apply the mask
        final GridCoverageProcessor gcp = new GridCoverageProcessor();
        try {
            return gcp.mask(inCoverage, roi, maskInside);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

}
