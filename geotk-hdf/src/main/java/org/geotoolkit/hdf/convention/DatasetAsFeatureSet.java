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
import java.util.function.Function;
import java.util.stream.Stream;
import javax.measure.Unit;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.hdf.api.Dataset;
import org.geotoolkit.hdf.datatype.Compound;
import org.geotoolkit.hdf.datatype.Compound.Member;
import org.geotoolkit.hdf.datatype.DataType;
import org.geotoolkit.hdf.message.DataspaceMessage;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * View a HDF Dataset as a FeatureSet.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DatasetAsFeatureSet extends AbstractFeatureSet {

    private static final String HDF_INDEX = "hdf-index";

    private final Dataset dataset;

    //featureset
    private FeatureType featureType;
    private boolean is1D;
    private boolean isEmpty;
    private final Map<String,MathTransform1D> timeAttributes = new HashMap<>();

    public DatasetAsFeatureSet(Dataset dataset) {
        super(null, false);
        this.dataset = dataset;
    }

    public Dataset getDataset() {
        return dataset;
    }

    @Override
    public synchronized FeatureType getType() throws DataStoreException {
        final DataType datatype = dataset.getDataType();
        final DataspaceMessage dataspace = dataset.getDataspace();
        if (featureType == null) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(dataset.getIdentifier().get());

            final int[] dimensionSizes = dataspace.getDimensionSizes();
            if (dimensionSizes == null) {
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
                        throw new DataStoreException("Failed to parse unit and crs.\n" + ex.getMessage(), ex);
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
                            if (crs instanceof TemporalCRS) {
                                atb.setValueClass(Instant.class);
                                final MathTransform1D mt;
                                try {
                                    mt = (MathTransform1D) CRS.findOperation(crs, CommonCRS.Temporal.JAVA.crs(), null).getMathTransform();
                                } catch (FactoryException ex) {
                                    throw new DataStoreException(ex);
                                }
                                timeAttributes.put(member.name, mt);
                            }
                        }
                    }
                }
            } else {
                ftb.addAttribute(datatype.getValueClass()).setName("value");
            }
            featureType = ftb.build();
        }
        return featureType;
    }

    @Override
    public Stream<Feature> features(boolean bln) throws DataStoreException {
        if (isEmpty) return Stream.empty();

        //limited to 1D dataset
        final int[] dimensionSizes = dataset.getDataspace().getDimensionSizes();
        final GridExtent area = dataset.getDataspace().getDimensionExtent();

        //read by blocks of 1000
        final int blockSize = 1000;
        final int[] sub = new int[dimensionSizes.length];
        Arrays.fill(sub, blockSize);
        final GridExtent points = area.subsample(sub);

        Stream<GridExtent> streamExtent = TileMatrices.pointStream(points).map(new Function<long[],GridExtent>() {
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
        final Object rawData = dataset.read(extent);

        final long[] low = extent.getLow().getCoordinateValues();
        final List<Feature> features = new ArrayList<>();

        final Iterator<long[]> iterator = TileMatrices.pointStream(extent).iterator();
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
                    MathTransform1D trs1d = timeAttributes.get(name);
                    if (trs1d != null) {
                        //convert value to Instant
                        final double timeMs = trs1d.transform(((Number) value).doubleValue());
                        value = Instant.ofEpochMilli((long) timeMs);
                    }
                    feature.setPropertyValue(name, value);
                }
            } else {
                feature.setPropertyValue("value", rawRecord);
            }
            features.add(feature);
        }

        return features.stream();
    }

}
