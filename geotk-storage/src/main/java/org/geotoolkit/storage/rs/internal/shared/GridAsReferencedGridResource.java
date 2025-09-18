/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.rs.internal.shared;

import org.geotoolkit.storage.dggs.internal.shared.GridAsDiscreteGlobalGridResource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.RasterLoadingStrategy;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.rs.Address;
import org.geotoolkit.storage.rs.ReferencedGridCoverage;
import org.geotoolkit.storage.rs.ReferencedGridGeometry;
import org.geotoolkit.storage.rs.ReferencedGridResource;
import org.geotoolkit.storage.rs.ReferencedGridTransform;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * View a grid coverage resource as a dggrs coverage resource.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GridAsReferencedGridResource extends AbstractResource implements ReferencedGridResource {

    private final ReferencedGridGeometry gridGeometry;
    private final GridCoverageResource source;
    private final GenericName name;

    public GridAsReferencedGridResource(GenericName name, DiscreteGlobalGridReferenceSystem dggrs, GridCoverageResource resource)
            throws DataStoreException, IncommensurableException, TransformException, FactoryException {
        super(null);
        ArgumentChecks.ensureNonNull("name", name);
        ArgumentChecks.ensureNonNull("resource", resource);
        this.name = name;
        this.source = resource;

        //create matching reference grid geometry replacing the horizontal crs
        final GridGeometry sourceGridGeometry = resource.getGridGeometry();
        final List<SingleCRS> singles = (List) ReferenceSystems.getSingleComponents(sourceGridGeometry.getCoordinateReferenceSystem(), true);
        final List<ReferencedGridGeometry> parts = new ArrayList<>();

        for (int i = 0, n = singles.size(); i < n ; i++) {
            final SingleCRS s = singles.get(i);
            if (CRS.isHorizontalCRS(s)) {
                ReferencedGridGeometry rrg = new ReferencedGridGeometry(dggrs, new GridExtent(null, 0, 0, true), ReferencedGridTransforms.toTransform(dggrs), null);
                parts.add(rrg);
            } else {
                final GridGeometry crsSlice = slice(sourceGridGeometry, s);
                parts.add(new ReferencedGridGeometry(crsSlice));
            }
        }
        gridGeometry = ReferencedGridGeometry.compound(parts.toArray(ReferencedGridGeometry[]::new));

        Quantity<?> res = GridAsDiscreteGlobalGridResource.computeAverageResolution(resource.getGridGeometry());
        DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        coder.setPrecision(res, null);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(name);
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return source.getSampleDimensions();
    }

    @Override
    public FeatureType getSampleType() throws DataStoreException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(source.getIdentifier().get());
        ReferencedGridCoverageAsFeatureSet.toFeatureType(ftb, getSampleDimensions());
        return ftb.build();
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return source.getEnvelope();
    }

    @Override
    public ReferencedGridGeometry getGridGeometry() {
        return gridGeometry;
    }

    @Override
    public ReferencedGridCoverage read(ReferencedGridGeometry userQuery, int... range) throws DataStoreException {

        List<SampleDimension> sampleDimensions = source.getSampleDimensions();
        if (range != null && range.length != 0) {
            final List<SampleDimension> selected = new ArrayList<>();
            for (int i = 0; i < range.length; i++) {
                selected.add(sampleDimensions.get(range[i]));
            }
            sampleDimensions = selected;
        }

        /*
        Convert the geometry to source grid geometry.
        add additional dimensions if some are missing.
        */
        GridGeometry coverageQuery;
        try {
            coverageQuery = toGridGeometry(userQuery);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
        if (source.getGridGeometry().isDefined(GridGeometry.EXTENT | GridGeometry.GRID_TO_CRS)) {
            //append dimensions we did not have in the query and remove those we don't have
            //bug coverageQuery = source.getGridGeometry().derive().rounding(GridRoundingMode.ENCLOSING).subgrid(coverageQuery).build();

            try {
                coverageQuery = smartIntersect(source.getGridGeometry(), coverageQuery);
            } catch (FactoryException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            } catch (DisjointExtentException ex) {
                //no intersection
                throw new NoSuchDataException("Data do not intersect requested area", ex);
            }
        }
        //convert it back to a referenced grid geometry query
        final ReferencedGridGeometry resultGeometry;
        try {
            resultGeometry = expend(userQuery, coverageQuery);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        final GridExtent extent = resultGeometry.getExtent();
        final ReferencedGridTransform transform = resultGeometry.getGridToRS();
        final int nbSamples = Math.toIntExact(TileMatrices.countCells(extent)) * sampleDimensions.size();

        final List<TupleArray> samples = new ArrayList<>();
        final double[] nans = new double[sampleDimensions.size()];
        for (int i = 0; i < nans.length; i++) {
            final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
            final double[] datas = new double[nbSamples];
            Arrays.fill(datas, Double.NaN);
            samples.add(TupleArrays.of(ss, datas));
            nans[i] = Double.NaN;
        }

        final ArrayReferencedGridCoverage target;
        try {
            target = new ArrayReferencedGridCoverage(getIdentifier().get(), resultGeometry, samples);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        try (final WritableBandedAddressIterator iterator = target.createWritableIterator()) {
            try {
                source.setLoadingStrategy(RasterLoadingStrategy.AT_GET_TILE_TIME);
                final GridCoverage coverage = source.read(coverageQuery, range).forConvertedValues(true);
                final GridCoverage.Evaluator evaluator = coverage.evaluator();
                evaluator.setNullIfOutside(true);
                evaluator.setWraparoundEnabled(true);

                while (iterator.next()) {
                    final int[] gridPosition = iterator.getPosition();
                    final Address location = transform.toAddress(gridPosition);
                    DirectPosition dp = location.toDirectPosition();
                    //todo convert to grid coverage coordinate
                    double[] values = evaluator.apply(dp);
                    if (values == null) {
                        values = nans;
                    }
                    iterator.setCell(values);
                }
            } catch (NoSuchDataException | FactoryException ex) {
                // do nothing
            }
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }

        return target;
    }

    private static GridGeometry slice(GridGeometry base, CoordinateReferenceSystem crs) throws FactoryException {
        final List<SingleCRS> singles = (List) ReferenceSystems.getSingleComponents(base.getCoordinateReferenceSystem(), true);
        int idx = 0;
        for (SingleCRS s : singles) {
            if (s == crs) {
                break;
            }
            idx += s.getCoordinateSystem().getDimension();
        }

        final MathTransform gridToCRS = base.getGridToCRS(PixelInCell.CELL_CENTER);
        final TransformSeparator ts = new TransformSeparator(gridToCRS);
        ts.addTargetDimensions(idx);
        final MathTransform trs = ts.separate();
        final int[] sourceDimensions = ts.getSourceDimensions();

        if (sourceDimensions.length == 1) {
            return base.selectDimensions(sourceDimensions);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static GridGeometry toGridGeometry(ReferencedGridGeometry geometry) throws FactoryException {

        final List<ReferenceSystem> all = ReferenceSystems.getSingleComponents(geometry.getReferenceSystem(), true);

        GridGeometry gg = null;
        for (int k = 0, n = all.size(); k < n; k++) {
            final ReferencedGridGeometry rgg = geometry.slice(all.get(k)).get();
            GridGeometry slice = rgg.isRegularGrid().orElse(null);
            if (slice == null) {
                final Envelope env = rgg.getEnvelope();
                final double[] resolution = rgg.getResolutionProjected(true);

                final long[] low = new long[env.getDimension()];
                final long[] high = new long[low.length];
                for (int i = 0 ; i < low.length; i++) {
                    high[i] = (long)Math.ceil((env.getSpan(i) / resolution[i])*2);
                }
                final GridExtent extent = new GridExtent(null, low, high, false);
                slice = new GridGeometry(extent, env, GridOrientation.HOMOTHETY);
            }
            gg = (gg == null) ? slice : new GridGeometry(gg, slice);
        }

        return gg;
    }

    /**
     * Create a new ReferencedGridGeometry containing the base ReferencedGridGeometry
     * and adding any dimension missing from the second GridGeometry.
     */
    private static ReferencedGridGeometry expend(ReferencedGridGeometry base, GridGeometry toAppend) throws FactoryException {

        final Set<ReferenceSystem> all = new LinkedHashSet();
        all.addAll(ReferenceSystems.getSingleComponents(toAppend.getCoordinateReferenceSystem(), true));

        final List<ReferencedGridGeometry> parts = new ArrayList<>();
        for (ReferenceSystem rs : all) {
            if (rs instanceof CoordinateReferenceSystem crs) {
                if (CRS.isHorizontalCRS(crs)) {
                    //find the dggrs in the base
                    for (ReferenceSystem brs : ReferenceSystems.getSingleComponents(base.getReferenceSystem(), true)) {
                        if (brs instanceof DiscreteGlobalGridReferenceSystem dggrs) {
                            //copy it from base
                            parts.add(base.slice(brs).get());
                        }
                    }
                } else {
                    Optional<ReferencedGridGeometry> slice = base.slice(rs);
                    if (!slice.isPresent()) {
                        //copy it from the missing geometry
                        parts.add(new ReferencedGridGeometry(slice(toAppend, crs)));
                    } else {
                        parts.add(slice.get());
                    }
                }
            }
        }

        return ReferencedGridGeometry.compound(parts.toArray(ReferencedGridGeometry[]::new));
    }

    private static GridGeometry smartIntersect(GridGeometry base, GridGeometry query) throws FactoryException {

        final List<ReferenceSystem> queryCrs = ReferenceSystems.getSingleComponents(query.getCoordinateReferenceSystem(), true);
        final ReferencedGridGeometry basergg = new ReferencedGridGeometry(base);
        final ReferencedGridGeometry queryrgg = new ReferencedGridGeometry(query);

        GridGeometry result = null;

        for (ReferenceSystem rs : ReferenceSystems.getSingleComponents(base.getCoordinateReferenceSystem(), true)) {
            final CoordinateReferenceSystem baseSliceCrs = (CoordinateReferenceSystem) rs;
            GridGeometry slice = basergg.slice(baseSliceCrs).get().isRegularGrid().get();

            for (ReferenceSystem target : queryCrs) {
                try {
                    CRS.findOperation(baseSliceCrs, (CoordinateReferenceSystem) target, null);
                    final GridGeometry targetSlice = queryrgg.slice(target).get().isRegularGrid().get();
                    slice = slice.derive().rounding(GridRoundingMode.ENCLOSING).subgrid(targetSlice).build();
                    break;
                } catch (FactoryException ex) {
                    //not found
                }
            }

            result = result == null ? slice : new GridGeometry(result, slice);
        }

        return result;
    }

}
