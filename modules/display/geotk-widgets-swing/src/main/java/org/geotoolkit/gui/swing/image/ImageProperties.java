/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.gui.swing.image;

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.ParseException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Dimension;
import java.lang.reflect.Array;

import java.awt.Image;
import java.awt.Insets;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.renderable.RenderableImage;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.media.jai.IHSColorSpace;
import javax.media.jai.OperationNode;
import javax.media.jai.PropertySource;
import javax.media.jai.PropertyChangeEmitter;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.OperationDescriptor;

import org.jdesktop.swingx.JXTitledSeparator;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.util.Strings;
import org.geotoolkit.util.NumberRange;
import org.apache.sis.util.Classes;
import org.apache.sis.measure.RangeFormat;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.lang.Debug;

import static java.awt.GridBagConstraints.*;


/**
 * A panel showing the properties of an image. The panel contains the following tabs
 * (some of them may be disabled depending on the image type):
 * <p>
 * <ul>
 *   <li>A summary with informations about the {@linkplain ColorModel color model},
 *       {@linkplain SampleModel sample model}, image size, tile size, <i>etc.</i></li>
 *   <li>A table of (<var>key</var>, <var>value</var>) pairs which are the
 *       {@linkplain RenderedImage#getPropertyNames() properties} associated with the image.
 *       The properties include for example the minimal and maximal pixel values computed by
 *       JAI.</li>
 *   <li>The numerical value of each band in a table, as provided by {@link ImageSampleValues}.</li>
 *   <li>An overview of the image, as provided by {@link ImagePane}.</li>
 * </ul>
 * <p>
 * While this pane works primarily with instances of the {@link RenderedImage} interface, it
 * accepts also instances of {@link RenderableImage} or {@link PropertySource} interfaces.
 * The {@link PropertySource#getProperty(String)} method will be invoked only when a property
 * is first required, in order to avoid the computation of deferred properties before
 * needed. If the source implements also the {@link PropertyChangeEmitter} interface,
 * then this widget will register a listener for property changes. The changes can be
 * emitted from any thread - it doesn't need to be the <cite>Swing</cite> thread.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @see ImageFileProperties
 * @see OperationTreeBrowser
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class ImageProperties extends JComponent implements Dialog {
    /**
     * Index in the {@link #descriptions} array for the text area of the operation name,
     * version and description. If the image is not an instance of {@link OperationNode},
     * then the class name is used. If this panel is an instance of {@link ImageFileProperties},
     * then this will rather be the file name.
     */
    private static final int DESCRIPTION = 0;

    /**
     * Index in the {@link #descriptions} array for the text area of an image property.
     */
    private static final int IMAGE_SIZE=1, TILE_SIZE=2, DATA_TYPE=3, SAMPLE_MODEL=4,
            COLOR_MODEL=5, COLOR_SPACE=6, COLOR_RAMP=7, VALUE_RANGE=8, CRS_NAME=9,
            PIXEL_SIZE=10;

    /**
     * The last {@link #descriptions} index plus one. This is the maximal array length.
     */
    private static final int LAST = 11, LAST_NO_METADATA = VALUE_RANGE;

    /**
     * The first item which may not be present. The labels for all items
     * starting at this index can be enabled or disabled.
     */
    private static final int FIRST_OPTIONAL = COLOR_RAMP;

    /**
     * An array of length {@link #LAST} (at most) of text areas for various image properties.
     */
    private final JLabel[] descriptions;

    /**
     * The label of optional descriptions. Those label may be enabled or disabled.
     */
    private final JLabel[] labelOptionals;

    /**
     * The color bar for {@link IndexColorModel}.
     */
    private final ColorRamp colorRamp;

    /**
     * The table model for image properties, or {@code null} if none.
     * This field is {@code null} - together with {@link #samples} - when this object
     * is actually an instance of the {@link ImageFileProperties} subclass, because the
     * properties tab is replaced by image metadata.
     */
    private final Table properties;

    /**
     * The table for sample values, or {@code null} if none.
     * This field is {@code null} - together with {@link #properties} - when this object
     * is actually an instance of the {@link ImageFileProperties} subclass, because the
     * later will try to load only a small portion of the image.  Since the subsampling
     * is arbitrary, so is the table size - so we are better to not show it.
     */
    private final ImageSampleValues samples;

    /**
     * The viewer for an image quick look.
     */
    protected final ImagePane viewer;

    /**
     * The panel which contains the tabs.
     */
    final JTabbedPane tabs;

    /**
     * The range format. Will be created when first needed.
     */
    private transient RangeFormat rangeFormat;

    /**
     * Creates a new instance of {@code ImageProperties} with no image.
     * One of {@link #setImage(RenderedImage) setImage(...)} methods must
     * be invoked in order to set the properties source.
     */
    public ImageProperties() {
        this((JComponent) null);
    }

    /**
     * Creates a new instance with the given panel as an additional "metadata" tab.
     * This is used for the {@link ImageFileProperties} constructor only.
     */
    ImageProperties(final JComponent metadata) {
        setLayout(new BorderLayout());
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        tabs           = new JTabbedPane();
        colorRamp      = new ColorRamp();
        descriptions   = new JLabel[(metadata != null) ? LAST : LAST_NO_METADATA];
        labelOptionals = new JLabel[descriptions.length - FIRST_OPTIONAL];
        /*
         * Build the informations tab. We use a two-columns layout, with the labels on
         * the left side and the values on the right side. We use a loop because all
         * rows are processed in the same way, with a few exceptions for example in order
         * to add a separator before CRS informations.
         */
        final JPanel info = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        final Insets insets = c.insets;
        c.gridy=0; c.anchor=WEST; c.fill=HORIZONTAL;
        for (int i=0; i<descriptions.length; i++) {
            c.gridx=0;
            final int labelKey;
            switch (i) {
                case DESCRIPTION: {
                    c.insets.left=9; c.weightx=1;    // No need to reset those particular settings.
                    c.gridwidth=2; insets.bottom=15; info.add(descriptions[i] = new JLabel(" "), c);
                    c.gridwidth=1; insets.bottom= 0; // Need to be reset for the next loop execution.
                    continue; // Do not add label.
                }
                case IMAGE_SIZE:   labelKey = Vocabulary.Keys.IMAGE_SIZE;   break;
                case TILE_SIZE:    labelKey = Vocabulary.Keys.TILES_SIZE;   break;
                case DATA_TYPE:    labelKey = Vocabulary.Keys.DATA_TYPE;    break;
                case SAMPLE_MODEL: labelKey = Vocabulary.Keys.SAMPLE_MODEL; break;
                case COLOR_MODEL:  labelKey = Vocabulary.Keys.COLOR_MODEL;  break;
                case COLOR_SPACE:  labelKey = Vocabulary.Keys.COLOR_SPACE;  break;
                case COLOR_RAMP:   labelKey = Vocabulary.Keys.COLORS;       break;
                case VALUE_RANGE:  labelKey = Vocabulary.Keys.VALUE_RANGE;  break;
                case PIXEL_SIZE:   labelKey = Vocabulary.Keys.PIXEL_SIZE;   break;
                case CRS_NAME: {
                    // Add a separator using HTML style instead than setting
                    // the fonts in order to a have consistent looks.
                    final JXTitledSeparator title = new JXTitledSeparator("<html><h3>" +
                            resources.getString(Vocabulary.Keys.COORDINATE_REFERENCE_SYSTEM) + "</h3></html>");
                    c.gridy++;
                    insets.left=9; // No need to reset this particular setting.
                    c.gridwidth=2; info.add(title, c);
                    c.gridwidth=1; // Need to be reset before to add the label.
                    labelKey = Vocabulary.Keys.DESCRIPTION;
                    break;
                }
                case LAST: // Just for a compile-time check of wrong values.
                default: throw new AssertionError(i);
            }
            final JLabel label = new JLabel(resources.getLabel(labelKey));
            if (i >= FIRST_OPTIONAL) {
                labelOptionals[i - FIRST_OPTIONAL] = label;
            }
            /*
             * At this point we are ready to add the (label, value) pair.
             * We will make a special case for the color ramp, where the
             * value is not a label.
             */
            c.gridy++; c.weightx=0; insets.left=40; info.add(label, c);
            c.gridx=1; c.weightx=1; insets.left= 9;
            final JComponent description;
            switch (i) {
                case COLOR_RAMP: {
                    c.anchor=CENTER; insets.right=6;
                    description = colorRamp;
                    description.setOpaque(false);
                    break;
                }
                default: {
                    description = descriptions[i] = new JLabel();
                    break;
                }
            }
            info.setOpaque(false);
            info.add(description, c);
            label.setLabelFor(description);
            c.anchor=WEST; insets.right=0;
        }
        tabs.addTab(resources.getString(Vocabulary.Keys.INFORMATIONS), info);
        /*
         * Build the image's properties tab and the image sample values tab.
         * In the particular case of ImageFileProperties, those two tabs are
         * replaced by a metadata tab.  We do not show the sample values tab
         * for image file because the ImageFileProperties widget will try to
         * load only a small portion of image data.
         */
        if (metadata == null) {
            properties = new Table(resources);
            final JTable table = new JTable(properties);
            table.setAutoCreateRowSorter(true);
            tabs.addTab(resources.getString(Vocabulary.Keys.PROPERTIES), new JScrollPane(table));
            samples = new ImageSampleValues();
            tabs.addTab(resources.getString(Vocabulary.Keys.PIXELS), samples);
        } else {
            metadata.setOpaque(false);
            properties = null;
            samples    = null;
            tabs.addTab(resources.getString(Vocabulary.Keys.METADATA), metadata);
        }
        /*
         * Build the image preview tab.
         */
        viewer = new ImagePane();
        viewer.setPaintingWhileAdjusting(true);
        tabs.addTab(resources.getString(Vocabulary.Keys.PREVIEW), viewer.createScrollPane());

        add(tabs, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 400));
    }

    /**
     * Create a new instance of {@code ImageProperties} for the specified
     * rendered image.
     *
     * @param image The image, or {@code null} if none.
     */
    public ImageProperties(final RenderedImage image) {
        this();
        if (image != null) {
            setImage(image);
        }
    }

    /**
     * Sets the operation name, description and version for the given image. If the image is
     * an instance of {@link OperationNode}, then a description of the operation will be fetch
     * from its resources bundle.
     * <p>
     * This method accepts also instances of {@link ImageReaderWriterSpi},
     * for the specific needs of {@link ImageFileProperties} only.
     *
     * @param image The image, or {@code null} if none.
     */
    final void setOperationDescription(final Object image) {
        final Locale     locale    = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);
        String name        = resources.getString(Vocabulary.Keys.UNDEFINED);
        String version     = null;
        String description = null;
        String extra       = null;
        if (image instanceof OperationNode) {
            /*
             * JAI operation - get the information from the descriptor.
             * We put the version number just below the operation name,
             * before the description, since the version applies to the
             * operation.
             */
            final String mode;
            final RegistryElementDescriptor descriptor;
            final OperationNode operation = (OperationNode) image;
            name       = operation.getOperationName();
            mode       = operation.getRegistryModeName();
            descriptor = operation.getRegistry().getDescriptor(mode, name);
            if (descriptor instanceof OperationDescriptor) {
                final ResourceBundle bundle;
                bundle      = ((OperationDescriptor) descriptor).getResourceBundle(locale);
                name        = bundle   .getString("LocalName");
                description = bundle   .getString("Description");
                version     = resources.getString(Vocabulary.Keys.VERSION_$1,
                              bundle   .getString("Version")) + " (" +
                              bundle   .getString("Vendor") + ')';
                name = resources.getString(Vocabulary.Keys.OPERATION_$1, name);
            }
        } else if (image instanceof ImageReaderWriterSpi) {
            /*
             * Image Reader or Writer provider - for ImageFileProperties only.
             * We put the version number after the description, since the description
             * is actually the decodeur implementation. The "name" is the MIME type,
             * which doesn't have a version number.
             */
            final ImageReaderWriterSpi spi = (ImageReaderWriterSpi) image;
            description = spi.getDescription(locale);
            extra = resources.getString(Vocabulary.Keys.VERSION_$1,
                    spi.getVersion()) + " (" + spi.getVendorName() + ')';
            String[] names = spi.getMIMETypes();
            if (names != null && names.length != 0) {
                name = names[0];
            } else {
                names = spi.getFormatNames();
                if (names != null && names.length != 0) {
                    name = names[0];
                }
            }
        } else if (image != null) {
            /*
             * Unknown case - typically a BufferedImage.
             */
            name = Classes.getShortClassName(image);
            name = resources.getString(Vocabulary.Keys.IMAGE_CLASS_$1, name);
        }
        /*
         * Formats the description field using the information fetched above.
         */
        final StringBuilder html = new StringBuilder("<html>");
        html.append("<h2>").append(name).append("</h2>");
        if (version != null) {
            html.append("<p>").append(version).append("</p>");
        }
        if (description != null) {
            html.append("<p><cite>").append(description).append("</cite></p>");
        }
        if (extra != null) {
            html.append("<p>").append(extra).append("</p>");
        }
        descriptions[DESCRIPTION].setText(html.append("</html>").toString());
    }

    /**
     * Sets all text fields to {@code null}. This method do not set the {@link #properties}
     * table; this is left to the caller.
     */
    void clear() {
        for (int i=0; i<descriptions.length; i++) {
            final JLabel description = descriptions[i];
            if (description != null) {
                description.setText(null);
            }
        }
        for (int i=0; i<labelOptionals.length; i++) {
            labelOptionals[i].setEnabled(false);
        }
        colorRamp.setColors((IndexColorModel) null);
    }

    /**
     * Sets the {@linkplain PropertySource property source} for this widget. If the source is a
     * {@linkplain RenderedImage rendered} or a {@linkplain RenderableImage renderable} image,
     * then the widget will be set as if the most specific flavor of {@code setImage(...)}
     * was invoked.
     *
     * @param image The image, or {@code null} if none.
     */
    public void setImage(final PropertySource image) {
        if (image instanceof RenderedImage) {
            setImage((RenderedImage) image);
            return;
        }
        if (image instanceof RenderableImage) {
            setImage((RenderableImage) image);
            return;
        }
        clear();
        setOperationDescription(image);
        if (properties != null) {
            properties.setSource(image);
            samples   .setImage((RenderedImage) null);
        }
        viewer.setImage((RenderedImage) null);
    }

    /**
     * Sets the specified {@linkplain RenderableImage renderable image} as the properties source.
     *
     * @param image The image, or {@code null} if none.
     */
    public void setImage(final RenderableImage image) {
        clear();
        if (image != null) {
            final Vocabulary resources = Vocabulary.getResources(getLocale());
            descriptions[IMAGE_SIZE].setText(resources.getString(
                    Vocabulary.Keys.SIZE_$2, image.getWidth(), image.getHeight()));
        }
        setOperationDescription(image);
        if (properties != null) {
            properties.setSource(image);
            samples   .setImage ((RenderedImage) null);
        }
        viewer.setImage(image);
    }

    /**
     * Sets the specified {@linkplain RenderedImage rendered image} as the properties source.
     *
     * @param image The image, or {@code null} if none.
     */
    public void setImage(final RenderedImage image) {
        if (image == null) {
            clear();
        } else {
            setImageDescription(image.getColorModel(), image.getSampleModel(),
                    image.getWidth(),     image.getHeight(),
                    image.getTileWidth(), image.getTileHeight(),
                    image.getNumXTiles(), image.getNumYTiles());
        }
        setOperationDescription(image);
        if (properties != null) {
            properties.setSource(image);
            samples   .setImage (image);
        }
        viewer.setImage(image);
    }

    /**
     * Sets the content of the description panel, not including the part which
     * is specific to the image operations and the part which depends on metadata.
     */
    final void setImageDescription(final ColorModel cm, final SampleModel sm,
            final int width, final int height, final int tileWidth, final int tileHeight,
            final int numXTiles, final int numYTiles)
    {
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        final IndexColorModel icm = (cm instanceof IndexColorModel) ? (IndexColorModel) cm : null;
        for (int i=IMAGE_SIZE; i<COLOR_RAMP; i++) {
            final String text;
            switch (i) {
                case IMAGE_SIZE: {
                    final Object numBands = (sm != null) ? sm.getNumBands() : resources.getString(Vocabulary.Keys.UNDEFINED);
                    text = resources.getString(Vocabulary.Keys.IMAGE_SIZE_$3, width, height, numBands);
                    break;
                }
                case TILE_SIZE: {
                    text = resources.getString(Vocabulary.Keys.TILE_SIZE_$4, numXTiles, numYTiles, tileWidth, tileHeight);
                    break;
                }
                case DATA_TYPE: {
                    text = getDataType(sm != null ? sm.getDataType() : DataBuffer.TYPE_UNDEFINED, cm, resources);
                    break;
                }
                case SAMPLE_MODEL: {
                    text = formatClassName(sm, resources);
                    break;
                }
                case COLOR_MODEL: {
                    text = formatClassName(cm, resources);
                    break;
                }
                case COLOR_SPACE: {
                    text = getColorSpace(cm, resources);
                    break;
                }
                default: throw new AssertionError(i);
            }
            descriptions[i].setText(text);
        }
        colorRamp.setColors(icm);
        labelOptionals[COLOR_RAMP - FIRST_OPTIONAL].setEnabled(icm != null);
    }

    /**
     * Sets the content of the geospatial description panel. This is the same panel than the
     * one modified by the previous {@code setImageDecription(...)} method, but this time for
     * the geospatial information part. Those informations are typically extracted from the
     * image metadata.
     *
     * @param crs The coordinate reference system, or {@code null}.
     * @param cellSize The cell size as a string, or {@code null}.
     * @param values The range of geophysics values, or {@code null} if none.
     *
     * @since 3.08
     */
    @SuppressWarnings("fallthrough")
    final void setGeospatialDescription(final CoordinateReferenceSystem crs,
            final String cellSize, final NumberRange<?> values)
    {
        for (int i=VALUE_RANGE; i<LAST; i++) {
            String text = null;
            switch (i) {
                case VALUE_RANGE: {
                    if (values != null) {
                        if (rangeFormat == null) {
                            rangeFormat = new RangeFormat(getLocale());
                        }
                        text = rangeFormat.format(values);
                    }
                    break;
                }
                case CRS_NAME: {
                    if (crs != null) {
                        text = crs.getName().getCode();
                    }
                    break;
                }
                case PIXEL_SIZE: {
                    text = cellSize;
                    break;
                }
            }
            descriptions[i].setText(text);
            labelOptionals[i - FIRST_OPTIONAL].setEnabled(true);
        }
    }

    /**
     * Sets whatever the geospatial descriptions are enabled or not.
     * Note that this is automatically set to {@code true} by the above method.
     */
    final void setGeospatialDescription(final boolean enabled) {
        for (int i=VALUE_RANGE; i<LAST; i++) {
            labelOptionals[i - FIRST_OPTIONAL].setEnabled(enabled);
        }
    }

    /**
     * Returns a string representation for the given data type.
     *
     * @param  type The data type (one of {@link DataBuffer} constants).
     * @param  cm The color model for computing the pixel size in bits, or {@code null}.
     * @param  resources The resources to use for formatting the type.
     * @return The data type as a localized string.
     */
    @SuppressWarnings("fallthrough")
    private static String getDataType(final int type, final ColorModel cm, final Vocabulary resources) {
        final int key;
        switch (type) {
            case DataBuffer.TYPE_BYTE:      // Fall through
            case DataBuffer.TYPE_USHORT:    key = Vocabulary.Keys.UNSIGNED_INTEGER_$2; break;
            case DataBuffer.TYPE_SHORT:     // Fall through
            case DataBuffer.TYPE_INT:       key = Vocabulary.Keys.SIGNED_INTEGER_$1; break;
            case DataBuffer.TYPE_FLOAT:     // Fall through
            case DataBuffer.TYPE_DOUBLE:    key = Vocabulary.Keys.REAL_NUMBER_$1; break;
            case DataBuffer.TYPE_UNDEFINED: // Fall through
            default: return resources.getString(Vocabulary.Keys.UNDEFINED);
        }
        final Integer  typeSize = DataBuffer.getDataTypeSize(type);
        final Integer pixelSize = (cm != null) ? cm.getPixelSize() : typeSize;
        return resources.getString(key, typeSize, pixelSize);
    }

    /**
     * Returns the name of the color space for the given color model.
     *
     * @param  cm The color model, or {@code null} if undefined.
     * @param  resources The resources to use for formatting the type.
     * @return The name of the color space.
     */
    private static String getColorSpace(final ColorModel cm, final Vocabulary resources) {
        if (cm != null) {
            final ColorSpace cs = cm.getColorSpace();
            if (cs != null) {
                final String text;
                switch (cs.getType()) {
                    case ColorSpace.TYPE_GRAY: {
                        text = resources.getString(Vocabulary.Keys.GRAY_SCALE);
                        break;
                    }
                    case ColorSpace.TYPE_RGB:  text = "RGB";  break;
                    case ColorSpace.TYPE_CMYK: text = "CMYK"; break;
                    case ColorSpace.TYPE_HLS:  text = "HLS";  break;
                    case ColorSpace.TYPE_HSV: {
                        text = (cs instanceof IHSColorSpace) ? "IHS" : "HSV";
                        break;
                    }
                    default: {
                        text = resources.getString(Vocabulary.Keys.UNKNOWN);
                        break;
                    }
                }
                return text + " (" + resources.getString(
                        Vocabulary.Keys.COMPONENT_COUNT_$1, cs.getNumComponents()) + ')';
            }
        }
        return resources.getString(Vocabulary.Keys.UNDEFINED);
    }

    /**
     * Split a class name into a more human readable sentence
     * (e.g. "PixelInterleavedSampleModel" into "Pixel interleaved sample model").
     *
     * @param  object The object to format, or {@code null} if undefined.
     * @param  resources The resources to use for formatting localized text.
     * @return The object class name.
     */
    private static String formatClassName(final Object object, final Vocabulary resources) {
        if (object == null) {
            return resources.getString(Vocabulary.Keys.UNDEFINED);
        }
        final String name = Classes.getShortClassName(object);
        final StringBuilder buffer = Strings.camelCaseToWords(name, true);
        long numColors = 0;
        if (object instanceof IndexColorModel) {
            numColors = ((IndexColorModel) object).getMapSize();
        } else if (object instanceof ColorModel) {
            final ColorModel cm = (ColorModel) object;
            final int[] sizes = cm.getComponentSize();
            if (sizes != null) {
                numColors = 1;
                // numColorComponents should be either sizes.length or sizes.length - 1,
                // depending if there is an alpha channel or not. We want to ignore alpha.
                for (int i=cm.getNumColorComponents(); --i>=0;) {
                    numColors *= 1L << sizes[i];
                }
            }
        }
        if (numColors != 0) {
            buffer.append(" (").append(resources.getString(Vocabulary.Keys.COLOR_COUNT_$1, numColors)).append(')');
        }
        return buffer.toString().trim();
    }

    /**
     * The table model for image's properties. The image can actually be any of
     * {@link PropertySource}, {@link RenderedImage} or {@link RenderableImage}
     * interface. The method {@link PropertySource#getProperty} will be invoked
     * only when a property is first required, in order to avoid the computation
     * of deferred properties before needed. If the source implements also the
     * {@link PropertyChangeEmitter} interface, then this table will be registered
     * as a listener for property changes. The changes can be emitted from any thread,
     * which may or may not be the <cite>Swing</cite> thread.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.3
     * @module
     *
     * @todo Check for {@code WritablePropertySource} and make cells editable accordingly.
     */
    @SuppressWarnings("serial")
    private static final class Table extends AbstractTableModel implements PropertyChangeListener {
        /**
         * The resources for formatting localized strings.
         */
        private final Vocabulary resources;

        /**
         * The property sources. Usually (but not always) the same object than
         * {@link #changeEmitter}. May be {@code null} if no source has been set.
         */
        private PropertySource source;

        /**
         * The property change emitter, or {@code null} if none. Usually (but not always)
         * the same object than {@link #source}.
         */
        private PropertyChangeEmitter changeEmitter;

        /**
         * The properties names, or {@code null} if none.
         */
        private String[] names;

        /**
         * Constructs a default table with no properties source. The method {@link #setSource}
         * must be invoked after the construction in order to display some image's properties.
         *
         * @param resources The resources for formatting localized strings.
         */
        public Table(final Vocabulary resources) {
            this.resources = resources;
        }

        /**
         * Wraps the specified {@link RenderedImage} into a {@link PropertySource}.
         */
        private static PropertySource wrap(final RenderedImage image) {
            return new PropertySource() {
                @Override public String[] getPropertyNames() {
                    return image.getPropertyNames();
                }
                @Override public String[] getPropertyNames(final String prefix) {
                    // TODO: Not the real answer, but this method
                    // is not needed by this Table implementation.
                    return getPropertyNames();
                }
                @Override public Class<?> getPropertyClass(final String name) {
                    return null;
                }
                @Override public Object getProperty(final String name) {
                    return image.getProperty(name);
                }
            };
        }

        /**
         * Wraps the specified {@link RenderableImage} into a {@link PropertySource}.
         */
        private static PropertySource wrap(final RenderableImage image) {
            return new PropertySource() {
                @Override public String[] getPropertyNames() {
                    return image.getPropertyNames();
                }
                @Override public String[] getPropertyNames(final String prefix) {
                    // TODO: Not the real answer, but this method
                    // is not needed by this Table implementation.
                    return getPropertyNames();
                }
                @Override public Class<?> getPropertyClass(final String name) {
                    return null;
                }
                @Override public Object getProperty(final String name) {
                    return image.getProperty(name);
                }
            };
        }

        /**
         * Sets the source as a {@link PropertySource}, a {@link RenderedImage} or a
         * {@link RenderableImage}. If the source implements the {@link PropertyChangeEmitter}
         * interface, then this table will be registered as a listener for property changes.
         * The changes can be emitted from any thread (may or may not be the Swing thread).
         *
         * @param image The properties source, or {@code null} for removing any source.
         */
        public void setSource(final Object image) {
            if (image == source) {
                return;
            }
            if (changeEmitter != null) {
                changeEmitter.removePropertyChangeListener(this);
                changeEmitter = null;
            }
            if (image instanceof PropertySource) {
                source = (PropertySource) image;
            } else if (image instanceof RenderedImage) {
                source = wrap((RenderedImage) image);
            } else if (image instanceof RenderableImage) {
                source = wrap((RenderableImage) image);
            } else {
                source = null;
            }
            names = (source!=null) ? source.getPropertyNames() : null;
            if (image instanceof PropertyChangeEmitter) {
                changeEmitter = (PropertyChangeEmitter) image;
                changeEmitter.addPropertyChangeListener(this);
            }
            fireTableDataChanged();
        }

        /**
         * Returns the number of rows, which is equals to the number of properties.
         */
        @Override
        public int getRowCount() {
            return (names!=null) ? names.length : 0;
        }

        /**
         * Returns the number of columns, which is 2 (the property name and its value).
         */
        @Override
        public int getColumnCount() {
            return 2;
        }

        /**
         * Returns the column name for the given index.
         */
        @Override
        public String getColumnName(final int column) {
            final int key;
            switch (column) {
                case 0: key=Vocabulary.Keys.NAME;  break;
                case 1: key=Vocabulary.Keys.VALUE; break;
                default: throw new IndexOutOfBoundsException(String.valueOf(column));
            }
            return resources.getString(key);
        }

        /**
         * Returns the most specific superclass for all the cell values in the column.
         */
        @Override
        public Class<?> getColumnClass(final int column) {
            switch (column) {
                case 0: return String.class;
                case 1: return Object.class;
                default: throw new IndexOutOfBoundsException(String.valueOf(column));
            }
        }

        /**
         * Returns the property for the given cell.
         *
         * @param  row The row index.
         * @param  column The column index.
         * @return The cell value at the given index.
         * @throws IndexOutOfBoundsException if the row or the column is out of bounds.
         */
        @Override
        public Object getValueAt(int row, int column) throws IndexOutOfBoundsException {
            final String name = names[row];
            switch (column) {
                case 0: {
                    return name;
                }
                case 1: {
                    Object value = source.getProperty(name);
                    if (value == Image.UndefinedProperty) {
                        value = resources.getString(Vocabulary.Keys.UNDEFINED);
                    }
                    return expandArray(value);
                }
                default: {
                    throw new IndexOutOfBoundsException(String.valueOf(column));
                }
            }
        }

        /**
         * If the specified object is an array, enumerate the array components.
         * Otherwise, returns the object unchanged. This method is sligtly different
         * than {@link java.util.Arrays#toString(Object[])} in that it expands inner
         * array components recursively.
         */
        private static Object expandArray(final Object array) {
            if (array != null && array.getClass().isArray()) {
                final StringBuilder buffer = new StringBuilder();
                buffer.append('{');
                final int length = Array.getLength(array);
                for (int i=0; i<length; i++) {
                    if (i != 0) {
                        buffer.append(", ");
                    }
                    buffer.append(expandArray(Array.get(array, i)));
                }
                buffer.append('}');
                return buffer.toString();
            }
            return array;
        }

        /**
         * Invoked when a property changed. This method find the row for the modified
         * property and fire a table change event.
         *
         * @param The property change event.
         */
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            /*
             * Make sure that we are running in the Swing thread.
             */
            if (!EventQueue.isDispatchThread()) {
                EventQueue.invokeLater(new Runnable() {
                    @Override public void run() {
                        propertyChange(event);
                    }
                });
                return;
            }
            /*
             * Find the rows for the modified property, and fire a "table updated' event.
             */
            final String name = event.getPropertyName();
            int first = getRowCount(); // Past the last row.
            int last  = -1;            // Before the first row.
            if (name == null) {
                last  = first-1;
                first = 0;
            } else {
                for (int i=first; --i>=0;) {
                    if (names[i].equalsIgnoreCase(name)) {
                        first = i;
                        if (last < 0) {
                            last = i;
                        }
                    }
                }
            }
            if (first <= last) {
                fireTableRowsUpdated(first, last);
            }
        }
    }

    /**
     * Forces the current values to be taken from the editable fields and set them as the
     * current values. The default implementation does nothing since there is no editable
     * fields in this widget.
     *
     * @since 3.12
     */
    @Override
    public void commitEdit() throws ParseException {
    }

    /**
     * {@inheritDoc}
     *
     * @param  title The dialog box title, or {@code null} for a default title.
     *
     * @since 3.05
     */
    @Override
    public boolean showDialog(final Component owner, String title) {
        if (title == null) {
            title = Vocabulary.getResources(getLocale()).getString(Vocabulary.Keys.PROPERTIES);
        }
        return SwingUtilities.showDialog(owner, this, title);
    }

    /**
     * Shows the properties for the specified rendered image in a frame.
     * This convenience method is mostly a helper for debugging purpose.
     *
     * @param image The image to display in a frame.
     *
     * @since 3.05
     */
    @Debug
    public static void show(final RenderedImage image) {
        SwingUtilities.show(new ImageProperties(image), Vocabulary.format(Vocabulary.Keys.PROPERTIES));
    }
}
