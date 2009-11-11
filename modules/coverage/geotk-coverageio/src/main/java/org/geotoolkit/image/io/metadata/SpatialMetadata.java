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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

import org.geotoolkit.resources.Errors;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.Localized;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.logging.LoggedFormat;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.SpatialImageWriter;


/**
 * Spatial (usually geographic) informations encoded in an image. This class converts the
 * {@link IIOMetadataNode} elements and attribute values to ISO 19115-2 metadata objects.
 * The tree is expected conform to the {@linkplain SpatialMetadataFormat spatial metadata
 * format} defined in this package. The conversions are performed by the following methods:
 *
 * <blockquote><table>
 *   <tr><th>Method</th><th>Node</th></tr>
 *   <tr>
 *     <td>{@link #getSampleDimensions()}&nbsp;</td>
 *     <td>&nbsp;{@code "ImageDescription/Dimensions"}</td>
 *   </tr>
 * </table></blockquote>
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
     * The metadata format.
     */
    final IIOMetadataFormat format;

    /**
     * The {@link ImageReader} or {@link ImageWriter} that holds the metadata,
     * or {@code null} if none.
     */
    private final Object owner;

    /**
     * The root node to be returned by {@link #getAsTree}.
     */
    private Node root;

    /**
     * The standard date format. Will be created only when first needed.
     */
    private transient LoggedFormat<Date> dateFormat;

    /**
     * Creates an initially empty metadata instance for the given format.
     * The {@code format} argument is usually one of the {@code SpatialMetadataFormat}
     * {@link SpatialMetadataFormat#STREAM STREAM} or {@link SpatialMetadataFormat#IMAGE IMAGE}
     * constants, but other formats are allowed if the structure is compatible or the specialized
     * getter methods are overloaded.
     *
     * @param format The metadata format.
     */
    public SpatialMetadata(final IIOMetadataFormat format) {
        this(format, (Object) null);
    }

    /**
     * Creates an initially empty metadata instance for the given format and reader.
     * The {@code format} argument is usually one of the {@code SpatialMetadataFormat}
     * {@link SpatialMetadataFormat#STREAM STREAM} or {@link SpatialMetadataFormat#IMAGE IMAGE}
     * constants, but other formats are allowed if the structure is compatible or the specialized
     * getter methods are overloaded.
     *
     * @param format The metadata format.
     * @param reader The source image reader, or {@code null} if none.
     */
    public SpatialMetadata(final IIOMetadataFormat format, final ImageReader reader) {
        this(format, (Object) reader);
    }

    /**
     * Creates an initially empty metadata instance for the given format and writer.
     * The {@code format} argument is usually one of the {@code SpatialMetadataFormat}
     * {@link SpatialMetadataFormat#STREAM STREAM} or {@link SpatialMetadataFormat#IMAGE IMAGE}
     * constants, but other formats are allowed if the structure is compatible or the specialized
     * getter methods are overloaded.
     *
     * @param format The metadata format.
     * @param writer The target image writer, or {@code null} if none.
     */
    public SpatialMetadata(final IIOMetadataFormat format, final ImageWriter writer) {
        this(format, (Object) writer);
    }

    /**
     * Creates an initially empty metadata instance for the given format and reader/writer.
     */
    private SpatialMetadata(final IIOMetadataFormat format, final Object owner) {
        super(false, // Can not return or accept a DOM tree using the standard metadata format.
              null,  // There is no native metadata format.
              null,  // There is no native metadata format.
              new String[] {
                  format.getRootName()
              },
              new String[] {
                  "org.geotoolkit.image.io.metadata.SpatialMetadataFormat"
              });
        this.format = format;
        this.owner  = owner;
    }

    /**
     * Returns the list of all {@code dimension} elements under the {@code "ImageDescription/Dimensions"}
     * node. If this node does not exist or has no element, then this method returns an empty list
     * (never {@code null}).
     * <p>
     * The returned list is writable: if an element of that list is modified or if a new element
     * is added, the change will be reflected in the underlying {@link IIOMetadata}.
     *
     * @return The list of every {@code "Dimension"} elements.
     *
     * @since 3.06
     *
     * @todo Current implementation returns an unmodifiable empty list if all cases.
     *       Real implementation will be provided soon.
     */
    public List<SampleDimension> getSampleDimensions() {
        return java.util.Collections.emptyList();
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
     * Checks the format name.
     */
    private void checkFormatName(final String formatName) throws IllegalArgumentException {
        if (!format.getRootName().equals(formatName)) {
            throw new IllegalArgumentException(Errors.getResources(getLocale()).getString(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "formatName", formatName));
        }
    }

    /**
     * Returns the root of a tree of metadata contained within this object according
     * to the conventions defined by the metadata format associated to this instance.
     */
    final Node getAsTree() {
        if (root == null) {
            root = new IIOMetadataNode(format.getRootName());
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
        checkFormatName(formatName);
        return getAsTree();
    }

    /**
     * Alters the internal state of this metadata from a tree whose syntax is defined by
     * the given metadata format. The default implementation simply replaces all existing
     * state with the contents of the given tree.
     *
     * @param formatName The desired metadata format.
     * @param root An XML DOM Node object forming the root of a tree.
     *
     * @todo We need to performs a real merge; this is required by mosaic image readers.
     */
    @Override
    public void mergeTree(final String formatName, final Node root) throws IIOInvalidTreeException {
        checkFormatName(formatName);
        reset();
        this.root = root;
    }

    /**
     * Alters the internal state of this metadata from a tree defined by the specified metadata.
     * The default implementation asks the format described by the {@link SpatialMetadataFormat}
     * argument, then delegates to {@link #mergeTree(String, Node)}.
     *
     * @param  metadata The metadata to merge to this object.
     * @throws IIOInvalidTreeException If the metadata can not be merged.
     */
    public void mergeTree(final IIOMetadata metadata) throws IIOInvalidTreeException {
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
     * Returns {@code false} since this metadata supports some write operations.
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }

    /**
     * Resets all the data stored in this object to default values.
     * All nodes below the root node are discarted.
     */
    @Override
    public void reset() {
        root = null;
    }

    /**
     * Invoked when a warning occured. This method is invoked when some inconsistency has
     * been detected in the geographic metadata. The default implementation delegates to
     * {@link SpatialImageReader#warningOccurred(LogRecord)} if possible, or send the record to
     * the {@code "org.geotoolkit.image.io.metadata"} logger otherwise.
     * <p>
     * Subclasses may override this method if more processing is wanted, or for
     * throwing exception if some warnings should be considered as fatal errors.
     *
     * @param record The warning record to log.
     */
    protected void warningOccurred(final LogRecord record) {
        if (owner instanceof SpatialImageReader) {
            ((SpatialImageReader) owner).warningOccurred(record);
        } else if (owner instanceof SpatialImageWriter) {
            ((SpatialImageWriter) owner).warningOccurred(record);
        } else {
            final Logger logger = Logging.getLogger(SpatialMetadata.class);
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
     * @return A format that log warnings when it can't parse fully a string.
     */
    protected <T> LoggedFormat<T> createLoggedFormat(final Format format, final Class<T> type) {
        return new FormatAdapter<T>(format, type);
    }

    /**
     * Returns a standard date format to be shared by {@link MetadataAccessor}.
     */
    final LoggedFormat<Date> dateFormat() {
        if (dateFormat == null) {
            final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateFormat = createLoggedFormat(format, Date.class);
            dateFormat.setLogger("org.geotoolkit.image.io.metadata");
            dateFormat.setCaller(MetadataAccessor.class, "getAttributeAsDate");
        }
        return dateFormat;
    }

    /**
     * Returns a string representation of this metadata, mostly for debugging purpose.
     */
    @Override
    public String toString() {
        return Trees.toString(Trees.xmlToSwing(getAsTree(format.getRootName())));
    }
}
