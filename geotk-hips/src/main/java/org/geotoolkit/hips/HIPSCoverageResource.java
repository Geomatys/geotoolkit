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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometries.index.MortonIterator;
import org.apache.sis.measure.NumberRange;
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
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.internal.shared.RasterDiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.internal.shared.TiledDiscreteGlobalGridCoverageResource;
import org.geotoolkit.storage.rs.internal.shared.CodedCoverageAsFeatureSet;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class HIPSCoverageResource extends TiledDiscreteGlobalGridCoverageResource implements StoreResource {

    private static final RasterDiscreteGlobalGridCoverage NO_CELL = new RasterDiscreteGlobalGridCoverage(
            Names.createLocalName(null, null, "nocell"),
            DiscreteGlobalGridGeometry.unstructured(HealpixDggrs.INSTANCE, List.of("0"), null),
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
        super();
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
        return DiscreteGlobalGridGeometry.unstructured(HEALPIX, null, null);
    }

    @Override
    public FeatureType getSampleType() throws DataStoreException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(getIdentifier().get());
        CodedCoverageAsFeatureSet.toFeatureType(ftb, getSampleDimensions());
        return ftb.build();
    }

    @Override
    public NumberRange<Integer> getTileAvailableDepths() {
        try {
            getLongProperties();
        } catch (ServiceException ex) {
            throw new RuntimeException(ex);
        }
        return NumberRange.create(minOrder, true, maxOrder, true);
    }

    @Override
    public int getTileRelativeDepth() {
        try {
            getLongProperties();
        } catch (ServiceException ex) {
            throw new RuntimeException(ex);
        }
        return tileSubOrder;
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

        final DiscreteGlobalGridGeometry gridGeom = DiscreteGlobalGridGeometry.subZone(HEALPIX, zone.getIdentifier(), tileSubOrder);

        //map coordinates to cell index using a morton curve
        long start = zone.getFirstChildAtRelativeDepth(tileSubOrder).getLongIdentifier();
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

    @Override
    public RasterDiscreteGlobalGridCoverage getZoneTile(Object identifierOrZone) throws DataStoreException {
        System.out.println("GetZoneTile " + identifierOrZone);
        HealpixZone zone = (HealpixZone) HEALPIX.getGridSystem().getHierarchy().getZone(identifierOrZone);
        try {
            return cache.getOrCreate(zone.getGeographicIdentifier().toString(), () -> downloadCell(zone));
        } catch (Exception ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }
}
