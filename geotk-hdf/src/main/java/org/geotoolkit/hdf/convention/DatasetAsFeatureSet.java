/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf.convention;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.measure.Unit;
import ucar.nc2.constants.CF;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.hdf.HDF5Provider;
import org.geotoolkit.hdf.HDF5Store;
import org.geotoolkit.hdf.api.Dataset;
import org.geotoolkit.hdf.datatype.Compound;
import org.geotoolkit.hdf.datatype.Compound.Member;
import org.geotoolkit.hdf.datatype.DataType;
import org.geotoolkit.hdf.message.DataspaceMessage;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * View a HDF Dataset as a FeatureSet.
 * Each element of the dataset becomes a feature, identified by its index in the dataspace.
 * Compound datatype members become feature attributes, and datasets listed in the CF
 * {@code coordinates} attribute (limited to longitude and latitude) are combined into a point geometry.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DatasetAsFeatureSet extends AbstractFeatureSet implements StoreResource{

    private static final GeometryFactory GF = new GeometryFactory();
    private static final String HDF_INDEX = "hdf-index";

    private final HDF5Store store;
    private final Dataset dataset;

    //featureset
    private FeatureType featureType;
    private boolean is1D;
    private boolean isEmpty;
    private final Map<String,Function<Object, Instant>> timeAttributes = new HashMap<>();
    private double scaleFactor = 1.0;
    private Class valueClass;

    //geometry type info
    private CoordinateReferenceSystem crs;
    private Dataset[] coordinates;

    /**
     * Creates a feature set view of the given dataset.
     *
     * @param store store owning the dataset.
     * @param dataset dataset to expose as features.
     */
    public DatasetAsFeatureSet(HDF5Store store, Dataset dataset) {
        super(null, false);
        this.store = store;
        this.dataset = dataset;
    }

    /**
     * Returns the dataset viewed as a feature set.
     *
     * @return the dataset viewed as a feature set
     */
    public Dataset getDataset() {
        return dataset;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        getType();
        if (crs != null) {
            final GeneralEnvelope env = new GeneralEnvelope(crs);
            for (int i = 0; i < coordinates.length; i++) {
                final NumberRange range = coordinates[i].getValueRange();
                final double scale = getScale(coordinates[i]);
                env.setRange(i, range.getMinDouble(true) * scale, range.getMaxDouble(true) * scale);
            }
            return Optional.of(env);
        }
        return Optional.empty();
    }

    @Override
    public synchronized FeatureType getType() throws DataStoreException {
        if (featureType != null) return featureType;

        final DataType datatype = dataset.getDataType();
        final DataspaceMessage dataspace = dataset.getDataspace();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(dataset.getIdentifier().orElseThrow(() -> new IllegalStateException("Dataset name should never be null")));

        valueClass = datatype.getValueClass();

        //check if we have a CF scale factor
        this.scaleFactor = getScale(dataset);
        if (this.scaleFactor != 1.0) {
            valueClass = double.class;
        }

        //check if we have CF related coordinates
        final String coordinates = (String) dataset.getAttributes().getOrDefault(CF.COORDINATES, null);
        if (coordinates != null) {
            final String[] parts = coordinates.split(" ");


            this.coordinates = new Dataset[parts.length];
            for (int i = 0; i < parts.length; i++) {
                this.coordinates[i] = (Dataset) dataset.getParent().resolve(parts[i].trim());
            }

            if (parts.length == 2) {
                if (CF.LONGITUDE.equals(this.coordinates[0].getName())) {
                    crs = CommonCRS.WGS84.normalizedGeographic();
                } else {
                    crs = CommonCRS.WGS84.geographic();
                }
                crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.POSITIVE_RANGE);

                ftb.addAttribute(Point.class).setName(CF.COORDINATES).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
            } else {
                HDF5Provider.LOGGER.log(Level.WARNING, "Dataset " + dataset.getName() +" has " + parts.length + "dimensions, it is not supported yet");
                this.coordinates = null;
            }
        }

        final int[] dimensionSizes = dataspace.getDimensionSizes();
        if (dimensionSizes == null || dimensionSizes.length < 1) {
            isEmpty = true;
            is1D = true;
        } else {
            for (int i = 0; i < dimensionSizes.length; i++) {
                if (dimensionSizes[i] == 0) {
                    isEmpty = true;
                    break;
                }
            }
            is1D = dimensionSizes.length == 1;
        }

        if (is1D) {
            ftb.addAttribute(Long.class).setName(HDF_INDEX).addRole(AttributeRole.IDENTIFIER_COMPONENT);
        } else {
            ftb.addAttribute(long[].class).setName(HDF_INDEX).addRole(AttributeRole.IDENTIFIER_COMPONENT);
        }

        if (datatype instanceof Compound cmp) {
            for (Member member : cmp.members) {
                final Entry<Unit,CoordinateReferenceSystem> unitAndCrs;
                try {
                    unitAndCrs = dataset.getAttributeUnit(member.name);
                } catch (FactoryException ex) {
                    throw new DataStoreException("Failed to parse unit and crs.", ex);
                }
                final String description = dataset.getAttributeDescription(member.name);

                final AttributeTypeBuilder atb = ftb.addAttribute(member.memberType.getValueClass()).setName(member.name);
                if (description != null) {
                    atb.setDescription(description);
                }
                if (unitAndCrs != null) {
                    atb.setUnit(unitAndCrs.getKey());
                    if (unitAndCrs.getValue() != null) {
                        CoordinateReferenceSystem crs = unitAndCrs.getValue();
                        atb.setCRS(crs);
                        if (crs instanceof TemporalCRS tcrs) {
                            atb.setValueClass(Instant.class);

                            final var defaultTemporalCrs = DefaultTemporalCRS.castOrCopy(tcrs);
                            final Function<Object, Instant> converter = obj -> {
                                if (obj == null) return null;
                                else if (obj instanceof Number value) {
                                    double decimalValue = value.doubleValue();
                                    return defaultTemporalCrs.toInstant(decimalValue);
                                } else {
                                    throw new IllegalStateException("Input value is not a number, but a "+obj.getClass().getCanonicalName());
                                }
                            };
                            timeAttributes.put(member.name, converter);
                        }
                    }
                }
            }
        } else {
            ftb.addAttribute(valueClass).setName("value");
        }
        featureType = ftb.build();

        return featureType;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        if (isEmpty) return Stream.empty();

        //limited to 1D dataset
        final int[] dimensionSizes = dataset.getDataspace().getDimensionSizes();
        final GridExtent area = dataset.getDataspace().getDimensionExtent();

        //read by blocks of 1000
        final int blockSize = 1000;
        final long[] sub = new long[dimensionSizes.length];
        Arrays.fill(sub, blockSize);
        final GridExtent points = area.subsample(sub);

        Stream<GridExtent> streamExtent = points.latticePointStream(parallel).map(new Function<long[],GridExtent>() {
            @Override
            public GridExtent apply(long[] value) {
                final long[] low = new long[dimensionSizes.length];
                final long[] high = new long[dimensionSizes.length];
                for (int i=0;i<low.length;i++) {
                    low[i] = value[i] * blockSize;
                    high[i] = (value[i]+1) * blockSize -1; //inclusive
                }
                //ensure we do not got out of data range
                return area.intersect(new GridExtent(null, low, high, true));
            }
        });

        return streamExtent.flatMap(new Function<GridExtent, Stream<Feature>>() {
            @Override
            public Stream<Feature> apply(GridExtent t) {
                try {
                    return toFeatures(t);
                } catch (IOException | DataStoreException | TransformException ex) {
                    throw new BackingStoreException(ex.getMessage(), ex);
                }
            }
        });
    }

    private Stream<Feature> toFeatures(GridExtent extent) throws DataStoreException, IOException, TransformException {
        final FeatureType type = getType();
        final Object rawData = readScaled(dataset, extent);

        //read coordinates
        Object coord0 = null;
        Object coord1 = null;
        if (crs != null) {
            coord0 = readScaled(coordinates[0],extent);
            coord1 = readScaled(coordinates[1],extent);
        }

        final long[] low = extent.getLow().getCoordinateValues();
        final List<Feature> features = new ArrayList<>();

        final Iterator<long[]> iterator = extent.latticePointStream(false).iterator();
        while (iterator.hasNext()) {
            final long[] location = iterator.next();
            Object rawRecord = Array.get(rawData, Math.toIntExact(location[0] - low[0]));
            if (!is1D) {
                for (int i = 1; i < low.length; i++) {
                    rawRecord = Array.get(rawRecord, Math.toIntExact(location[i] - low[i]));
                }
            }

            final Feature feature = type.newInstance();
            if (is1D) {
                feature.setPropertyValue(HDF_INDEX, location[0]);
            } else {
                feature.setPropertyValue(HDF_INDEX, location);
            }
            if (dataset.getDataType() instanceof Compound cmp) {
                for (int k = 0; k < cmp.members.length; k++) {
                    final String name = cmp.members[k].name;
                    Object value = Array.get(rawRecord, k);
                    if (value != null) {
                        Function<Object, Instant> converter = timeAttributes.get(name);
                        if (converter != null) value = converter.apply(value);
                    }
                    feature.setPropertyValue(name, value);
                }
            } else {
                feature.setPropertyValue("value", rawRecord);
            }

            //rebuild geometry
            if (crs != null) {
                Number x = (Number) Array.get(coord0, Math.toIntExact(location[0] - low[0]));
                Number y = (Number) Array.get(coord1, Math.toIntExact(location[0] - low[0]));
                final Point pt = GF.createPoint(new CoordinateXY(x.doubleValue(), y.doubleValue()));
                pt.setUserData(crs);
                feature.setPropertyValue(CF.COORDINATES, pt);
            }


            features.add(feature);
        }

        return features.stream();
    }

    @Override
    public DataStore getOriginator() {
        return store;
    }

    @Override
    public Optional<FileSet> getFileSet() throws DataStoreException {
        return store.getFileSet();
    }

    private static Object readScaled(Dataset dataset, GridExtent extent, int ... compoundindexes) throws IOException, DataStoreException {
        Object data = dataset.read(extent, compoundindexes);
        final double scaleFactor = getScale(dataset);
        if (scaleFactor != 1.0) {
            double[] array = new double[Array.getLength(data)];
            for (int i = 0; i < array.length; i++) {
                array[i] = ((Number)Array.get(data, i)).doubleValue() * scaleFactor;
            }
            data = array;
        }

        return data;
    }

    private static double getScale(Dataset dataset) {
        //check if we have a CF scale factor
        double scaleFactor = 1.0;
        final Object cdt = dataset.getAttributes().getOrDefault(CF.SCALE_FACTOR, null);
        if (cdt instanceof Number n) {
            scaleFactor = n.doubleValue();
        } else if (cdt != null && cdt.getClass().isArray()) {
            final Object av = Array.get(cdt, 0);
            if (av instanceof Number n) {
                scaleFactor = n.doubleValue();
            }
        }
        return scaleFactor;
    }
}
