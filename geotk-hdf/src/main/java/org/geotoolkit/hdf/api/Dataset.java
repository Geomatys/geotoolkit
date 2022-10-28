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
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.internal.storage.io.ChannelDataInput;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.hdf.ObjectHeader;
import org.geotoolkit.hdf.SymbolTableEntry;
import org.geotoolkit.hdf.btree.BTreeV1;
import org.geotoolkit.hdf.btree.BTreeV1Chunk;
import org.geotoolkit.hdf.datatype.DataType;
import org.geotoolkit.hdf.filter.Deflate;
import org.geotoolkit.hdf.filter.Filter;
import org.geotoolkit.hdf.filter.Shuffle;
import org.geotoolkit.hdf.heap.LocalHeap;
import org.geotoolkit.hdf.io.ChunkInputStream;
import org.geotoolkit.hdf.io.Connector;
import org.geotoolkit.hdf.io.ConstantInputStream;
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
import org.opengis.util.GenericName;

/**
 * A dataset is an object header that contains messages that describe the
 * datatype, dataspace, layout, filters, external files, fill value, and other
 * elements with the layout message pointing to either a raw data chunk or to
 * a B-tree that points to raw data chunks.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Dataset extends AbstractResource implements Node {

    private final Group parent;
    private final Connector connector;
    private final SymbolTableEntry entry;

    private final String name;
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

        final int[] dimensionSizes = dataspace.getDimensionSizes();
        cellByteSize = datatype.getByteSize();
        dimensionByteSize = new long[dimensionSizes.length];
        if (dimensionSizes.length > 0){
            dimensionByteSize[0] = cellByteSize;
            for (int i = 1; i < dimensionSizes.length; i++) {
                dimensionByteSize[i] = dimensionByteSize[i-1] * dimensionSizes[i-1];
            }
        }
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
        return Optional.of(Names.createLocalName(null, null, name));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
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
            extent = new GridExtent(null, null, ArraysExt.copyAsLongs(dimensionSizes), false);
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
                    final Filter[] filters;
                    if (filter == null) {
                        filters = new Filter[0];
                    } else {
                        final List<FilterDescription> lst = filter.getFilters();
                        filters = new Filter[lst.size()];
                        for (int i = 0; i < filters.length; i++) {
                            final FilterDescription desc = lst.get(i);
                            filters[i] = switch (desc.filteridentificationValue) {
                                case FilterPipelineMessage.DEFLATE -> new Deflate();
                                case FilterPipelineMessage.SHUFFLE -> new Shuffle(desc.clientData[0]);
                                default -> throw new IOException("Unsupported filter " + desc.filteridentificationValue);
                            };
                        }
                    }

                    //read btree of all chunks
                    channel.seek(cdt.address);
                    final BTreeV1 datatree = new BTreeV1();
                    datatree.read(channel, dataspace.getDimensionSizes().length);
                    final BTreeV1.DataNode node = (BTreeV1.DataNode) datatree.root;
                    final List<BTreeV1Chunk> rawChunks = node.getChunks();

                    final Map<Long,ChunkInputStream> chunks = new TreeMap<>();
                    for (int i = 0, n = rawChunks.size(); i < n; i++) {
                        final BTreeV1Chunk chunk = rawChunks.get(i);
                        if (chunk.filterMask != 0) {
                            throw new IOException("Filter masks not supported yet");
                        }
                        final long[] startCoordinate = chunk.offset;
                        final long location = locationToOffset(startCoordinate);

                        if (location >= startOffset && location <= endOffset) {
                            //keep only the chunks in the range we will read
                            final ChunkInputStream s = new ChunkInputStream(channel, chunk.address, chunk.size, filters);
                            chunks.put(location, s);
                        }
                    }

                    byte[] fill = new byte[1];
                    if (fillValue != null && fillValue.getFillValue() != null && fillValue.getFillValue().length > 0) {
                        fill = fillValue.getFillValue();
                    }

                    InputStream stream = null;
                    long offset = 0;
                    for (Entry<Long,ChunkInputStream> entry : chunks.entrySet()) {
                        final Long start = entry.getKey();
                        final ChunkInputStream chunk = entry.getValue();

                        if (start != offset) {
                            //add fill values
                            ConstantInputStream fillStream = new ConstantInputStream(start-offset, fill);
                            stream = (stream == null) ? fillStream : new java.io.SequenceInputStream(stream, fillStream);
                            offset = start;
                        }

                        stream = (stream == null) ? chunk : new java.io.SequenceInputStream(stream, chunk);
                        offset += chunk.getUncompressedSize();
                    }
                    if (offset != endOffset) {
                        //fill what remains
                        ConstantInputStream fillStream = new ConstantInputStream(endOffset-offset, fill);
                        stream = (stream == null) ? fillStream : new java.io.SequenceInputStream(stream, fillStream);
                    }

                    final ReadableByteChannel rbc = Channels.newChannel(stream);
                    channel = new HDF5ChannelDataInput(new ChannelDataInput("", rbc, ByteBuffer.allocate(4096), false));
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
}
