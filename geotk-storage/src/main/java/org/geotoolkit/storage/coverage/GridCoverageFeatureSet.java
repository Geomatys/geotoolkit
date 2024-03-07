/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.AbstractAssociation;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.JTSMapping;
import org.geotoolkit.geometry.jts.coordinatesequence.LiteCoordinateSequence;
import org.geotoolkit.internal.feature.TypeConventions;
import org.geotoolkit.storage.AbstractResource;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociation;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.InvalidPropertyValueException;
import org.opengis.feature.MultiValuedPropertyException;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;
import org.opengis.util.LocalName;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive;

/**
 * Decorate a GridCoverageResource as a FeatureSet.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GridCoverageFeatureSet extends AbstractResource implements FeatureSet {

    private static final String ATT_COLOR = "color";
    private static final LocalName DEFAULT_COVERAGE_NAME = Names.createLocalName(null, null, "Coverage");

    private final org.apache.sis.storage.GridCoverageResource gcr;

    public GridCoverageFeatureSet(org.apache.sis.storage.GridCoverageResource gcr) throws DataStoreException {
        identifier = NamedIdentifier.castOrCopy(gcr.getIdentifier().orElse(Names.createLocalName(null, null, "Voxel")));
        this.gcr = gcr;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return gcr.getEnvelope();
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return createCoverageType(gcr);
    }

    @Override
    public Stream<Feature> features(boolean parallal) throws DataStoreException {
        final FeatureType type = getType();
        final FeatureAssociationRole role = (FeatureAssociationRole) type.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final Feature feature = type.newInstance();

        final GridGeometry gridGeom = gcr.getGridGeometry();
        Envelope envelope = gridGeom.getEnvelope();
        if (envelope != null) {
            Geometry geom = GeometricUtilities.toJTSGeometry(envelope, GeometricUtilities.WrapResolution.SPLIT);
            if (geom != null) {
                geom = JTSMapping.convertType(geom, MultiPolygon.class);
                JTS.setCRS(geom, gridGeom.getCoordinateReferenceSystem());
                feature.setPropertyValue(AttributeConvention.GEOMETRY, geom);
            }
        }
        feature.setProperty(coverageRecords(gcr, role));
        return Stream.of(feature);
    }

    /**
     * FeatureSet of all coverage voxels.
     */
    public FeatureSet voxels() throws DataStoreException {
        final FeatureType type = getType();
        final FeatureAssociationRole role = (FeatureAssociationRole) type.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final FeatureType valueType = role.getValueType();

        return new AbstractFeatureSet(null, false) {
            @Override
            public FeatureType getType() {
                return valueType;
            }

            @Override
            public Stream<Feature> features(boolean parallel) {
                final Stream<Feature> dataStream = create(valueType, gcr);
                return parallel ? dataStream.parallel() : dataStream;
            }
        };
    }

    public static FeatureType createCoverageType(GridCoverage coverage) throws DataStoreException {
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(coverage.getCoordinateReferenceSystem());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_TYPE);
        ftb.setName(getName(coverage));
        ftb.addAttribute(Polygon.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAssociation(createRecordType(coverage)).setName(TypeConventions.RANGE_ELEMENTS_PROPERTY).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);

        return ftb.build();
    }

    public static FeatureType createCoverageType(org.apache.sis.storage.GridCoverageResource resource) throws DataStoreException {

        final GridGeometry gridGeometry = resource.getGridGeometry();
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(gridGeometry.getCoordinateReferenceSystem());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_TYPE);
        ftb.setName(resource.getIdentifier().orElse(null));
        // define the geometry as a MultiPolygon, it may happen when the pixel crosses the anti-meridian
        ftb.addAttribute(MultiPolygon.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAssociation(createRecordType(resource)).setName(TypeConventions.RANGE_ELEMENTS_PROPERTY).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);

        return ftb.build();
    }

    public static FeatureType createRecordType(GridCoverage coverage) throws DataStoreException {
        return createRecordType(getName(coverage), coverage.getCoordinateReferenceSystem(), coverage.getSampleDimensions());
    }

    public static FeatureType createRecordType(org.apache.sis.storage.GridCoverageResource resource) throws DataStoreException {
        return createRecordType(
                resource.getIdentifier().orElse(DEFAULT_COVERAGE_NAME),
                resource.getGridGeometry().getCoordinateReferenceSystem(),
                resource.getSampleDimensions());
    }

    private static FeatureType createRecordType(final GenericName dataName, final CoordinateReferenceSystem crs, final List<SampleDimension> samples) {
        final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(crs);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TypeConventions.COVERAGE_RECORD_TYPE);
        ftb.setName(Names.createScopedName(dataName, null, "Record"));
        ftb.addAttribute(Geometry.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs2d).setMinimumOccurs(1).setMaximumOccurs(1).addRole(AttributeRole.DEFAULT_GEOMETRY);

        //if the CRS has more the 2 dimensions, we convert the envelope operation
        //to an attribute, the envelope will be N dimesion, and the geometry 2D
        if (crs.getCoordinateSystem().getDimension() > 2) {
            ftb.addAttribute(Envelope.class).setName(AttributeConvention.ENVELOPE_PROPERTY).setCRS(crs);
        }

        //use existing sample dimensions
        for (int i=0,n=samples.size();i<n;i++) {
            final SampleDimension gsd = samples.get(i);
            final String name = gsd.getName() == null ? ""+i : gsd.getName().toString();
            ftb.addAttribute(Double.class).setName(name).setMinimumOccurs(1).setMaximumOccurs(1);
        }
        ftb.addAttribute(Color.class).setName(ATT_COLOR);
        return ftb.build();
    }

    public static FeatureAssociation coverageRecords(final GridCoverageResource res, final FeatureAssociationRole role) {
        try {
            return coverageRecords(role, res.getGridGeometry(), () -> create(role.getValueType(), res));
        } catch (DataStoreException e) {
            throw new BackingStoreException("Cannot acquire grid geometry of input resource", e);
        }
    }

    // TODO: check if this is really desirable. It looks like
    public static FeatureAssociation coverageRecords(final GridCoverage coverage, final FeatureAssociationRole role) {
        return coverageRecords(role, coverage.getGridGeometry(), () -> create(role.getValueType(), coverage));
    }

    static FeatureAssociation coverageRecords(final FeatureAssociationRole role, final GridGeometry sourceGeometry, Supplier<Stream<Feature>> cellExtractor) {
        if (!sourceGeometry.isDefined(GridGeometry.EXTENT)) {
            // Memory safety. See GridCoverageSpliterator constructor below for more explanations
            throw new IllegalArgumentException("Cannot model a coverage as feature association if it does not define an extent." +
                    " Please resample your dataset beforehand to provide a fixed resolution view of the data.");
        }

        final long size = getTotalNumberOfCells(sourceGeometry.getExtent());
        final int count;
        try {
            count = Math.toIntExact(size);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Input coverage provides too many cells for them to be modeled as a collection of cells (require less than 2 billion cells due to Java API)");
        }

        final Collection<Feature> pixels = new AbstractCollection<Feature>() {
            @Override
            public Iterator<Feature> iterator() {
                return cellExtractor.get().iterator();
            }

            @Override
            public void forEach(Consumer<? super Feature> action) {
                cellExtractor.get().forEach(action);
            }

            @Override
            public Spliterator<Feature> spliterator() {
                return cellExtractor.get().spliterator();
            }

            @Override
            public Stream<Feature> stream() {
                return cellExtractor.get();
            }

            @Override
            public Stream<Feature> parallelStream() {
                return cellExtractor.get().parallel();
            }

            @Override
            public int size() {
                return count;
            }
        };

        return new AbstractAssociation(role) {
            @Override
            public Collection<Feature> getValues() {
                return pixels;
            }

            @Override
            public void setValues(Collection<? extends Feature> values) throws InvalidPropertyValueException {
                throw new InvalidPropertyValueException("Property is unmodifiable.");
            }

            @Override
            public Feature getValue() throws MultiValuedPropertyException {
                throw new MultiValuedPropertyException();
            }

            @Override
            public void setValue(Feature value) throws InvalidPropertyValueException {
                throw new InvalidPropertyValueException("Property is unmodifiable.");
            }
        };
    }

    private static Stream<Feature> create(FeatureType recordType, GridCoverage coverage) {
        final GridGeometry geom = coverage.getGridGeometry();
        final Function<GridGeometry, GridCoverage2DRecordIterator> sliceGenerator = sliceGeom
                -> new GridCoverage2DRecordIterator(recordType, coverage, sliceGeom.getExtent());
        return StreamSupport.stream(new GridCoverageSpliterator(geom, sliceGenerator), false);
    }

    private static Stream<Feature> create(FeatureType recordType, GridCoverageResource coverage) {
        final GridGeometry geom;
        try {
            geom = coverage.getGridGeometry();
        } catch (DataStoreException e) {
            throw new BackingStoreException("Cannot read grid geometry of input resource", e);
        }
        final Function<GridGeometry, GridCoverage2DRecordIterator> sliceGenerator = sliceGeom -> {
            final GridCoverage slicedData;
            try {
                slicedData = coverage.read(sliceGeom);
            } catch (DataStoreException e) {
                throw new BackingStoreException("Cannot extract a slice from source dataset", e);
            }
            return new GridCoverage2DRecordIterator(recordType, slicedData);
        };
        return StreamSupport.stream(new GridCoverageSpliterator(geom, sliceGenerator), false);
    }

    /**
     * Compute how many cells are available in the grid described by the given extent. It is a simple multiplication of
     * the size of each axis in the extent, with only a little check to ensure each axis value is strictly positive.
     * @param sourceExtent The extent to compute number of described cells for. Must not be null.
     * @return Number of cells found in given extent. Always <em>strictly positive</em>.
     * @throws IllegalArgumentException if input extent is invalid. An extent is marked invalid if it defines no
     *                                  dimensions, or if all of its dimensions define a size inferior to 1.
     */
    private static long getTotalNumberOfCells(GridExtent sourceExtent) {
        final long totalNumberOfCells;
        final long[] gridAxisSizes = IntStream.range(0, sourceExtent.getDimension())
                .mapToLong(sourceExtent::getSize)
                .toArray();
        totalNumberOfCells = Arrays.stream(gridAxisSizes)
                // Corner cases: an extent could provide a size of 0, but that does not mean the entire extent has
                // degenerated to a 0-dimension dataset. It is used as a sort of workaround to express very thin
                // slices (enumerated values on axis without any thickness). To avoid error, we remove them before
                // computing the total number of cells. Note, we do not remove ones. It is VERY important, because even
                // if there's no reason to use them in the multiplication, it allows to distinguish valid cases where an
                // extent size is a single cell, from cases where an extent would declare a size < 1.
                .filter(value -> value > 0)
                .reduce(Math::multiplyExact)
                .orElseThrow(() -> new IllegalArgumentException("Input extent has 0 dimensions, or specify only dimensions with a size inferior to 1 (which is invalid)"));
        return totalNumberOfCells;
    }

    /**
     * If input is also a {@link Resource}, return its {@link Resource#getIdentifier() identifier } if present.
     * If no name is found, a default constant value is returned.
     */
    private static GenericName getName(GridCoverage source) throws DataStoreException {
        if (source instanceof Resource) return ((Resource) source).getIdentifier().orElse(DEFAULT_COVERAGE_NAME);
        else return DEFAULT_COVERAGE_NAME;
    }

    private static class GridCoverageSpliterator implements Spliterator<Feature> {

        private final GridGeometryIterator slices;
        private final long totalNumberOfCells;

        private GridCoverage2DRecordIterator currentSlice;

        private final Function<GridGeometry, GridCoverage2DRecordIterator> sliceGenerator;

        private GridCoverageSpliterator(final GridGeometry sourceGeom, Function<GridGeometry, GridCoverage2DRecordIterator> sliceGenerator) {
            ensureNonNull("Source grid geometry", sourceGeom);
            ensureNonNull("Slice generator", sliceGenerator);
            if (!sourceGeom.isDefined(GridGeometry.EXTENT)) {
                /* To avoid potential memory problems, force user to have prepared its datasource at a fixed resolution.
                 * Note that, for now, this safety is required because the spliterator implementation subset source dataset
                 * by slicing its geometry extent. In the future, if a better solution is setup, and that it does not
                 * require source extent, we could remove this safety.
                 */
                throw new IllegalArgumentException("Cannot stream a dataset whose extent is not defined. Please resample your dataset beforehand to provide a fixed resolution view of the data.");
            }
            this.sliceGenerator = sliceGenerator;
            slices = new GridGeometryIterator(sourceGeom);

            final GridExtent sourceExtent = sourceGeom.getExtent();
            totalNumberOfCells = getTotalNumberOfCells(sourceExtent);
            ensureStrictlyPositive("Total number of cells", totalNumberOfCells);
        }

        @Override
        public boolean tryAdvance(Consumer<? super Feature> action) {
            if (currentSlice != null && currentSlice.hasNext()) {
                final Feature nextFeature = currentSlice.next();
                action.accept(nextFeature);
                return true;
            } else if (slices == null || !slices.hasNext()) {
                return false;
            } else {
                final GridGeometry nextSlice = slices.next();
                currentSlice = sliceGenerator.apply(nextSlice);
            }

            // Note: candidate to tail-recursion, potentially optimisable by JVM.
            return tryAdvance(action);
        }

        @Override
        public Spliterator<Feature> trySplit() {
            // TODO: split by 2D slice ?
            return null;
        }

        @Override
        public long estimateSize() {
            // TODO: decrease at each iteration
            return totalNumberOfCells;
        }

        @Override
        public int characteristics() {
            // IMPORTANT: The spliterator is NOT ordered. Intuitively, it should be, as we iterate over a regular/sized
            // grid. However, the underlying pixel is not forced to row-major iteration, so it might optimiser its
            // browing regarding tiling structure of source image.
            return SIZED + DISTINCT + IMMUTABLE + NONNULL;

            // TODO: in the future, the spliterator could be splitted by halfing some source extent dimension(s)
            // That would allow to provide parallelism whose all sub-spliterators would be sized (size of the orking extent).
            // If splitted on the most-likely independent dimensions (time or vertical axis), it would allow to load
            // independent source slices and provide low latency parallelism (I hope).
            //
            // + CONCURRENT + SUBSIZED;
        }
    }

    private static class GridCoverage2DRecordIterator implements Iterator<Feature> {

        private final GeometryFactory GF = JTS.getFactory();
        private final FeatureType recordType;
        private final GridCoverage coverage;
        private final String[] properties;
        private final PixelIterator geophysicPixelIterator;
        private final RenderedImage colorImg;
        private final ColorModel colorModel;
        private final PixelIterator coloredPixelIterator;
        private final MathTransform imageToCrs;
        private final CoordinateReferenceSystem crs;
        private final int crsDim;
        private final Envelope envelope;
        private Feature next = null;

        private GridCoverage2DRecordIterator(FeatureType recordType, GridCoverage coverage) {
            this(recordType, coverage, null);
        }

        private GridCoverage2DRecordIterator(FeatureType recordType, GridCoverage coverage, final GridExtent roi) {
            this.recordType = recordType;
            this.coverage = coverage.forConvertedValues(true);
            final RenderedImage samplesImage = this.coverage.forConvertedValues(true).render(roi);
            this.geophysicPixelIterator = new PixelIterator.Builder().create(samplesImage);
            this.colorImg = this.coverage.forConvertedValues(false).render(roi);
            this.colorModel = colorImg.getColorModel();
            this.coloredPixelIterator = new PixelIterator.Builder().create(colorImg);
            final GridGeometry gridGeometry = this.coverage.getGridGeometry();
            this.envelope = gridGeometry.getEnvelope();
            this.crs = gridGeometry.getCoordinateReferenceSystem();
            this.crsDim = crs.getCoordinateSystem().getDimension();
            this.imageToCrs = org.geotoolkit.internal.coverage.CoverageUtilities.getImageToCRS(gridGeometry, roi, samplesImage, PixelInCell.CELL_CENTER);

            //list properties
            final List<String> properties = new ArrayList<>();
            for (PropertyType pt : recordType.getProperties(true)) {
                if (pt instanceof AttributeType && !AttributeConvention.contains(pt.getName()) && !ATT_COLOR.equals(pt.getName().tip().toString())) {
                    properties.add(pt.getName().toString());
                }
            }
            this.properties = properties.toArray(new String[properties.size()]);
        }

        @Override
        public boolean hasNext() {
            findNext();
            return next != null;
        }

        @Override
        public Feature next() {
            findNext();
            if(next == null){
                throw new NoSuchElementException("No more features.");
            }
            final Feature candidate = next;
            next = null;
            return candidate;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void findNext(){
            if(next != null) return;

            while (next == null && geophysicPixelIterator.next()) {
                next = recordType.newInstance();
                //build geometry
                final int x = geophysicPixelIterator.getPosition().x;
                final int y = geophysicPixelIterator.getPosition().y;

                //extract color
                Color color = Color.WHITE;
                if (colorModel != null) {
                    coloredPixelIterator.moveTo(x, y);
                    Object dataElement = coloredPixelIterator.getDataElements(null);
                    try {
                        color = new Color(colorModel.getRGB(dataElement), true);
                    } catch (Throwable ex) {
                        color = Color.RED;
                    }
                }

                final double[] poly = new double[]{
                    x-0.5, y-0.5,
                    x+0.5, y-0.5,
                    x+0.5, y+0.5,
                    x-0.5, y+0.5,
                    x-0.5, y-0.5
                };
                final Polygon geom;
                try {
                    if (crsDim == 2) {
                        imageToCrs.transform(poly, 0, poly, 0, 5);
                        geom = GF.createPolygon(new LiteCoordinateSequence(poly));
                    } else {
                        //preserve all dimensions
                        final double[] crsPoly = new double[crsDim*5];
                        imageToCrs.transform(poly, 0, crsPoly, 0, 5);
                        CoordinateSequence lcs = new PackedCoordinateSequence.Double(crsPoly, crsDim, 0);
                        geom = GF.createPolygon(lcs);
                    }
                } catch (TransformException ex) {
                    throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
                }
                JTS.setCRS(geom, crs);

                next.setPropertyValue(AttributeConvention.GEOMETRY, geom);
                //read sample values
                for (int i=0;i<properties.length;i++) {
                    next.setPropertyValue(properties[i], geophysicPixelIterator.getSampleDouble(i));
                }

                next.setPropertyValue(ATT_COLOR, color);

                //if the CRS has more the 2 dimensions, we convert the envelope operation
                //has been converted to an attribute, the envelope will be N dimesion, and the geometry 2D
                if (envelope.getDimension() > 2) {
                    final GeneralEnvelope env = new GeneralEnvelope(envelope);
                    final org.locationtech.jts.geom.Envelope jtsEnv = geom.getEnvelopeInternal();
                    env.setRange(0, jtsEnv.getMinX(), jtsEnv.getMaxX());
                    env.setRange(1, jtsEnv.getMinY(), jtsEnv.getMaxY());
                    next.setPropertyValue(AttributeConvention.ENVELOPE_PROPERTY.toString(), env);
                }
            }
        }
    }
}
