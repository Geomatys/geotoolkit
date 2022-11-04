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
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.io.ChannelDataInput;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.ArraysExt;
import static org.apache.sis.util.ArraysExt.swap;
import org.apache.sis.util.collection.BackingStoreException;
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
import org.geotoolkit.hdf.io.ConstantSeekableByteChannel;
import org.geotoolkit.hdf.io.HDF5ChannelDataInput;
import org.geotoolkit.hdf.io.HDF5DataInput;
import org.geotoolkit.hdf.io.SequenceSeekableByteChannel;
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
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
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

    private static final String HDF_INDEX = "hdf-index";

    private final Group parent;
    private final Connector connector;
    private final SymbolTableEntry entry;

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
    private final long[] dimensionByteSize;
    private TreeMap<Long,BTreeV1Chunk> chunks = null;

    //featureset
    private FeatureType featureType;
    private boolean is1D;
    private boolean isEmpty;

    public Dataset(Group parent, Connector connector, SymbolTableEntry entry, String name) throws IOException, DataStoreException {
        super(null, false);
        this.parent = parent;
        this.connector = connector;
        this.entry = entry;
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

        final int[] dimensionSizes = dataspace.getDimensionSizes().clone();
        ArraysExt.reverse(dimensionSizes);
        cellByteSize = datatype.getByteSize();
        dimensionByteSize = new long[dimensionSizes.length];
        if (dimensionSizes.length > 0){
            dimensionByteSize[0] = cellByteSize;
            for (int i = 1; i < dimensionSizes.length; i++) {
                dimensionByteSize[i] = dimensionByteSize[i-1] * dimensionSizes[i-1];
            }
            reverse(dimensionByteSize);
        }

        genericName = Node.createName(this);
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
                    ftb.addAttribute(member.memberType.getValueClass()).setName(member.name);
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
                } catch (IOException | DataStoreException ex) {
                    throw new BackingStoreException(ex.getMessage(), ex);
                }
            }
        });
    }

    private Stream<Feature> toFeatures(GridExtent extent) throws DataStoreException, IOException {
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
                    feature.setPropertyValue(cmp.members[k].name, Array.get(rawRecord, k));
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

        final long startOffset = locationToOffset(extent.getLow().getCoordinateValues());
        final long endOffset = locationToOffset(extent.getHigh().getCoordinateValues());

        //build channel
        HDF5DataInput channel = null;
        try {
            if (layout instanceof DataLayoutMessage.Compact cdt) {
                channel = connector.createChannel();
                channel.seek(cdt.rawDataAddress);
            } else if (layout instanceof DataLayoutMessage.Contiguous cdt) {
                channel = connector.createChannel();
                channel.seek(cdt.address);
            } else if (layout instanceof DataLayoutMessage.Chunked cdt) {
                channel = connector.createChannel();

                if (channel.isUndefinedLength(cdt.address)) {
                    //no data allocated
                    final ChannelDataInput cdi = new ChannelDataInput("no-data-allocated", ByteBuffer.wrap(new byte[0]));
                    channel = new HDF5ChannelDataInput(cdi);
                } else {

                    //build filters
                    final List<FilterDescription> filters;
                    if (filter == null) {
                        filters = Collections.EMPTY_LIST;
                    } else {
                        filters = filter.getFilters();
                    }

                    //read btree of all chunks
                    final TreeMap<Long,BTreeV1Chunk> rawchunks = getChunks(channel);

                    //keep only the chunks in the range we will read
                    final Long floor = rawchunks.floorKey(startOffset);
                    final Long ceiling = rawchunks.floorKey(endOffset);

                    final NavigableMap<Long, BTreeV1Chunk> candidates = rawchunks.subMap(floor == null ? rawchunks.firstKey() : floor, true,
                            ceiling == null ? rawchunks.lastKey() : ceiling, true);

                    final TreeMap<Long,ChunkSeekableByteChannel> chunks = new TreeMap<>();
                    for (Entry<Long,BTreeV1Chunk> entry : candidates.entrySet()) {
                        final BTreeV1Chunk chunk = entry.getValue();
                        if (chunk.filterMask != 0) {
                            throw new IOException("Filter masks not supported yet");
                        }
                        chunks.put(entry.getKey(), new ChunkSeekableByteChannel(chunk, channel, filters));
                    }

                    byte[] fill = new byte[1];
                    if (fillValue != null && fillValue.getFillValue() != null && fillValue.getFillValue().length > 0) {
                        fill = fillValue.getFillValue();
                    }

                    final List<SeekableByteChannel> stack = new ArrayList<>();
                    long offset = 0;
                    for (Entry<Long,ChunkSeekableByteChannel> entry : chunks.entrySet()) {
                        final Long start = entry.getKey();
                        final ChunkSeekableByteChannel chunk = entry.getValue();

                        if (start != offset) {
                            //add fill values
                            ConstantSeekableByteChannel fillStream = new ConstantSeekableByteChannel(start-offset, fill);
                            stack.add(fillStream);
                            offset = start;
                        }

                        stack.add(chunk);
                        offset += chunk.size();
                    }
                    if (offset < endOffset) {
                        //fill what remains
                        ConstantSeekableByteChannel fillStream = new ConstantSeekableByteChannel(endOffset-offset, fill);
                        stack.add(fillStream);
                    }
                    //create a btree style sequence of stream to avoid deep stack trace concatenation
                    SeekableByteChannel stream = createStackStream(stack);

                    channel = new HDF5ChannelDataInput(new ChannelDataInput("", stream, ByteBuffer.allocate(4096), false));
                }

            } else if (layout instanceof DataLayoutMessage.Virtual cdt) {
                throw new IOException("Not supported layout " + layout.getClass().getSimpleName());
            } else {
                throw new IOException("Unexpected layout " + layout.getClass().getSimpleName());
            }

            //read datas
            if (dimensionSizes.length == 0) {
                //scalar value
                return datatype.readData(channel, compoundindexes);
            } else {
                return readDatas(channel, extent, compoundindexes);
            }

        } finally {
            if (channel != null) {
                channel.close();
            }
        }
    }

    private synchronized TreeMap<Long,BTreeV1Chunk> getChunks(HDF5DataInput channel) throws IOException {
        if (chunks == null) {
            final DataLayoutMessage.Chunked cdt = (DataLayoutMessage.Chunked) layout;
            //read btree of all chunks
            channel.seek(cdt.address);
            final BTreeV1 datatree = new BTreeV1();
            datatree.read(channel, dataspace.getDimensionSizes().length);
            final BTreeV1.DataNode node = (BTreeV1.DataNode) datatree.root;
            final List<BTreeV1Chunk> rawChunks = node.getChunks();

            chunks = new TreeMap<>();
            for (BTreeV1Chunk chl : rawChunks) {
                final long[] startCoordinate = chl.offset;
                final long location = locationToOffset(startCoordinate);
                chunks.put(location, chl);
            }
        }

        return chunks;
    }

    private static SeekableByteChannel createStackStream(List<SeekableByteChannel> streams) throws IOException {
        if (streams.size() == 1) return streams.get(0);
        final SeekableByteChannel s0 = createStackStream(streams.subList(0, streams.size()/2));
        final SeekableByteChannel s1 = createStackStream(streams.subList(streams.size()/2, streams.size()));
        return new SequenceSeekableByteChannel(s0, s1);
    }

    private Object readDatas(HDF5DataInput channel, GridExtent extent, int ... compoundindexes) throws IOException {
        final long[] low = extent.getLow().getCoordinateValues();
        final long[] high = extent.getHigh().getCoordinateValues();
        final int[] dimensions = new int[low.length];
        for (int i = 0; i< dimensions.length; i++) {
            dimensions[i] = Math.toIntExact(high[i] - low[i] + 1);
        }

        return readDatas(channel, low, high, dimensions, 0, compoundindexes);
    }

    private Object readDatas(HDF5DataInput channel, final long[] low, final long[] high, final int[] dimensions, int dimIdx, int ... compoundindexes) throws IOException {

//        channel.mark();
        final long basePosition = channel.getStreamPosition();

        Object values;
        if (dimIdx == low.length - 1) {
            //a strip
            channel.seek(basePosition + (cellByteSize * low[dimIdx]));
            values = datatype.readData(channel, dimensions[dimIdx], compoundindexes);
        } else {
            //an array of strips
            values = java.lang.reflect.Array.newInstance(datatype.getValueClass(), dimensions[dimIdx], 0);
            for (int k = 0; k < dimensions[dimIdx]; k++) {
                channel.seek(basePosition + (low[dimIdx] + k) * dimensionByteSize[dimIdx]);
                final Object strip = readDatas(channel, low, high, dimensions, dimIdx+1, compoundindexes);
                java.lang.reflect.Array.set(values, k, strip);
            }
        }

//        channel.reset();
        return values;
    }

    /**
     * Convert given location to offset in the data stream.
     * @param location sample location
     * @return offset in datastream
     */
    private long locationToOffset(long[] location) {
        long offset = 0;
        for (int i = 0; i < dimensionByteSize.length; i++) {
            offset += dimensionByteSize[i] * location[i];
        }
        return offset;
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
