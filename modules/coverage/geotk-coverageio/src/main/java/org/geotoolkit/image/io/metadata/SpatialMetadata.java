/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.metadata;

import java.text.Format;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;

import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.acquisition.AcquisitionInformation;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.Localized;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.logging.LoggedFormat;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.SpatialImageWriter;
import org.geotoolkit.measure.RangeFormat;


/**
 * Spatial (usually geographic) informations encoded in an image. This class converts the
 * {@link IIOMetadataNode} elements and attribute values to ISO 19115-2 metadata objects.
 * While ISO 19115-2 is the primary standard supported by this class, other standards can
 * works if they are designed with the same rules than the {@link org.opengis.metadata}
 * package.
 * <p>
 * ISO 19115-2 metadata instances are obtained by {@link #getInstanceForType(Class)} (for
 * a single instance) or {@link #getListForType(Class)} (for a list of metadata instances)
 * methods. The table below lists some common metadata elements. The "<cite>Format</cite>"
 * and "<cite>Path to node</cite>" columns give the location of the metadata element in a
 * tree conform to the <a href="SpatialMetadataFormat.html#default-formats">spatial metadata
 * format</a> defined in this package.
 * <p>
 * <blockquote><table border="1" cellspacing="0">
 *   <tr bgcolor="lightblue">
 *     <th>&nbsp;Format&nbsp;</th>
 *     <th>&nbsp;Path to node&nbsp;</th>
 *     <th>&nbsp;Method call&nbsp;</th>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@link SpatialMetadataFormat#STREAM STREAM}&nbsp;</td>
 *     <td>&nbsp;{@code "DiscoveryMetadata"}</td>
 *     <td>&nbsp;<code>getInstanceForType({@linkplain DataIdentification}.class)</code></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@link SpatialMetadataFormat#STREAM STREAM}&nbsp;</td>
 *     <td>&nbsp;{@code "DiscoveryMetadata/Extent/GeographicElement"}</td>
 *     <td>&nbsp;<code>getInstanceForType({@linkplain GeographicBoundingBox}.class)</code></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@link SpatialMetadataFormat#STREAM STREAM}&nbsp;</td>
 *     <td>&nbsp;{@code "AcquisitionMetadata"}</td>
 *     <td>&nbsp;<code>getInstanceForType({@linkplain AcquisitionInformation}.class)</code></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@link SpatialMetadataFormat#IMAGE IMAGE}&nbsp;</td>
 *     <td>&nbsp;{@code "ImageDescription"}</td>
 *     <td>&nbsp;<code>getInstanceForType({@linkplain ImageDescription}.class)</code></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@link SpatialMetadataFormat#IMAGE IMAGE}&nbsp;</td>
 *     <td>&nbsp;{@code "ImageDescription/Dimensions"}</td>
 *     <td>&nbsp;<code>getListForType({@linkplain SampleDimension}.class)</code></td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@link SpatialMetadataFormat#IMAGE IMAGE}&nbsp;</td>
 *     <td>&nbsp;{@code "RectifiedGridDomain"}</td>
 *     <td>&nbsp;<code>getInstanceForType({@linkplain RectifiedGrid}.class)</code></td>
 *   </tr>
 * </table></blockquote>
 * <p>
 * The {@code getInstanceForType(Class)} and {@code getListForType(Class)} methods are
 * tolerant to metadata located in other paths than the ones documented below, provided
 * that the underlying metadata {@linkplain #format} declares exactly one element accepting
 * a {@linkplain IIOMetadataFormat#getObjectClass(String) user object}
 * {@linkplain Class#isAssignableFrom(Class) assignable} to the given type.
 * In case of ambiguity, an {@link IllegalArgumentException} is thrown.
 *
 * {@section Errors handling}
 * If some inconsistency are found while reading (for example if the coordinate system
 * dimension doesn't match the envelope dimension), then the default implementation
 * {@linkplain #warningOccurred logs a warning}. We do not throw an exception because
 * minor errors are not uncommon in geographic data, and we want to process the data on
 * a "<cite>best effort</cite>" basis. However because every warnings are logged through
 * the {@code warningOccurred} method, subclasses can override this method if they want
 * to treat some warnings as fatal errors.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @see SpatialMetadataFormat
 *
 * @since 3.04 (derived from 2.4)
 * @module
 */
public class SpatialMetadata extends IIOMetadata implements Localized {
    /**
     * An empty {@code SpatialMetadata} with no data and no format. This constant is
     * an alternative to {@code null} for meaning that no metadata are available.
     *
     * @since 3.06
     */
    public static final SpatialMetadata EMPTY = new SpatialMetadata();

    /**
     * The metadata format given at construction time. This is typically
     * {@link SpatialMetadataFormat#IMAGE} or {@link SpatialMetadataFormat#STREAM}.
     */
    protected final IIOMetadataFormat format;

    /**
     * The {@link ImageReader} or {@link ImageWriter} that holds the metadata,
     * or {@code null} if none.
     */
    private final Object owner;

    /**
     * The metadata provided by standard {@link ImageReader} instances, on which to fallback
     * if the user asked for something else than the spatial metadata managed by this class.
     * This is {@code null} if there is no such fallback.
     */
    private final IIOMetadata fallback;

    /**
     * The root node to be returned by {@link #getAsTree}.
     */
    private Node root;

    /**
     * The values created by {@link #getInstanceForType(Class)}, cached for reuse.
     */
    private transient Map<Class<?>, Object> instances;

    /**
     * The values created by {@link #getListForType(Class)}, cached for reuse.
     */
    private transient Map<Class<?>, List<?>> lists;

    /**
     * The standard date format. Will be created only when first needed.
     *
     * @see #dateFormat()
     */
    private transient LoggedFormat<Date> dateFormat;

    /**
     * The standard range format. Will be created only when first needed.
     *
     * @see #rangeFormat()
     */
    private transient LoggedFormat<NumberRange<?>> rangeFormat;

    /**
     * Creates a metadata with no format. This constructor
     * is for the {@link #EMPTY} constant only.
     */
    private SpatialMetadata() {
        format   = null;
        owner    = null;
        fallback = null;
    }

    /**
     * Creates an initially empty metadata instance for the given format.
     * The {@code format} argument is usually one of the {@code SpatialMetadataFormat}
     * {@link SpatialMetadataFormat#STREAM STREAM} or {@link SpatialMetadataFormat#IMAGE IMAGE}
     * constants, but other formats are allowed.
     *
     * @param format The metadata format.
     */
    public SpatialMetadata(final IIOMetadataFormat format) {
        this(format, (Object) null, null);
    }

    /**
     * Creates an initially empty metadata instance for the given format and reader.
     * The {@code format} argument is usually one of the {@code SpatialMetadataFormat}
     * {@link SpatialMetadataFormat#STREAM STREAM} or {@link SpatialMetadataFormat#IMAGE IMAGE}
     * constants, but other formats are allowed.
     * <p>
     * If the {@code fallback} argument is non-null, then any call to a
     * {@link #getMetadataFormat(String) getMetadataFormat}, {@link #getAsTree(String) getAsTree},
     * {@link #mergeTree(String,Node) mergeTree} or {@link #setFromTree(String,Node) setFromTree}
     * method with a format name different than
     * <code>format.{@linkplain IIOMetadataFormat#getRootName() getRootName()}</code> will be
     * delegated to that fallback. This is useful when the given {@code ImageReader} is actually
     * a wrapper around an other {@code ImageReader}.
     *
     * @param format   The metadata format.
     * @param reader   The source image reader, or {@code null} if none.
     * @param fallback The fallback for any format name different than {@code format.getRootName()},
     *                 or {@code null} if none.
     */
    public SpatialMetadata(final IIOMetadataFormat format, final ImageReader reader, final IIOMetadata fallback) {
        this(format, (Object) reader, fallback);
    }

    /**
     * Creates an initially empty metadata instance for the given format and writer.
     * The {@code format} argument is usually one of the {@code SpatialMetadataFormat}
     * {@link SpatialMetadataFormat#STREAM STREAM} or {@link SpatialMetadataFormat#IMAGE IMAGE}
     * constants, but other formats are allowed.
     * <p>
     * If the {@code fallback} argument is non-null, then any call to a
     * {@link #getMetadataFormat(String) getMetadataFormat}, {@link #getAsTree(String) getAsTree},
     * {@link #mergeTree(String,Node) mergeTree} or {@link #setFromTree(String,Node) setFromTree}
     * method with a format name different than
     * <code>format.{@linkplain IIOMetadataFormat#getRootName() getRootName()}</code> will be
     * delegated to that fallback. This is useful when the given {@code ImageWriter} is actually
     * a wrapper around an other {@code ImageWriter}.
     *
     * @param format   The metadata format.
     * @param writer   The target image writer, or {@code null} if none.
     * @param fallback The fallback for any format name different than {@code format.getRootName()},
     *                 or {@code null} if none.
     */
    public SpatialMetadata(final IIOMetadataFormat format, final ImageWriter writer, final IIOMetadata fallback) {
        this(format, (Object) writer, fallback);
    }

    /**
     * Creates an initially empty metadata instance for the given format and reader/writer.
     * The format name of the given fallback are merged with the name of the given format.
     */
    private SpatialMetadata(final IIOMetadataFormat format, final Object owner, final IIOMetadata fallback) {
        if (format == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, "format"));
        }
        this.format   = format;
        this.owner    = owner;
        this.fallback = fallback;
        if (fallback != null) {
            standardFormatSupported   = fallback.isStandardMetadataFormatSupported();
            nativeMetadataFormatName  = fallback.getNativeMetadataFormatName();
            String[] extraFormatNames = fallback.getExtraMetadataFormatNames();
            if (extraFormatNames != null) {
                final int length = extraFormatNames.length;
                extraFormatNames = Arrays.copyOf(extraFormatNames, length + 1);
                extraFormatNames[length] = format.getRootName();
                extraMetadataFormatNames = extraFormatNames;
                return;
            }
            /*
             * Note: we leave 'nativeMetadataFormatClassName' and 'extraMetadataFormatClassNames'
             * to null, which is illegal. However the only method to use those informations is
             * IIOMetadata.getMetadataFormat(String), so this is okay if we overload that method.
             *
             * Getting the format class would be costly since there is no IIOMetadata method
             * giving that information.
             */
        }
        extraMetadataFormatNames = new String[] {format.getRootName()};
    }

    /**
     * Returns an instance of the given type extracted from this {@code IIOMetadata}.
     * This method performs the following steps:
     *
     * <ol>
     *   <li><p><b>Invoke <code>{@linkplain MetadataAccessor#listPaths
     *       MetadataAccessor.listPaths}({@linkplain #format}, type)</code>:</b><br>
     *
     *       Search for an element declared in the {@linkplain #format} specified at construction
     *       time which accept a {@linkplain IIOMetadataNode#getUserObject() user object} of the
     *       given type. Exactly one element is expected, otherwise an {@link IllegalArgumentException}
     *       is thrown.</p></li>
     *
     *   <li><p><b>Create a <code>new {@linkplain MetadataAccessor#MetadataAccessor(IIOMetadata, String, String, String)
     *       MetadataAccessor}(this, {@linkplain #format}.getRootName(), path, "#auto")</code>:</b><br>
     *
     *       Create a new metadata accessor for the singleton path found at the previous step.</p></li>
     *
     *   <li><p><b>Invoke <code>{@linkplain MetadataAccessor#getUserObject(Class)
     *       MetadataAccessor.getUserObject}(type)</code>:</b><br>
     *
     *       If a user object was explicitly specified, return that object.</p></li>
     *
     *   <li><p><b>Invoke <code>{@linkplain MetadataAccessor#newProxyInstance(Class)
     *       MetadataAccessor.newProxyInstance}(type)</code>:</b><br>
     *
     *       If no explicit user object was found, create a proxy which will implement the getter
     *       methods by a code that fetch the value from the corresponding attribute.</p></li>
     * </ol>
     *
     * @param  <T> The compile-time type specified as the {@code type} argument.
     * @param  type The interface implemented by the instance to fetch.
     * @return An implementation of the given interface.
     * @throws IllegalArgumentException If the given type is not a valid interface,
     *         or if no element or more than one element exist for the given type.
     *
     * @see MetadataAccessor#newProxyInstance(Class)
     *
     * @since 3.06
     */
    public <T> T getInstanceForType(final Class<T> type) throws IllegalArgumentException {
        if (instances == null) {
            instances = new HashMap<Class<?>, Object>();
        }
        @SuppressWarnings("unchecked")
        T object = (T) instances.get(type);
        if (object == null) {
            final MetadataAccessor accessor = new MetadataAccessor(this, null, type);
            object = accessor.getUserObject(type);
            if (object == null) {
                object = accessor.newProxyInstance(type);
            }
            instances.put(type, object);
        }
        return object;
    }

    /**
     * Returns a list of instances of the given type extracted from this {@code IIOMetadata}.
     * This method performs the same work than {@link #getInstanceForType(Class)}, but for a
     * list.
     *
     * @param  <T> The compile-time type specified as the {@code type} argument.
     * @param  type The interface implemented by the elements of the list to fetch.
     * @return A list of implementations of the given interface.
     * @throws IllegalArgumentException If the given type is not a valid interface,
     *         or if no element or more than one element exist for the given type.
     *
     * @see MetadataAccessor#newProxyList(Class)
     *
     * @since 3.06
     */
    public <T> List<T> getListForType(final Class<T> type) throws IllegalArgumentException {
        if (lists == null) {
            lists = new HashMap<Class<?>, List<?>>();
        }
        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) lists.get(type);
        if (list == null) {
            final MetadataAccessor accessor = new MetadataAccessor(this, null, type);
            list = accessor.newProxyList(type);
            lists.put(type, list);
        }
        return list;
    }

    /**
     * Returns the language to use when formatting messages, or {@code null} for the
     * default. The default implementation delegates to {@link ImageReader#getLocale} or
     * {@link ImageWriter#getLocale} if possible, or returns {@code null} otherwise.
     *
     * @return The locale for formatting messages.
     */
    @Override
    public Locale getLocale() {
        if (owner instanceof ImageReader) {
            return ((ImageReader) owner).getLocale();
        }
        if (owner instanceof ImageWriter) {
            return ((ImageWriter) owner).getLocale();
        }
        return null;
    }

    /**
     * Returns {@code true} if we should use the {@linkplain #format} given at construction
     * time, {@code false} if we should use the fallback, or thrown an exception otherwise.
     */
    private boolean isValidName(final String formatName) throws IllegalArgumentException {
        if (formatName == null || format == null) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, "formatName"));
        }
        if (format.getRootName().equalsIgnoreCase(formatName)) {
            return true;
        }
        if (fallback != null) {
            return false;
        }
        throw new IllegalArgumentException(Errors.getResources(getLocale()).getString(
                Errors.Keys.ILLEGAL_ARGUMENT_$2, "formatName", formatName));
    }

    /**
     * Returns an object describing the given metadata format, or {@code null} if no description is
     * available. The default implementation is as below:
     * <p>
     * <ul>
     *   <li>If {@code formatName} is equals, ignoring case, to <code>{@linkplain #format}.getRootName()</code>
     *       where {@code format} is the argument given at construction time, then return that format.</li>
     *   <li>Otherwise if a fallback has been specified at construction time, delegate to that
     *       fallback.</li>
     *   <li>Otherwise throw an {@code IllegalArgumentException}.</li>
     * </ul>
     *
     * @param  formatName The desired metadata format.
     * @return The desired metadata format, or {@code null} if none.
     * @throws IllegalArgumentException If the specified format name is not recognized.
     */
    @Override
    public IIOMetadataFormat getMetadataFormat(final String formatName) throws IllegalArgumentException {
        if (isValidName(formatName)) {
            return format;
        } else {
            return fallback.getMetadataFormat(formatName);
        }
    }

    /**
     * Returns the root of a tree of metadata contained within this object according to the
     * conventions defined by the metadata {@linkplain #format} associated to this instance.
     */
    final Node getAsTree() {
        if (format == null) {
            throw new UnsupportedOperationException();
        }
        if (root == null) {
            root = new IIONode(format.getRootName());
        }
        return root;
    }

    /**
     * Returns the root of a tree of metadata contained within this object
     * according to the conventions defined by a given metadata format.
     *
     * @param  formatName the desired metadata format.
     * @return The node forming the root of metadata tree.
     * @throws IllegalArgumentException if the format name is {@code null} or is not
     *         one of the names returned by {@link #getMetadataFormatNames}.
     */
    @Override
    public Node getAsTree(final String formatName) throws IllegalArgumentException {
        if (isValidName(formatName)) {
            return getAsTree();
        } else {
            return fallback.getAsTree(formatName);
        }
    }

    /**
     * Alters the internal state of this metadata from a tree whose syntax is defined by
     * the given metadata format. The semantics of how a tree or subtree may be merged with
     * another tree are format-specific. It may be simply replacing all existing state with
     * the contents of the given tree.
     *
     * @param  formatName The desired metadata format.
     * @param  root An XML DOM Node object forming the root of a tree.
     * @throws IIOInvalidTreeException If the tree cannot be parsed successfully using the
     *         rules of the given format.
     */
    @Override
    public void mergeTree(final String formatName, final Node root) throws IIOInvalidTreeException {
        if (isValidName(formatName)) {
            this.root = root;
        } else {
            fallback.mergeTree(formatName, root);
        }
    }

    /**
     * Alters the internal state of this metadata from a tree defined by the specified metadata.
     * The default implementation is equivalent to the code below (omitting exception handling)
     * where {@link #format} is the metadata format given at construction time:
     *
     * {@preformat java
     *     String formatName = format.getRootName();
     *     mergeTree(formatName, metadata.getAsTree(formatName);
     * }
     *
     * @param  metadata The metadata to merge to this object.
     * @throws IIOInvalidTreeException If the metadata can not be merged.
     */
    public void mergeTree(final IIOMetadata metadata) throws IIOInvalidTreeException {
        if (format == null) {
            throw new UnsupportedOperationException();
        }
        final String formatName = format.getRootName();
        final Node tree;
        try {
            tree = metadata.getAsTree(formatName);
        } catch (IllegalArgumentException exception) {
            throw new IIOInvalidTreeException(Errors.format(
                    Errors.Keys.GEOTOOLKIT_EXTENSION_REQUIRED_$1, "mergeTree"), exception, null);
        }
        mergeTree(formatName, tree);
    }

    /**
     * Merges the two specified trees. If both source and target metadata are non-null,
     * then this method performs the following steps:
     * <p>
     * <ul>
     *   <li>Searches for a format name which is common to both metadata;</li>
     *   <li>invokes {@link IIOMetadata#getAsTree(String)} on the source metadata;</li>
     *   <li>invokes {@link IIOMetadata#mergeTree(String, Node)} on the target metadata.</li>
     * </ul>
     *
     * @param  source The source metadata, or {@code null}.
     * @param  target The target metadata, or {@code null}.
     * @return {@code source} if {@code target} was null, or {@code target} otherwise.
     *
     * @throws IllegalStateException if {@code target} is read-only.
     * @throws IIOInvalidTreeException if the {@code source} tree cannot be parsed successfully.
     */
    public static IIOMetadata merge(final IIOMetadata source, final IIOMetadata target)
            throws IllegalStateException, IIOInvalidTreeException
    {
        if (source == null) {
            return target;
        }
        if (target == null) {
            return source;
        }
        final String format = commonFormatName(source, target);
        if (format != null) {
            target.mergeTree(format, source.getAsTree(format));
        }
        return null;
    }

    /**
     * Returns the name of a format which is common to both metadata.
     * The preferred formats are (in order):
     * <p>
     * <ul>
     *   <li>The native format of target metadata.<li>
     *   <li>The native format of source metadata.<li>
     *   <li>A format supported by both metadata which is not the standard format.</li>
     *   <li>The standard format is last resort, because it contains no geographic
     *       data and we wanted to give the priority to geographic formats.</li>
     * </ul>
     * <p>
     * If no common format is found, then this method returns {@code null}.
     */
    private static String commonFormatName(final IIOMetadata source, final IIOMetadata target) {
        final String[] sourceFormats = source.getMetadataFormatNames();
        String format = target.getNativeMetadataFormatName();
        if (format != null) {
            if (XArrays.contains(sourceFormats, format)) {
                return format;
            }
        }
        /*
         * The target native format is not supported. Try the source native format. We will search
         * only in extra names (not in all names) because we don't want to consider the standard
         * format now, and because it is not worth to test again the target native format since we
         * just did that in the block before.
         */
        final String[] targetFormats = target.getExtraMetadataFormatNames();
        if (targetFormats != null) {
            format = source.getNativeMetadataFormatName();
            if (format != null) {
                if (XArrays.contains(targetFormats, format)) {
                    return format;
                }
            }
            /*
             * Checks if there is a target extra format supported by the source metadata.
             */
            for (int i=0; i<targetFormats.length; i++) {
                format = targetFormats[i];
                if (XArrays.contains(sourceFormats, format)) {
                    return format;
                }
            }
        }
        /*
         * The standard format is the only one left. We try it last because it contains no
         * geographic information, and we wanted to give the priority to geographic formats.
         */
        if (source.isStandardMetadataFormatSupported() && target.isStandardMetadataFormatSupported()) {
            return IIOMetadataFormatImpl.standardMetadataFormatName;
        }
        return null;
    }

    /**
     * Returns {@code true} if this object does not support the {@link #mergeTree mergeTree},
     * {@link #setFromTree setFromTree}, and {@link #reset reset} methods. The default
     * implementation conservatively returns {@code true} if a fallback has been given
     * to the constructor and that fallback is read only.
     */
    @Override
    public boolean isReadOnly() {
        return (fallback == null) || fallback.isReadOnly();
    }

    /**
     * Resets all the data stored in this object to default values.
     * All nodes below the root node are discarted.
     */
    @Override
    public void reset() {
        root = null;
        if (fallback != null) {
            fallback.reset();
        }
    }

    /**
     * Invoked when some inconsistency has been detected in the spatial metadata. The default
     * implementation delegates to the first of the following choices which is applicable:
     * <p>
     * <ul>
     *   <li>{@link SpatialImageReader#warningOccurred(LogRecord)}</li>
     *   <li>{@link SpatialImageWriter#warningOccurred(LogRecord)}</li>
     *   <li>Send the record to the {@code "org.geotoolkit.image.io"} logger otherwise.</li>
     * </ul>
     * <p>
     * Subclasses can override this method if more processing is wanted, or for
     * throwing exception if some warnings should be considered as fatal errors.
     *
     * @param record The warning record to log.
     *
     * @see MetadataAccessor#warningOccurred(LogRecord)
     * @see javax.imageio.event.IIOReadWarningListener
     */
    protected void warningOccurred(final LogRecord record) {
        if (owner instanceof SpatialImageReader) {
            ((SpatialImageReader) owner).warningOccurred(record);
        } else if (owner instanceof SpatialImageWriter) {
            ((SpatialImageWriter) owner).warningOccurred(record);
        } else {
            final Logger logger = Logging.getLogger("org.geotoolkit.image.io");
            record.setLoggerName(logger.getName());
            logger.log(record);
        }
    }

    /**
     * A {@link LoggedFormat} which use the {@link SpatialMetadata#getLocale reader locale}
     * for warnings.
     */
    private final class FormatAdapter<T> extends LoggedFormat<T> {
        private static final long serialVersionUID = -1108933164506428318L;

        FormatAdapter(final Format format, final Class<T> type) {
            super(format, type);
        }

        @Override
        protected Locale getWarningLocale() {
            return getLocale();
        }

        @Override
        protected void logWarning(final LogRecord warning) {
            warningOccurred(warning);
        }
    }

    /**
     * Wraps the specified format in order to either parse fully a string, or log a warning.
     *
     * @param  <T>    The expected type of parsed values.
     * @param  format The format to use for parsing and formatting.
     * @param  type   The expected type of parsed values.
     * @return A format that logs warnings when it can't parse fully a string.
     */
    protected <T> LoggedFormat<T> createLoggedFormat(final Format format, final Class<T> type) {
        return new FormatAdapter<T>(format, type);
    }

    /**
     * Creates a logged format of the given type.
     *
     * @param  <T>    The expected type of parsed values.
     * @param  type   The expected type of parsed values.
     * @param  format The method to logs as the "caller" when the parsing fails.
     * @return A format that logs warnings when it can't parse fully a string.
     */
    private <T> LoggedFormat<T> createLoggedFormat(final Class<T> type, final String caller) {
        final LoggedFormat<T> format = createLoggedFormat(createFormat(type), type);
        format.setLogger("org.geotoolkit.image.io.metadata");
        format.setCaller(MetadataAccessor.class, caller);
        return format;
    }

    /**
     * Creates the format to use for parsing and formatting dates or number ranges.
     *
     * {@note We use the Canada locale because it is a bit closer to ISO standards than
     *        the US locale (e.g. order of elements in a date) while using the English
     *        symbols (e.g. the dot as a decimal separator instead than coma).}
     *
     * @param  type The expected type of parsed values.
     * @return The format to use.
     */
    private static Format createFormat(final Class<?> type) {
        if (Date.class.isAssignableFrom(type)) {
            final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format;
        }
        if (NumberRange.class.isAssignableFrom(type)) {
            return new RangeFormat(Locale.CANADA);
        }
        throw new IllegalArgumentException(String.valueOf(type));
    }

    /**
     * Parses the given string as an object of the given type. This method should never be invoked,
     * except as a (admitly inefficient) fallback when the {@link IIOMetadata} in use is not an
     * instance of {@code SpatialMetadata} (this usually don't happen).
     *
     * @param  type The expected type of the parsed value.
     * @param  text The text to parse
     * @return The parsed value.
     * @throws ParseException If the parsing failed.
     */
    static <T> T parse(final Class<T> type, final String text) throws ParseException {
        return type.cast(createFormat(type).parseObject(text));
    }

    /**
     * Formats the given object as a string. This method should never be invoked, except as a
     * (admitly inefficient) fallback when the {@link IIOMetadata} in use is not an instance of
     * {@code SpatialMetadata} (this usually don't happen).
     *
     * @param  type The expected type of the value.
     * @param  value The value to format.
     * @return The formatted value.
     */
    static <T> String format(final Class<T> type, final T value) {
        return createFormat(type).format(value);
    }

    /**
     * Returns a standard date format to be shared by {@link MetadataAccessor}.
     * This method creates a new format only when first invoked, and reuses the
     * existing instance on subsequent invocations.
     */
    final LoggedFormat<Date> dateFormat() {
        if (dateFormat == null) {
            dateFormat = createLoggedFormat(Date.class, "getAttributeAsDate");
        }
        return dateFormat;
    }

    /**
     * Returns a standard range format to be shared by {@link MetadataAccessor}.
     * This method creates a new format only when first invoked, and reuses the
     * existing instance on subsequent invocations.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    final LoggedFormat<NumberRange<?>> rangeFormat() {
        if (rangeFormat == null) {
            rangeFormat = (LoggedFormat) createLoggedFormat(NumberRange.class, "getAttributeAsRange");
        }
        return rangeFormat;
    }

    /**
     * Returns a string representation of this metadata, mostly for debugging purpose.
     * The default implementation formats the metadata as a tree similar to the one
     * formatted by {@link MetadataAccessor#toString()}.
     */
    @Override
    public String toString() {
        if (format == null) {
            return "EMPTY";
        }
        return Trees.toString(Trees.xmlToSwing(getAsTree(format.getRootName())));
    }
}
