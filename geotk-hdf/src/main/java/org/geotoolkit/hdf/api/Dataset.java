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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import static java.util.Collections.singletonMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.io.stream.ChannelDataInput;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.crs.DefaultEngineeringCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.cs.DefaultLinearCS;
import org.apache.sis.referencing.cs.DefaultTimeCS;
import org.apache.sis.referencing.datum.DefaultEngineeringDatum;
import org.apache.sis.referencing.datum.DefaultTemporalDatum;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.netcdf.AttributeNames;
import org.apache.sis.util.ArraysExt;
import static org.apache.sis.util.ArraysExt.swap;
import org.apache.sis.util.iso.Names;
import org.apache.sis.util.resources.Vocabulary;
import org.geotoolkit.hdf.HDF5Provider;
import org.geotoolkit.hdf.ObjectHeader;
import org.geotoolkit.hdf.SymbolTableEntry;
import static org.geotoolkit.hdf.api.Group.prettyPrint;
import org.geotoolkit.hdf.btree.BTreeV1;
import org.geotoolkit.hdf.btree.BTreeV1Chunk;
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
import org.geotoolkit.temporal.object.TemporalUtilities;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.EngineeringDatum;
import org.opengis.referencing.datum.TemporalDatum;
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
public final class Dataset extends AbstractResource implements Node {

    public static final CoordinateReferenceSystem LATITUDE;
    public static final CoordinateReferenceSystem LONGITUDE;

    static {
        {
            final EngineeringDatum datum = new DefaultEngineeringDatum(Collections.singletonMap("name", "Datum"));
            final CoordinateSystemAxis axis = new DefaultCoordinateSystemAxis(Collections.singletonMap("name", "Axis"), "unnamed", AxisDirection.NORTH, Units.UNITY);
            final CoordinateSystem cs = new DefaultLinearCS(Collections.singletonMap("name", "CS"), axis);
            LATITUDE = new DefaultEngineeringCRS(Collections.singletonMap("name", "LATITUDE"), datum, cs);
        }
        {
            final EngineeringDatum datum = new DefaultEngineeringDatum(Collections.singletonMap("name", "Datum"));
            final CoordinateSystemAxis axis = new DefaultCoordinateSystemAxis(Collections.singletonMap("name", "Axis"), "unnamed", AxisDirection.EAST, Units.UNITY);
            final CoordinateSystem cs = new DefaultLinearCS(Collections.singletonMap("name", "CS"), axis);
            LONGITUDE = new DefaultEngineeringCRS(Collections.singletonMap("name", "LONGITUDE"), datum, cs);
        }
    }

    private final Group parent;
    private final Connector connector;

    private final String name;
    private final GenericName genericName;
    private final BTreeV1 btree;
    private final LocalHeap localHeap;
    private final long address;

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

    public Dataset(Group parent, Connector connector, SymbolTableEntry entry, String name) throws IOException, DataStoreException {
        super(null, false);
        this.parent = parent;
        this.connector = connector;
        this.name = name;
        this.address = entry.getObjectHeaderAddress();

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
    public long getAddress() {
        return address;
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

    /**
     *
     * @param attributeName null if datatype is not compund.
     * @return
     */
    public Entry<Unit,CoordinateReferenceSystem> getAttributeUnit(String attributeName) throws FactoryException {
        final Object units = attributes.get(attributeName == null ? "units" : (attributeName + ":units"));
        final Object standardName = attributes.get(attributeName == null ? "standard_name" : (attributeName + ":standard_name"));
        final Object shortName = attributes.get(attributeName == null ? "short_name" : (attributeName + ":short_name"));
        final Object longName = attributes.get(attributeName == null ? "long_name" : (attributeName + ":long_name"));
        final String unitStr = units == null ? null : String.valueOf(units);
        Unit unit = null;
        CoordinateReferenceSystem crs = null;
        if (unitStr != null) {
            try {
                unit = Units.valueOf(unitStr);
            } catch (MeasurementParseException ex) {
                int idx = unitStr.indexOf("since");
                if (idx > 0) {
                    try {
                        final String baseUnitStr = unitStr.substring(0, idx).trim();
                        unit = Units.valueOf(baseUnitStr);
                        final String originStr = unitStr.substring(idx+5).trim();
                        final Calendar epoch = TemporalUtilities.parseDateCal(originStr);

                        final Map<String,?> props = singletonMap(NAME_KEY, new NamedIdentifier(null, "attributeName"));
                        final TemporalDatum datum = new DefaultTemporalDatum(props, epoch.getTime().toInstant());
                        final Map<String,Object> properties = new HashMap<>();
                        final String crsName = attributeName == null ? getName() : attributeName;
                        properties.put(TemporalCRS.NAME_KEY, new NamedIdentifier(Names.createLocalName(null, null, crsName)));
                        final Map<String,?> cs   = singletonMap(NAME_KEY, new NamedIdentifier(null, Vocabulary.formatInternational(Vocabulary.Keys.Temporal)));
                        final Map<String,?> axis = singletonMap(NAME_KEY, new NamedIdentifier(null, Vocabulary.formatInternational(Vocabulary.Keys.Time)));
                        final DefaultTimeCS timeCs = new DefaultTimeCS(cs, new DefaultCoordinateSystemAxis(axis, "t", AxisDirection.FUTURE, unit));
                        crs = new DefaultTemporalCRS(properties, datum, timeCs);

                    } catch (MeasurementParseException | ParseException e) {
                        HDF5Provider.LOGGER.log(Level.WARNING, "Failed to parse unit {0}", unitStr);
                    }
                } else {
                    HDF5Provider.LOGGER.log(Level.WARNING, "Failed to parse unit {0}", unitStr);
                }
            }
        }

        if (crs == null) {
            if (standardName != null) {
                final String stdName = String.valueOf(standardName);
                if ("latitude".equalsIgnoreCase(stdName)) {
                    crs = LATITUDE;
                    unit = crs.getCoordinateSystem().getAxis(0).getUnit();
                } else if ("longitude".equalsIgnoreCase(stdName)) {
                    crs = LONGITUDE;
                    unit = crs.getCoordinateSystem().getAxis(0).getUnit();
                }
            }
        }

        if (unit != null) {
            return new AbstractMap.SimpleImmutableEntry<>(unit, crs);
        }
        return null;
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

                // TODO: chunks matching query should be retrieved using an index, instead of using a full-scan strategy.
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


        final GridExtent intersection = safeIntersection(chunkExtent, queryExtent);
        if (intersection == null) {
            HDF5Provider.LOGGER.log(Level.FINER, () -> String.format("Chunk extent does not intersect queried one:%nChunk: %s%nQueried: %s%n", chunkExtent, queryExtent));
            return;
        }

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

    private Object readChunkDatas(HDF5DataInput chunkChannel, final long[] dimensionByteSize, final long[] chunkLow, final long[] intersectionLow, final int[] intersectionSize, int dimIdx, int ... compoundindexes) throws IOException, DataStoreException {
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
            Object v = prettyPrint(this, entry.getValue());
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

    /**
     * @return {@code null} if extents do <em>not</em> intersect. Otherwise, the result of their intersection.
     */
    private static GridExtent safeIntersection(GridExtent first, GridExtent second) {
        var dim = first.getDimension();
        assert dim == second.getDimension();
        final long[] newLow = new long[dim];
        final long[] newHigh = new long[dim];
        for (int i = 0; i < dim ; i++) {
            var low = Math.max(first.getLow(i), second.getLow(i));
            var high = Math.min(first.getHigh(i), second.getHigh(i));
            if (high < low) return null;
            newLow[i] = low;
            newHigh[i] = high;
        }
        return new GridExtent(null, newLow, newHigh, true);
    }
}
