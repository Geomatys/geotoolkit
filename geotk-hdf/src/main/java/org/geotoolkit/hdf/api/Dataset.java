/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.hdf.api;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import static java.util.Collections.singletonMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.measure.Unit;
import javax.measure.format.ParserException;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.io.ChannelDataInput;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.cs.DefaultTimeCS;
import org.apache.sis.referencing.datum.DefaultTemporalDatum;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.netcdf.AttributeNames;
import org.apache.sis.util.ArraysExt;
import static org.apache.sis.util.ArraysExt.swap;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.iso.Names;
import org.apache.sis.util.resources.Vocabulary;
import org.geotoolkit.hdf.ObjectHeader;
import org.geotoolkit.hdf.SymbolTableEntry;
import org.geotoolkit.hdf.btree.BTreeV1;
import org.geotoolkit.hdf.btree.BTreeV1Chunk;
import org.geotoolkit.hdf.datatype.Compound;
import org.geotoolkit.hdf.datatype.Compound.Member;
import org.geotoolkit.hdf.datatype.DataType;
import org.geotoolkit.hdf.heap.LocalHeap;
import org.geotoolkit.hdf.io.ChunkSeekableByteChannel;
import org.geotoolkit.hdf.io.Connector;
import org.geotoolkit.hdf.io.HDF5ChannelDataInput;
import org.geotoolkit.hdf.io.HDF5DataInput;
import org.geotoolkit.hdf.message.AttributeMessage;
import org.geotoolkit.hdf.message.BogusMessage;
import org.geotoolkit.hdf.message.DataLayoutMessage;
import org.geotoolkit.hdf.message.DataspaceMessage;
import org.geotoolkit.hdf.message.DatatypeMessage;
import org.geotoolkit.hdf.message.FillValueMessage;
import org.geotoolkit.hdf.message.FilterPipelineMessage;
import org.geotoolkit.hdf.message.FilterPipelineMessage.FilterDescription;
import org.geotoolkit.hdf.message.Message;
import org.geotoolkit.hdf.message.NillMessage;
import org.geotoolkit.hdf.message.ObjectHeaderContinuationMessage;
import org.geotoolkit.hdf.message.ObjectModificationTimeMessage;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * A dataset is an object header that contains messages that describe the
 * datatype, dataspace, layout, filters, external files, fill value, and other
 * elements with the layout message pointing to either a raw data chunk or to
 * a B-tree that points to raw data chunks.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Dataset extends AbstractResource implements Node, FeatureSet {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.hdf");
    private static final String HDF_INDEX = "hdf-index";

    private final Group parent;
    private final Connector connector;

    private final String name;
    private final GenericName genericName;
    private final BTreeV1 btree;
    private final LocalHeap localHeap;

    //parsed values
    private final Map<String,Object> attributes = new HashMap<>();
    private DataType datatype;
    private DataspaceMessage dataspace;
    private FillValueMessage fillValue;
    private DataLayoutMessage.Layout layout;
    private FilterPipelineMessage filter;

    //decoding informations
    private final int cellByteSize;
    private List<BTreeV1Chunk> chunks = null;

    //featureset
    private FeatureType featureType;
    private boolean is1D;
    private boolean isEmpty;
    private final Map<String,MathTransform1D> timeAttributes = new HashMap<>();

    public Dataset(Group parent, Connector connector, SymbolTableEntry entry, String name) throws IOException, DataStoreException {
        super(null, false);
        this.parent = parent;
        this.connector = connector;
        this.name = name;

        final ObjectHeader header;
        try (final HDF5DataInput channel = connector.createChannel()) {
            header = entry.getHeader(channel);
            btree = entry.getBTree(channel).orElse(null);
            localHeap = entry.getLocalHeap(channel).orElse(null);
        }

        //extract properties
        for (Message message : header.getMessages()) {
            if (message instanceof AttributeMessage cdt) {
                attributes.put(cdt.getName(), cdt.getValue());
            } else if (message instanceof DataspaceMessage cdt) {
                dataspace = cdt;
            } else if (message instanceof DatatypeMessage cdt) {
                datatype = cdt.getDataType();
            } else if (message instanceof FillValueMessage cdt) {
                fillValue = cdt;
            } else if (message instanceof FilterPipelineMessage cdt) {
                filter = cdt;
            } else if (message instanceof DataLayoutMessage cdt) {
                layout = cdt.getLayout();
            } else if (message instanceof NillMessage
                    || message instanceof BogusMessage
                    || message instanceof ObjectModificationTimeMessage
                    || message instanceof ObjectHeaderContinuationMessage) {
                //ignore those
            } else {
                throw new IOException("Unhandled message " + message.getClass().getSimpleName());
            }
        }

        cellByteSize = datatype.getByteSize();
        genericName = Node.createName(this);
    }

    private static long[] buildCellSizes(int cellByteSize, int[] dimensionSizes){
        ArraysExt.reverse(dimensionSizes);
        long[] dimensionByteSize = new long[dimensionSizes.length];
        if (dimensionSizes.length > 0){
            dimensionByteSize[0] = cellByteSize;
            for (int i = 1; i < dimensionSizes.length; i++) {
                dimensionByteSize[i] = dimensionByteSize[i-1] * dimensionSizes[i-1];
            }
            reverse(dimensionByteSize);
        }
        return dimensionByteSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Group getParent() {
        return parent;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(genericName);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Extract attribute descrption concatenating CF standard_name and long_name.
     * @param attributeName, null if datatype is not compund.
     * @return
     */
    public String getAttributeDescription(String attributeName) {
        final Object standardName = attributes.get(attributeName == null ? AttributeNames.STANDARD_NAME : attributeName + ":" + AttributeNames.STANDARD_NAME);
        final Object longName = attributes.get(attributeName == null ? "long_name" : attributeName + ":long_name");
        final StringBuilder description = new StringBuilder();
        if (standardName != null) {
            description.append(standardName);
        }
        if (longName != null) {
            if (!description.isEmpty()) {
                description.append(" : ");
            }
            description.append(longName);
        }
        if (!description.isEmpty()) {
            return description.toString();
        }
        return null;
    }

    public Entry<Unit,CoordinateReferenceSystem> getAttributeUnit(String attributeName) {
        final Object units = attributes.get(attributeName + ":units");
        final String unitStr = String.valueOf(units);
        Unit unit = null;
        CoordinateReferenceSystem crs = null;
        try {
            unit = Units.valueOf(unitStr);
        } catch (ParserException ex) {
            int idx = unitStr.indexOf("since");
            if (idx > 0) {
                try {
                    final String baseUnitStr = unitStr.substring(0, idx).trim();
                    unit = Units.valueOf(baseUnitStr);
                    final String originStr = unitStr.substring(idx+5).trim();
                    final Calendar epoch = TemporalUtilities.parseDateCal(originStr);

                    final Map<String,?> props = singletonMap(NAME_KEY, new NamedIdentifier(null, "attributeName"));
                    final TemporalDatum datum = new DefaultTemporalDatum(props, epoch.getTime());
                    final Map<String,Object> properties = new HashMap<>();
                    properties.put(TemporalCRS.IDENTIFIERS_KEY, new NamedIdentifier(Names.createLocalName(null, null, attributeName)));
                    final Map<String,?> cs   = singletonMap(NAME_KEY, new NamedIdentifier(null, Vocabulary.formatInternational(Vocabulary.Keys.Temporal)));
                    final Map<String,?> axis = singletonMap(NAME_KEY, new NamedIdentifier(null, Vocabulary.formatInternational(Vocabulary.Keys.Time)));
                    final DefaultTimeCS timeCs = new DefaultTimeCS(cs, new DefaultCoordinateSystemAxis(axis, "t", AxisDirection.FUTURE, unit));
                    crs = new DefaultTemporalCRS(properties, datum, timeCs);

                } catch (ParserException | ParseException e) {
                    LOGGER.log(Level.WARNING, "Failed to parse unit {0}", unitStr);
                }
            } else {
                LOGGER.log(Level.WARNING, "Failed to parse unit {0}", unitStr);
            }
        }
        if (unit != null) {
            return new AbstractMap.SimpleImmutableEntry<>(unit, crs);
        }
        return null;
    }

    @Override
    public synchronized FeatureType getType() throws DataStoreException {
        if (featureType == null) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(getIdentifier().get());

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
                    final Entry<Unit,CoordinateReferenceSystem> unitAndCrs = getAttributeUnit(member.name);
                    final String description = getAttributeDescription(member.name);

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
        final int[] dimensionSizes = getDataspace().getDimensionSizes();
        final GridExtent area = getDataspace().getDimensionExtent();

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
        final Object rawData = read(extent);

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
            if (datatype instanceof Compound cmp) {
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

    public DataType getDataType() {
        return datatype;
    }

    public DataspaceMessage getDataspace() {
        return dataspace;
    }

    public FillValueMessage getFillValue() {
        return fillValue;
    }

    public DataLayoutMessage.Layout getLayout() {
        return layout;
    }

    /**
     * Currently, only datasets using chunked data storage use the filter
     * pipeline on their raw data.
     */
    public FilterPipelineMessage getFilter() {
        return filter;
    }

    public Object read(GridExtent extent, int ... compoundindexes) throws IOException, DataStoreException {

        //build read extent
        final int[] dimensionSizes = dataspace.getDimensionSizes();
        if (extent == null) {
            extent = dataspace.getDimensionExtent();
        } else if (!dataspace.getDimensionExtent().intersect(extent).equals(extent)){
            throw new DataStoreException("Requested extent " + extent + " is not contained in " + dataspace.getDimensionExtent());
        }

        //build channel
        try (HDF5DataInput channel = connector.createChannel()) {

            //build the fill value
            byte[] fill = new byte[1];
            if (fillValue != null && fillValue.getFillValue() != null && fillValue.getFillValue().length > 0) {
                fill = fillValue.getFillValue();
            }

            if (dimensionSizes.length == 0) {
                //scalar value
                if (layout instanceof DataLayoutMessage.Compact cdt) {
                    channel.seek(cdt.rawDataAddress);
                } else if (layout instanceof DataLayoutMessage.Contiguous cdt) {
                    channel.seek(cdt.address);
                } else if (layout instanceof DataLayoutMessage.Chunked cdt) {
                    throw new DataStoreException("Using chunked layout for a scalar value is not supported");
                }
                return datatype.readData(channel, compoundindexes);
            } else {
                final List<BTreeV1Chunk> chunks;
                final List<FilterDescription> filters = new ArrayList<>();

                if (layout instanceof DataLayoutMessage.Compact cdt) {
                    final BTreeV1Chunk fakeChunk = new BTreeV1Chunk();
                    fakeChunk.address = cdt.rawDataAddress;
                    fakeChunk.uncompressedSize = cdt.size;
                    fakeChunk.size = cdt.size;
                    fakeChunk.filterMask = 255;
                    fakeChunk.offset = getDataspace().getDimensionExtent();
                    chunks = Arrays.asList(fakeChunk);

                } else if (layout instanceof DataLayoutMessage.Contiguous cdt) {
                    final BTreeV1Chunk fakeChunk = new BTreeV1Chunk();
                    fakeChunk.address = cdt.address;
                    fakeChunk.uncompressedSize = cdt.size;
                    fakeChunk.size = cdt.size;
                    fakeChunk.filterMask = 255;
                    fakeChunk.offset = getDataspace().getDimensionExtent();
                    chunks = Arrays.asList(fakeChunk);

                } else if (layout instanceof DataLayoutMessage.Chunked cdt) {

                    if (connector.isUndefinedLength(cdt.address)) {
                        chunks = Collections.EMPTY_LIST;
                    } else {

                        //build filters
                        if (filter != null) {
                            filters.addAll(filter.getFilters());
                        }

                        //read btree of all chunks
                        chunks = getChunks(channel);
                    }

                } else if (layout instanceof DataLayoutMessage.Virtual cdt) {
                    throw new IOException("Not supported layout " + layout.getClass().getSimpleName());
                } else {
                    throw new IOException("Unexpected layout " + layout.getClass().getSimpleName());
                }

                //read datas
                final int[] dimensions = new int[extent.getDimension()];
                for (int i = 0; i< dimensions.length; i++) {
                    dimensions[i] = (int) extent.getSize(i);
                }
                final Object results = java.lang.reflect.Array.newInstance(datatype.getValueClass(), dimensions);

                for (BTreeV1Chunk chunk : chunks) {
                    appendChunkDatas(results, () -> {
                        channel.seek(chunk.address);
                        if (chunk.filterMask == 255) {
                            return channel;
                        } else {
                            final ChunkSeekableByteChannel chunkChannel = new ChunkSeekableByteChannel(chunk, channel, filters);
                            return new HDF5ChannelDataInput(new ChannelDataInput("", chunkChannel, ByteBuffer.allocate(4096), false));
                        }
                    }, chunk.offset, extent, compoundindexes);
                }
                return results;
            }
        }
    }

    private synchronized List<BTreeV1Chunk> getChunks(HDF5DataInput channel) throws IOException {
        if (chunks == null) {
            final DataLayoutMessage.Chunked cdt = (DataLayoutMessage.Chunked) layout;
            //read btree of all chunks
            channel.seek(cdt.address);
            final BTreeV1 datatree = new BTreeV1();
            datatree.read(channel, cdt.dimensionSizes);
            final BTreeV1.DataNode node = (BTreeV1.DataNode) datatree.root;
            chunks = new ArrayList<>(node.getChunks());
        }

        return chunks;
    }

    private void appendChunkDatas(Object results, Callable<HDF5DataInput> chunkChannel,
            GridExtent chunkExtent, GridExtent queryExtent, int ... compoundindexes) throws IOException {

        final GridExtent intersection = chunkExtent.intersect(queryExtent);
        final long[] chunkTranslate = chunkExtent.getLow().getCoordinateValues();

        final long[] intersectionlow = intersection.getLow().getCoordinateValues();
        final int[] dimensions = new int[intersectionlow.length];
        final int[] fulldimensions = new int[intersectionlow.length];
        final long[] intersectionToQueryTranslate = new long[intersectionlow.length];
        for (int i = 0; i< dimensions.length; i++) {
            dimensions[i] = (int) intersection.getSize(i);
            if (dimensions[i] <= 0) return;
            fulldimensions[i] = (int) chunkExtent.getSize(i);
            intersectionToQueryTranslate[i] = intersectionlow[i] - queryExtent.getLow(i);
        }
        HDF5DataInput dfi = null;
        try {
            dfi = chunkChannel.call();
            Object chunkDatas = readChunkDatas(dfi, buildCellSizes(cellByteSize, fulldimensions), chunkTranslate, intersectionlow, dimensions, 0, compoundindexes);
            //copy datas in result array
            copy(chunkDatas, results, intersectionToQueryTranslate);
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            //close channel only if it's a chunk
            if (dfi instanceof ChunkSeekableByteChannel csbc) {
                csbc.close();
            }
        }
    }

    private Object readChunkDatas(HDF5DataInput chunkChannel, final long[] dimensionByteSize, final long[] chunkLow, final long[] intersectionLow, final int[] intersectionSize, int dimIdx, int ... compoundindexes) throws IOException {
        final long basePosition = chunkChannel.getStreamPosition();
        Object values;
        if (dimIdx == intersectionLow.length - 1) {
            //a strip
            chunkChannel.seek(basePosition + (cellByteSize * (intersectionLow[dimIdx] - chunkLow[dimIdx])));
            values = datatype.readData(chunkChannel, intersectionSize[dimIdx], compoundindexes);
        } else {
            //an array of strips
            final int[] sizes = new int[intersectionSize.length - dimIdx];
            sizes[0] = intersectionSize[dimIdx];
            values = java.lang.reflect.Array.newInstance(datatype.getValueClass(), sizes);
            for (int k = 0; k < intersectionSize[dimIdx]; k++) {
                chunkChannel.seek(basePosition + ((intersectionLow[dimIdx] - chunkLow[dimIdx]) + k) * dimensionByteSize[dimIdx]);
                final Object strip = readChunkDatas(chunkChannel, dimensionByteSize, chunkLow, intersectionLow, intersectionSize, dimIdx+1, compoundindexes);
                java.lang.reflect.Array.set(values, k, strip);
            }
        }
        return values;
    }

    private static void copy(Object source, Object target, long[] offset) {

        if (offset.length == 1) {
            int length = Array.getLength(source);
            if (length == 0) return;
            System.arraycopy(source, 0, target, (int) offset[0], length);
        } else {
            int nb = Array.getLength(source);
            final long[] subOffset = Arrays.copyOfRange(offset, 1, offset.length);
            for (int i = 0; i < nb; i++) {
                final Object subSource = Array.get(source, i);
                final Object subTarget = Array.get(target, (int) (i + offset[0]));
                copy(subSource, subTarget,subOffset);
            }
        }
    }

    @Override
    public String toString() {
        final String name = getName();
        final StringBuilder sb = new StringBuilder(name);
        sb.append("[HDF5-Dataset]");
        for (Map.Entry entry : getAttributes().entrySet()) {
            Object v = entry.getValue();
            if (v instanceof CharSequence) {
                v = "\"" + v + "\"";
                v = ((String)v).replace('\n', ' ');
            }
            sb.append('\n').append("@").append(entry.getKey()).append(" = ").append(v);
        }
        sb.append("\n").append(".layout = ").append(layout.getClass().getSimpleName());
        sb.append("\n").append(".dataspace = ").append(Arrays.toString(dataspace.getDimensionSizes()));
        sb.append("\n").append(".datatype = ").append(datatype.getByteSize()).append("bytes, ").append(datatype);

        Object value = dataPreview(this, 16);
        sb.append("\nValues subset : ").append(value);

        return sb.toString();
    }

    /**
     * Extract a string representation of the first values in the dataset.
     */
    private static String dataPreview(Dataset dataset, int maxArraySize) {
        Object value;
        try {
            int[] dimensions = dataset.getDataspace().getDimensionSizes();
            for (int i : dimensions) {
                if (i == 0) {
                    return "<empty, dimension has a size of 0>";
                }
            }
            final long[] starts = new long[dimensions.length];
            final long[] ends = new long[dimensions.length];
            for (int i=0;i<dimensions.length;i++) {
                ends[i] = Math.min(dimensions[i], maxArraySize);
            }
            final GridExtent extent = new GridExtent(null, starts, ends, false);
            value = dataset.read(extent);
        } catch (Exception e) {
            value = e.getMessage();
        }

        return toString(value, maxArraySize);
    }

    private static String toString(Object value, int maxArraySize) {
        if (value == null) {
            return "null";
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            length = Math.min(length, maxArraySize);
            Object[] arr = (Object[]) Array.newInstance(Object.class, length);
            for (int i=0;i<length;i++) {
                Object v = Array.get(value, i);
                Array.set(arr, i, toString(v, maxArraySize));
            }
            return Arrays.toString(arr);
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * Reverses the order of elements in the given array.
     * This operation is performed in-place.
     * If the given array is {@code null}, then this method does nothing.
     *
     * @param  values  the array in which to reverse the order of elements, or {@code null} if none.
     */
    public static void reverse(final long[] values) {
        if (values != null) {
            int i = values.length >>> 1;
            int j = i + (values.length & 1);
            while (--i >= 0) {
                swap(values, i, j++);
            }
        }
    }
}
