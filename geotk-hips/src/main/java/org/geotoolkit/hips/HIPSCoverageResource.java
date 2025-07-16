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
package org.geotoolkit.hips;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometries.index.MortonIterator;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.dggs.healpix.HealpixDggrs;
import org.geotoolkit.dggs.healpix.HealpixZone;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.rs.internal.shared.DefaultCode;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.storage.dggs.internal.shared.ArrayDiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.internal.shared.RasterDiscreteGlobalGridCoverage;
import org.geotoolkit.storage.rs.CodeTransform;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.internal.shared.BandedCodeIterator;
import org.geotoolkit.storage.rs.internal.shared.CodedCoverageAsFeatureSet;
import org.geotoolkit.storage.rs.internal.shared.WritableBandedCodeIterator;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class HIPSCoverageResource extends AbstractResource implements DiscreteGlobalGridResource, StoreResource {

    private static RasterDiscreteGlobalGridCoverage NO_CELL = new RasterDiscreteGlobalGridCoverage(
            Names.createLocalName(null, null, "nocell"),
            new DiscreteGlobalGridGeometry(HealpixDggrs.INSTANCE, List.of("0"), null),
            List.of(new SampleDimension.Builder().setName(0).build()), BufferedImages.createImage(1, 1, 1, DataBuffer.TYPE_DOUBLE), null, null);

    private static final SampleDimension RED = new SampleDimension.Builder().setName("Red").build();
    private static final SampleDimension GREEN = new SampleDimension.Builder().setName("Green").build();
    private static final SampleDimension BLUE = new SampleDimension.Builder().setName("Blue").build();
    private static final SampleDimension ALPHA = new SampleDimension.Builder().setName("Alpha").build();

    private static final String PNG = "png";
    private static final String JPEG = "jpeg";
    private static final String WEBP = "webp";
    private static final String FITS = "fits";


    private static final HealpixDggrs HEALPIX = HealpixDggrs.INSTANCE;

    private final HIPSStore store;
    private final HIPSProperties shortProperties;
    private final HIPSService service;

    //cache interesting properties
    private HIPSProperties longProperties;
    private int minOrder;
    private int maxOrder;
    private int tileWidth;
    private int tileSubOrder;
    private List<String> tileFormats;
    private String bestFormat;
    private List<SampleDimension> sampleDimensions;

    //tile cache
    private final Cache<String,RasterDiscreteGlobalGridCoverage> cache = new Cache<>(10, 2, true);

    public HIPSCoverageResource(HIPSStore store, HIPSProperties shortProperties) throws ServiceException {
        super(store);
        this.store = store;
        this.shortProperties = shortProperties;

        String baseUri = String.valueOf(shortProperties.get(HIPSProperties.HIPS_SERVICE_URL));
        if (baseUri.endsWith("/")) baseUri = baseUri.substring(0, baseUri.length()-1);
        final ServiceConfiguration conf = ServiceConfiguration.builder()
                .updateBaseUri(baseUri)
                .build();
        service = new HIPSService(conf);
    }

    private synchronized HIPSProperties getLongProperties() throws ServiceException {
        if (longProperties == null) {
            longProperties = service.getHipsProperties().getData();
            tileWidth = Integer.parseInt((String) longProperties.get(HIPSProperties.HIPS_TILE_WIDTH));
            maxOrder = Integer.parseInt((String) longProperties.get(HIPSProperties.HIPS_ORDER));
            if (longProperties.containsKey(HIPSProperties.HIPS_ORDER_MIN)) {
                minOrder = Integer.parseInt((String) longProperties.get(HIPSProperties.HIPS_ORDER_MIN));
            } else {
                minOrder = 0;
            }
            final String str = (String) longProperties.get(HIPSProperties.HIPS_TILE_FORMAT);
            final String[] parts = str.split(" ");
            tileFormats = List.of(parts);

            if (tileFormats.contains(PNG)) {
                bestFormat = PNG;
                sampleDimensions = List.of(RED, GREEN, BLUE, ALPHA);
            } else if (tileFormats.contains(JPEG)) {
                bestFormat = JPEG;
                sampleDimensions = List.of(RED, GREEN, BLUE);
            } else if (tileFormats.contains(WEBP)) {
                bestFormat = WEBP;
                sampleDimensions = List.of(RED, GREEN, BLUE, ALPHA);
            } else {
                throw new IllegalArgumentException("No tile format supported");
            }
            tileSubOrder = (int) Math.round(Math.log(tileWidth) / Math.log(2));
        }
        return longProperties;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        String did = (String) shortProperties.get(HIPSProperties.CREATOR_DID);
        if (did == null) return Optional.empty();
        return Optional.of(Names.createLocalName(null, null, did));
    }

    @Override
    public DiscreteGlobalGridGeometry getGridGeometry() {
        return new DiscreteGlobalGridGeometry(HEALPIX, null, null);
    }

    @Override
    public FeatureType getSampleType() throws DataStoreException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(getIdentifier().get());
        CodedCoverageAsFeatureSet.toFeatureType(ftb, getSampleDimensions());
        return ftb.build();
    }

    @Override
    public NumberRange<Integer> getAvailableDepths() {
        try {
            getLongProperties();
        } catch (ServiceException ex) {
            throw new RuntimeException(ex);
        }
        return NumberRange.create(minOrder, true, maxOrder, true);
    }

    @Override
    public int getDefaultDepth() {
        try {
            getLongProperties();
        } catch (ServiceException ex) {
            throw new RuntimeException(ex);
        }
        return maxOrder;
    }

    @Override
    public int getMaxRelativeDepth() {
        try {
            getLongProperties();
        } catch (ServiceException ex) {
            throw new RuntimeException(ex);
        }
        return tileSubOrder;
    }

    @Override
    public DiscreteGlobalGridCoverage read(CodedGeometry grid, int... range) throws DataStoreException {
        final DiscreteGlobalGridGeometry geometry = DiscreteGlobalGridResource.toDiscreteGlobalGridGeometry(grid);
        try {
            getLongProperties();
        } catch (ServiceException ex) {
            throw new RuntimeException(ex);
        }

        final List<Object> zones = geometry.getZoneIds();
        final List<TupleArray> samples = new ArrayList<>();
        final double[] nans = new double[sampleDimensions.size()];
        for (int i = 0; i < nans.length; i++) {
            final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
            samples.add(TupleArrays.of(ss, new double[zones.size()]));
            nans[i] = Double.NaN;
        }

        final DiscreteGlobalGridGeometry dggrsGeom = new DiscreteGlobalGridGeometry(HEALPIX, zones, null);
        final ArrayDiscreteGlobalGridCoverage target = new ArrayDiscreteGlobalGridCoverage(getIdentifier().orElse(Names.createLocalName(null, null, "hips")), dggrsGeom, samples);
        try (final WritableBandedCodeIterator iterator = target.createWritableIterator()) {
            while (iterator.next()) {
                final int[] gridPosition = iterator.getPosition();
                final HealpixZone zone = (HealpixZone) HEALPIX.getGridSystem().getHierarchy().getZone(zones.get(gridPosition[0]));
                double[] values = getZoneValue(zone);
                if (values == null) {
                    values = nans;
                }
                iterator.setCell(values);
            }
        } catch (ServiceException ex) {
            throw new DataStoreException(ex);
        }

        return target;
    }

    private double[] getZoneValue(HealpixZone zone) throws ServiceException, DataStoreException {

        //find the parent tile to pick data from
        HealpixZone hipsCell = zone;
        int remaining = tileSubOrder;
        while (remaining > 0 && hipsCell.getLocationType().getRefinementLevel() > 0) {
            hipsCell = (HealpixZone) hipsCell.getParents().iterator().next();
            remaining--;
        }

        //ensure we do not go under the available range
        while (hipsCell.getOrder() > maxOrder) {
            //has only one parent
            hipsCell = (HealpixZone) hipsCell.getParents().iterator().next();
        }

        final int hipsCellPixelLevel = hipsCell.getLocationType().getRefinementLevel() + tileSubOrder;
        final int zoneLevel = zone.getLocationType().getRefinementLevel();
        final RasterDiscreteGlobalGridCoverage cell;
        final HealpixZone toDl = hipsCell;
        try {
            cell = cache.getOrCreate(toDl.getGeographicIdentifier().toString(), () -> downloadCell(toDl));
        } catch (Exception ex) {
            throw new DataStoreException(ex);
        }

        if (cell == NO_CELL) {
            //if null at this point, the cell does not exist
            return null;
        }

        if (hipsCellPixelLevel > zoneLevel) {
            //coverage is at a lower level, use a more accurate cell for picking
            for (int i = 0, n = hipsCellPixelLevel-zoneLevel; i < n; i++) {
                //pick first child at each level
                zone = (HealpixZone) zone.getChildren().iterator().next();
            }

        } else if (hipsCellPixelLevel < zoneLevel) {
            //coverage is at a higher level, use a less accurate cell for picking
            for (int i = 0, n = zoneLevel - hipsCellPixelLevel; i < n; i++) {
                //pick parent, only one in healpix
                zone = (HealpixZone) zone.getParents().iterator().next();
            }
        }

        final DiscreteGlobalGridGeometry cellGeometry = cell.getGeometry();
        final CodeTransform cellGridToRS = cellGeometry.getGridToRS();
        final BandedCodeIterator iterator = cell.createIterator();
        final Object[] ordinates = new Object[1];
        final DefaultCode address = new DefaultCode(HEALPIX, ordinates);
        try {
            ordinates[0] = zone.getIdentifier();
            iterator.moveTo(cellGridToRS.toGrid(address));
            return iterator.getCell((double[]) null);
        } catch (IllegalArgumentException | TransformException e) {
            return null;
        }
    }

    public RasterDiscreteGlobalGridCoverage downloadCell(HealpixZone zone) throws DataStoreException, ServiceException {
        if (zone.getOrder() > maxOrder) {
            throw new DataStoreException("Zone order is superior to maximum available :" + maxOrder);
        }
        String ext = bestFormat;
        if (bestFormat.equals(JPEG)) ext = "jpg";
        final ServiceResponse<byte[]> hipsCell;
        try {
            hipsCell = service.getHipsCell(zone.getOrder(), zone.getNpixel(), "." + ext);
        } catch (ServiceException ex) {
            if (ex.getCode() == 404) {
                //cell does not exist
                return NO_CELL;
            }
            throw ex;
        }

        final List<Object> zones = zone.getChildrenAtRelativeDepth(tileSubOrder).map(Zone::getIdentifier).toList();

        //map coordinates to cell index using a morton curve
        final DiscreteGlobalGridReferenceSystem.Coder coder = HEALPIX.createCoder();
        final long start;
        try {
            start = coder.decode(zones.get(0)).getLongIdentifier();
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }
        final Point[] offset = new Point[tileWidth*tileWidth];
        for (int x = 0; x < tileWidth; x++) {
            for (int y = 0; y < tileWidth; y++) {
                long idx = MortonIterator.index(y, x);
                offset[(int)idx] = new Point(x, y);
            }
        }

        final Function<Object,Point> zoneToGrid = new Function<Object, Point>() {
            @Override
            public Point apply(Object t) {
                if (t instanceof Long l) {
                    return offset[(int)(l - start)];
                } else {
                    final long l = Long.parseLong(t.toString());
                    return offset[(int)(l-start)];
                }
            }
        };
        final Function<Point,Object> gridToZone = new Function<Point,Object>() {
            @Override
            public Object apply(Point t) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        final byte[] data = hipsCell.getData();
        try {
            if (PNG.equals(bestFormat) || JPEG.equals(bestFormat) || WEBP.equals(bestFormat)) {
                final BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
                final DiscreteGlobalGridGeometry gridGeom = new DiscreteGlobalGridGeometry(HEALPIX, zones, null);
                return new RasterDiscreteGlobalGridCoverage(
                        getIdentifier().orElse(Names.createLocalName(null, null, "hips")),
                        gridGeom, sampleDimensions, image, zoneToGrid, gridToZone);
            } else {
                throw new DataStoreException("Format " + bestFormat + " not supported yet");
            }
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        try {
            getLongProperties();
        } catch (ServiceException ex) {
            throw new RuntimeException(ex);
        }
        return sampleDimensions;
    }

    @Override
    public DataStore getOriginator() {
        return store;
    }


}
