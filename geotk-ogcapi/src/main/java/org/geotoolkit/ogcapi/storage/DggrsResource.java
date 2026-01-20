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
package org.geotoolkit.ogcapi.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.Array;
import org.apache.sis.geometries.math.NDArrays;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.dggs.s2.S2Dggrs;
import org.geotoolkit.ogcapi.client.dggs.DggsApi;
import org.geotoolkit.ogcapi.client.feature.FeatureApi;
import org.geotoolkit.ogcapi.model.common.CollectionDescription;
import org.geotoolkit.ogcapi.model.dggs.Dggrs;
import org.geotoolkit.ogcapi.model.dggs.DggrsData;
import org.geotoolkit.ogcapi.model.dggs.DggrsDataValue;
import org.geotoolkit.ogcapi.model.dggs.DggrsItem;
import org.geotoolkit.ogcapi.model.dggs.DggrsListResponse;
import org.geotoolkit.ogcapi.model.jsonschema.JSONSchema;
import org.geotoolkit.ogcapi.model.jsonschema.JSONSchemaProperty;
import org.geotoolkit.ogcapi.request.dggs.GetDggrs;
import org.geotoolkit.ogcapi.request.dggs.GetDggrsList;
import org.geotoolkit.ogcapi.request.dggs.GetZoneData;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridHierarchy;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystems;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.rs.Code;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.internal.shared.ArrayDiscreteGlobalGridCoverage;
import org.geotoolkit.storage.rs.CodeTransform;
import org.geotoolkit.storage.rs.CodedCoverage;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.CodedResource;
import org.geotoolkit.storage.rs.internal.shared.BandedCodeIterator;
import org.geotoolkit.storage.rs.internal.shared.WritableBandedCodeIterator;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggrsResource extends CollectionItemResource implements Aggregate {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.ogcapi");

    private static final ArrayDiscreteGlobalGridCoverage NO_CELL = new ArrayDiscreteGlobalGridCoverage(
            Names.createLocalName(null, null, "nocell"),
            new DiscreteGlobalGridGeometry(S2Dggrs.INSTANCE, List.of("0"), null),
            List.of());

    private final FeatureApi featureApi;
    private final DggsApi dggrsApi;
    private List<ForDGGRS> components;
    private String preferredFormat = "json";
    private boolean queryGzip = true;

    //cache
    private List<SampleDimension> sampleDimensions;
    private FeatureType type;
    private List<CodedGeometry> geometries;
    //tile cache
    private boolean disableCache = false;
    private final Cache<String,ArrayDiscreteGlobalGridCoverage> cache = new Cache<>(10, 2, true);

    public DggrsResource(CollectionResource parent, CollectionDescription description) {
        super(parent, description);
        final OpenApiConfiguration config = parent.api.getConfiguration();
        featureApi = new FeatureApi(config);
        dggrsApi = new DggsApi(config);

    }

    public void setPreferredFormat(String preferredFormat) {
        this.preferredFormat = preferredFormat;
    }

    public void setQueryGzip(boolean queryGzip) {
        this.queryGzip = queryGzip;
    }

    public void setDisableCache(boolean disableCache) {
        this.disableCache = disableCache;
    }

    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {
        if (components != null) return components;

        try {
            components = new ArrayList<>();

            //sort them by efficiency and availibility
            //we place H3 and S2 first because they are implemented in java
            final List<String> allKnownDggrs = new ArrayList<>(DiscreteGlobalGridReferenceSystems.listDggrs());
            if (allKnownDggrs.contains("H3")) {
                allKnownDggrs.remove("H3");
                allKnownDggrs.add(0, "H3");
            }
            if (allKnownDggrs.contains("S2")) {
                allKnownDggrs.remove("S2");
                allKnownDggrs.add(0, "S2");
            }

            final DggrsListResponse lst = dggrsApi.collectionGetDGGRSList(
                    new GetDggrsList().collectionId(description.getId()).format("json")).getData();

            for (DggrsItem item : lst.getDggrs()) {
                Dggrs dggrs = dggrsApi.collectionGetDGGRS(
                        new GetDggrs().collectionId(description.getId()).dggrsId(item.getId()).format("json")).getData();
                if (allKnownDggrs.contains(dggrs.getId())) {
                    components.add(new ForDGGRS(dggrs));
                }
            }
        } catch (ServiceException | FactoryException ex) {
            throw new DataStoreException(ex);
        }

        components = Collections.unmodifiableList(components);
        return components;
    }

    private synchronized FeatureType getSampleType() throws DataStoreException {
        if (type != null) return type;

        try {
            ServiceResponse<JSONSchema> jsonSchema = featureApi.collectinGetJsonSchema(description.getId(), null);
            final JSONSchema schema = jsonSchema.getData();

            sampleDimensions = new ArrayList<>();

            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(description.getId());
            for (Entry<String,JSONSchemaProperty> entry : schema.getProperties().entrySet()) {
                ftb.addAttribute(String.class).setName(entry.getKey());
                sampleDimensions.add(new SampleDimension.Builder().setName(entry.getKey()).build());
            }
            type = ftb.build();

        } catch (ServiceException ex) {
            throw new DataStoreException(ex);
        }

        return type;
    }

    private List<SampleDimension> getSampleDimensions() throws DataStoreException {
        getSampleType();
        return sampleDimensions;
    }

    private ArrayDiscreteGlobalGridCoverage getOrDownloadTile(DiscreteGlobalGridReferenceSystem dggrs, Object baseZoneId, Integer relativeDepth) {
        final String key = dggrs.getName().toString() +"_"+baseZoneId+"_"+relativeDepth;
        ArrayDiscreteGlobalGridCoverage value = cache.peek(key);
        if (value == null) {
            final Cache.Handler<ArrayDiscreteGlobalGridCoverage> handler = cache.lock(key);
            try {
                value = handler.peek();
                if (value == null) {
                    try {
                        value = downloadTile(dggrs, baseZoneId, relativeDepth);
                    } catch (TransformException | ServiceException ex) {
                        ex.printStackTrace();
                        value = NO_CELL;
                    }
                }
            } finally {
                handler.putAndUnlock(disableCache ? null : value);
            }
        }
        return value;
    }

    private ArrayDiscreteGlobalGridCoverage downloadTile(DiscreteGlobalGridReferenceSystem dggrs, Object baseZoneId, Integer relativeDepth) throws TransformException, ServiceException {
        if (baseZoneId == null || relativeDepth == null) {
            throw new IllegalArgumentException("Base zone and/or relative depth is null");
        }

        final String dggrsName = dggrs.getName().getCode();
        baseZoneId = dggrs.getGridSystem().getHierarchy().toTextIdentifier(baseZoneId);
        final ServiceResponse<DggrsData> response = dggrsApi.collectionGetDGGRSZoneData(
                new GetZoneData()
                        .dggrsId(dggrsName)
                        .zoneId(baseZoneId.toString())
                        .collectionId(description.getId())
                        .format(preferredFormat)
                        .zoneDepth(String.valueOf(relativeDepth))
                        .gzip(queryGzip));

        final DggrsData data = response.getData();
        final List<DggrsDataValue> values = data.getValues().values().iterator().next();

        final List<Array> samples = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            final List<Object> datas = values.get(0).getData();
            final double[] arr = new double[datas.size()];
            for (int k = 0; k <datas.size(); k++) {
                Object v = datas.get(k);
                if (v instanceof String s) {
                    try {
                        v = Double.valueOf(s);
                    } catch (NumberFormatException e) {
                        v = Double.NaN;
                    }
                }
                if (v instanceof Number n) {
                    arr[k] = n.doubleValue();
                } else {
                    arr[k] = Double.NaN;
                }
            }

            final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
            final Array array = NDArrays.of(ss, arr);
            samples.add(array);
        }

        final DiscreteGlobalGridGeometry g = DiscreteGlobalGridGeometry.forSubZone(dggrs, baseZoneId, relativeDepth);
        return new ArrayDiscreteGlobalGridCoverage(Names.createLocalName(null, null, description.getId()), g, samples);
    }

    private final class ForDGGRS extends AbstractResource implements CodedResource {

        private final Dggrs dggrsDesc;
        private final DiscreteGlobalGridReferenceSystem dggrs;
        private final CodedGeometry geometry;
        private final int maxRefinementLevel;
        private final int maxRelativeDepth;
        private final int bestRelativeDepth;

        private ForDGGRS(Dggrs dggrsDesc) throws FactoryException {
            super(DggrsResource.this);
            this.dggrsDesc = dggrsDesc;
            this.dggrs = DiscreteGlobalGridReferenceSystems.forCode(dggrsDesc.getId());
            this.geometry = new DiscreteGlobalGridGeometry(dggrs, null, null, null);
            this.maxRefinementLevel = dggrsDesc.getMaxRefinementLevel();
            this.maxRelativeDepth = dggrsDesc.getMaxRelativeDepth();

            //compute the best relative depth, to for no more then 100.000 zones at one time
            int refinementRatio = dggrs.getGridSystem().getRefinementRatio();
            int bestSubLevel = 1;
            int count = refinementRatio;
            while (count*refinementRatio < 100000) {
                bestSubLevel++;
                count *= refinementRatio;
            }
            if (bestSubLevel > maxRelativeDepth) {
                bestSubLevel = maxRelativeDepth;
            }
            bestRelativeDepth = bestSubLevel;
        }

        @Override
        public Optional<GenericName> getIdentifier() throws DataStoreException {
            return Optional.of(Names.createLocalName(null, null, description.getId() + " " + dggrsDesc.getId()));
        }

        @Override
        public FeatureType getSampleType() throws DataStoreException {
            return DggrsResource.this.getSampleType();
        }

        @Override
        public List<SampleDimension> getSampleDimensions() throws DataStoreException {
            return DggrsResource.this.getSampleDimensions();
        }

        @Override
        public CodedGeometry getGridGeometry() throws DataStoreException {
            return geometry;
        }

        @Override
        public CodedCoverage read(CodedGeometry queryGeometry, int... range) throws DataStoreException {
            getSampleType();

            final DiscreteGlobalGridGeometry dggrsGeometry = (DiscreteGlobalGridGeometry) queryGeometry;
            final DiscreteGlobalGridReferenceSystem dggrs = dggrsGeometry.getReferenceSystem();
            Object baseZoneId = dggrsGeometry.getBaseZoneId();
            Integer relativeDepth = dggrsGeometry.getRelativeDepth();

            if (baseZoneId != null && relativeDepth != null && relativeDepth <= maxRelativeDepth) {
                //we can map this request directly to a tile from the server
                final String tid = dggrs.getGridSystem().getHierarchy().toTextIdentifier(baseZoneId);
                final ArrayDiscreteGlobalGridCoverage coverage = getOrDownloadTile(dggrs, tid, relativeDepth);
                if (coverage == NO_CELL) throw new NoSuchDataException();
                return coverage;
            }

            //Read data from multiple tiles
            final List<Object> zones = dggrsGeometry.getZoneIds();
            final List<Array> samples = new ArrayList<>();
            final double[] nans = new double[sampleDimensions.size()];
            for (int i = 0; i < nans.length; i++) {
                final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
                samples.add(NDArrays.of(ss, new double[zones.size()]));
                nans[i] = Double.NaN;
            }

            final DiscreteGlobalGridHierarchy dggh = dggrs.getGridSystem().getHierarchy();
            final DiscreteGlobalGridGeometry dggrsGeom = new DiscreteGlobalGridGeometry(dggrs, zones, null);
            final ArrayDiscreteGlobalGridCoverage target = new ArrayDiscreteGlobalGridCoverage(getIdentifier().orElse(Names.createLocalName(null, null, "dggrs")), dggrsGeom, samples);

            final BandedCodeIterator ite = target.createIterator();
            final WritableBandedCodeIterator wite = target.createWritableIterator();

            final Stream<int[]> stream = Stream.generate(new Supplier<int[]>() {
                @Override
                public synchronized int[] get() {
                    ite.next();
                    return ite.getPosition();
                }
            });
            stream.limit(zones.size()).parallel().forEach(new Consumer<int[]>() {
                @Override
                public void accept(int[] gridPosition) {
                    try {
                        final Zone zone = dggh.getZone(zones.get(gridPosition[0]));
                        double[] values = getZoneValue(dggrs, zone);
                        if (values == null) {
                            values = nans;
                        }
                        synchronized (wite) {
                            wite.moveTo(gridPosition);
                            wite.setCell(values);
                        }
                    } catch (ServiceException | DataStoreException e) {
                        e.printStackTrace();
                    }
                }
            });
            wite.close();


//            try (final WritableBandedCodeIterator iterator = target.createWritableIterator()) {
//                while (iterator.next()) {
//                    final int[] gridPosition = iterator.getPosition();
//                    final Zone zone = dggh.getZone(zones.get(gridPosition[0]));
//                    double[] values = getZoneValue(dggrs, zone);
//                    if (values == null) {
//                        values = nans;
//                    }
//                    iterator.setCell(values);
//                }
//            } catch (ServiceException ex) {
//                throw new DataStoreException(ex);
//            }

            return target;
        }

        private double[] getZoneValue(DiscreteGlobalGridReferenceSystem dggrs, Zone zone) throws ServiceException, DataStoreException {

            final int zoneLevel = zone.getLocationType().getRefinementLevel();

            //find the level we will query and relative depth
            int relativeDepth = bestRelativeDepth;
            int queryLevel = zoneLevel - bestRelativeDepth;
            if (queryLevel > maxRefinementLevel) {
                queryLevel = maxRefinementLevel;
                relativeDepth = 0;
            } else if (queryLevel < 0) {
                int diff = -queryLevel;
                queryLevel = 0;
                relativeDepth -= diff;
            }
            int pickLevel = queryLevel + relativeDepth;

            //get the parent zone and the picked zone which may be at higher level
            Zone parentZone = zone.getFirstParent(queryLevel);
            Zone pickZone = (zoneLevel != pickLevel) ? zone.getFirstParent(pickLevel) : zone;

            final ArrayDiscreteGlobalGridCoverage tile = getOrDownloadTile(dggrs, parentZone, relativeDepth);
            if (tile == NO_CELL) {
                //if null at this point, the cell does not exist
                return null;
            }

            return getZoneValue(tile, pickZone);
        }

        private double[] getZoneValue(ArrayDiscreteGlobalGridCoverage tile, Zone zone) {
            final DiscreteGlobalGridGeometry cellGeometry = tile.getGeometry();
            final CodeTransform cellGridToRS = cellGeometry.getGridToRS();
            final BandedCodeIterator iterator = tile.createIterator();
            final Object[] ordinates = new Object[1];
            final Code address = new Code(dggrs, ordinates);
            try {
                ordinates[0] = zone.getIdentifier();
                iterator.moveTo(cellGridToRS.toGrid(address));
                return iterator.getCell((double[]) null);
            } catch (IllegalArgumentException | TransformException e) {
                return null;
            }
        }

    }

}
