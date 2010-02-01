/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
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
import java.awt.geom.AffineTransform;
import java.lang.reflect.Array;

import java.awt.Image;
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
import javax.measure.unit.Unit;

import org.jdesktop.swingx.JXTitledSeparator;

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.internal.StringUtilities;
import org.geotoolkit.internal.SwingUtilities;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.Utilities;

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
 * @version 3.08
 *
 * @see ImageFileProperties
 * @see OperationTreeBrowser
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class ImageProperties extends JPanel implements Dialog {
    /**
     * The operation name, version and description. If the image is not an instance of
     * {@link OperationNode}, then the class name is used. If this panel is an instance
     * of {@link ImageFileProperties}, then this will rather be the file name.
     */
    private final JLabel description;

    /**
     * The text area for image size.
     */
    private final JLabel imageSize;

    /**
     * The text area for tile size.
     */
    private final JLabel tileSize;

    /**
     * The text area for sample type (e.g. "8 bits unsigned integer".
     */
    private final JLabel dataType;

    /**
     * The text area for the sample model.
     */
    private final JLabel sampleModel;

    /**
     * The text area for the color model.
     */
    private final JLabel colorModel;

    /**
     * The text area for the color space.
     */
    private final JLabel colorSpace;

    /**
     * The color bar for {@link IndexColorModel}.
     */
    private final ColorRamp colorRamp;

    /**
     * The label for {@link #colorRamp}.
     */
    private final JLabel colorRampLabel;

    /**
     * The text area for the CRS, pixel size and range of values,
     * or {@code null} if there is no metadata to show.
     *
     * @since 3.08
     */
    private final JLabel crsName, pixelSize, valueRange;

    /**
     * The table model for image's properties.
     */
    private final Table properties;

    /**
     * The table for sample values.
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
     * Creates a new instance of {@code ImageProperties} with no image.
     * One of {@link #setImage(RenderedImage) setImage(...)} methods must
     * be invoked in order to set the properties source.
     */
    public ImageProperties() {
        this((JPanel) null);
    }

    /**
     * Creates a new instance with the given panel as an additional "metadata" tab.
     * This is used for the {@link ImageFileProperties} constructor only.
     */
    ImageProperties(final JPanel metadata) {
        super(new BorderLayout());
        description = new JLabel(" ");
        imageSize   = new JLabel();
        tileSize    = new JLabel();
        dataType    = new JLabel();
        sampleModel = new JLabel();
        colorModel  = new JLabel();
        colorSpace  = new JLabel();
        colorRamp   = new ColorRamp();
        tabs        = new JTabbedPane();
        if (metadata != null) {
            crsName    = new JLabel();
            pixelSize  = new JLabel();
            valueRange = new JLabel();
        } else {
            crsName    = null;
            pixelSize  = null;
            valueRange = null;
        }
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        final GridBagConstraints c = new GridBagConstraints();
        colorRampLabel = getLabel(Vocabulary.Keys.COLORS, resources);
        /*
         * Build the informations tab.
         */
        if (true) {
            final JPanel panel = new JPanel(new GridBagLayout());
            c.anchor=WEST; c.fill=HORIZONTAL; c.insets.left=9;
            c.gridx=0; c.gridwidth=2; c.weightx=1; c.insets.bottom=15;
            c.gridy=0; panel.add(description, c);

            final int ytop = c.gridy;
            c.gridwidth=1; c.weightx=0; c.insets.bottom=0; c.insets.left=40;
            c.gridy++; panel.add(getLabel(Vocabulary.Keys.IMAGE_SIZE,   resources), c);
            c.gridy++; panel.add(getLabel(Vocabulary.Keys.TILES_SIZE,   resources), c);
            c.gridy++; panel.add(getLabel(Vocabulary.Keys.DATA_TYPE,    resources), c);
            c.gridy++; panel.add(getLabel(Vocabulary.Keys.SAMPLE_MODEL, resources), c);
            c.gridy++; panel.add(getLabel(Vocabulary.Keys.COLOR_MODEL,  resources), c);
            c.gridy++; panel.add(getLabel(Vocabulary.Keys.COLOR_SPACE,  resources), c);
            c.gridy++; panel.add(colorRampLabel, c);

            if (metadata != null) {
                c.gridy++; panel.add(getLabel(Vocabulary.Keys.VALUE_RANGE, resources), c);

                // Use HTML style instead than setting the fonts in order to a have consistent looks.
                final JXTitledSeparator title = new JXTitledSeparator("<html><h3>" +
                        resources.getString(Vocabulary.Keys.COORDINATE_REFERENCE_SYSTEM) + "</h3></html>");

                c.gridwidth=2; c.insets.left=9;
                c.gridy++; panel.add(title, c);
                c.gridwidth=1; c.insets.left=40;
                c.gridy++; panel.add(getLabel(Vocabulary.Keys.DESCRIPTION, resources), c);
                c.gridy++; panel.add(getLabel(Vocabulary.Keys.PIXEL_SIZE,  resources), c);
            }

            c.gridx=1; c.gridy=ytop; c.weightx=1; c.insets.left=9;
            c.gridy++; panel.add(imageSize,   c);
            c.gridy++; panel.add(tileSize,    c);
            c.gridy++; panel.add(dataType,    c);
            c.gridy++; panel.add(sampleModel, c);
            c.gridy++; panel.add(colorModel,  c);
            c.gridy++; panel.add(colorSpace,  c);
            c.gridy++; c.anchor=CENTER; c.insets.right=6;

            panel.add(colorRamp, c);
            tabs.addTab(resources.getString(Vocabulary.Keys.INFORMATIONS), panel);

            if (metadata != null) {
                c.gridy++; panel.add(valueRange, c);
                c.gridy++; // Skip the title.
                c.anchor=WEST; c.insets.right=0; c.insets.bottom=0;
                c.gridy++; panel.add(crsName,    c);
                c.gridy++; panel.add(pixelSize,  c);
            }
        }
        /*
         * Build the image's properties tab and the image sample value tab.
         * In the particular case of ImageFileProperties, those two tabs
         * are replaced by a metadata tab.
         */
        if (metadata == null) {
            properties = new Table(resources);
            final JTable table = new JTable(properties);
            table.setAutoCreateRowSorter(true);
            tabs.addTab(resources.getString(Vocabulary.Keys.PROPERTIES), new JScrollPane(table));
            samples = new ImageSampleValues();
            tabs.addTab(resources.getString(Vocabulary.Keys.PIXELS), samples);
        } else {
            properties = null;
            samples    = null;
            tabs.addTab(resources.getString(Vocabulary.Keys.METADATA), metadata);
        }
        /*
         * Build the image preview tab.
         */
        if (true) {
            viewer = new ImagePane();
            viewer.setPaintingWhileAdjusting(true);
            tabs.addTab(resources.getString(Vocabulary.Keys.PREVIEW), viewer.createScrollPane());
        }
        add(tabs, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 400));
    }

    /**
     * Returns the localized label for the given key.
     */
    private static JLabel getLabel(final int key, final Vocabulary resources) {
        return new JLabel(resources.getLabel(key));
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
        this.description.setText(html.append("</html>").toString());
    }

    /**
     * Set all text fields to {@code null}. This method do not set the {@link #properties}
     * table; this is left to the caller.
     */
    void clear() {
        imageSize  .setText(null);
        tileSize   .setText(null);
        dataType   .setText(null);
        sampleModel.setText(null);
        colorModel .setText(null);
        colorSpace .setText(null);
        colorRamp  .setColors((IndexColorModel)null);
        if (crsName != null) {
            crsName   .setText(null);
            pixelSize .setText(null);
            valueRange.setText(null);
        }
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
            imageSize.setText(resources.getString(Vocabulary.Keys.SIZE_$2,
                    image.getWidth(), image.getHeight()));
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
        imageSize  .setText(resources.getString(Vocabulary.Keys.IMAGE_SIZE_$3, width, height,
                (sm != null) ? sm.getNumBands() : resources.getString(Vocabulary.Keys.UNDEFINED)));
        tileSize   .setText(resources.getString(Vocabulary.Keys.TILE_SIZE_$4, numXTiles, numYTiles, tileWidth, tileHeight));
        dataType   .setText(getDataType(sm != null ? sm.getDataType() : DataBuffer.TYPE_UNDEFINED, cm, resources));
        sampleModel.setText(formatClassName(sm, resources));
        colorModel .setText(formatClassName(cm, resources));
        colorSpace .setText(getColorSpace  (cm, resources));
        colorRamp  .setColors(icm);
        colorRampLabel.setEnabled(icm != null);
    }

    /**
     * Sets the content of the geospatial description panel. This is the same panel than the
     * one modified by the previous {@code setImageDecription(...)} method, but this time for
     * the geospatial information part. Those informations are typically extracted from the
     * image metadata.
     *
     * @param gridToCRS The transform from grid coordinates to CRS coordinates, or {@code null}.
     * @param values The range of geophysics values, or {@code null} if none.
     *
     * @since 3.08
     */
    @SuppressWarnings("fallthrough")
    final void setGeospatialDescription(final CoordinateReferenceSystem crs,
            final AffineTransform gridToCRS, final NumberRange<?> values)
    {
        String text = null;
        if (crs != null) {
            text = crs.getName().getCode();
        }
        crsName.setText(text);
        text = null;
        if (gridToCRS != null) {
            final double scaleX = XAffineTransform.getScaleX0(gridToCRS);
            final double scaleY = XAffineTransform.getScaleX0(gridToCRS);
            Unit<?> xUnit = null, yUnit = null;
            if (crs != null) {
                final CoordinateSystem cs = crs.getCoordinateSystem();
                if (cs != null) switch (cs.getDimension()) {
                    default: yUnit = cs.getAxis(1).getUnit(); // Fall through
                    case 1:  xUnit = cs.getAxis(0).getUnit(); // Fall through
                    case 0:  break;
                }
            }
            final boolean sameUnits = Utilities.equals(xUnit, yUnit);
            final StringBuilder buffer = new StringBuilder();
            if (scaleX != scaleY || !sameUnits) {
                buffer.append(scaleX);
                if (!sameUnits && xUnit != null) {
                    buffer.append(' ').append(xUnit);
                }
                buffer.append(" × ");
            }
            buffer.append(scaleY);
            if (yUnit != null) {
                buffer.append(' ').append(yUnit);
            }
            text = buffer.toString();
        }
        pixelSize.setText(text);
        valueRange.setText(values != null ? values.toString() : null);
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
    private static final String getColorSpace(final ColorModel cm, final Vocabulary resources) {
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
                        text = resources.getString(Vocabulary.Keys.UNKNOW);
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
     * Split a class name into a more human readeable sentence
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
        final StringBuilder buffer = StringUtilities.separateWords(name);
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
        return SwingUtilities.showOptionDialog(owner, this, title);
    }

    /**
     * Shows the properties for the specified rendered image in a frame.
     * This convenience method is mostly a helper for debugging purpose.
     *
     * @param image The image to display in a frame.
     *
     * @since 3.05
     */
    public static void show(final RenderedImage image) {
        SwingUtilities.show(new ImageProperties(image), Vocabulary.format(Vocabulary.Keys.PROPERTIES));
    }
}
