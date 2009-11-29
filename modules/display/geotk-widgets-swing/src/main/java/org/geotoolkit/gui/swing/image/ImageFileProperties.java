/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.event.IIOReadWarningListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.EventQueue;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.DefaultListModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.geotoolkit.gui.swing.ExceptionMonitor;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.SwingUtilities;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.XArrays;


/**
 * A panel showing the properties of an image <u>file</u>. This is different than
 * {@link ImageProperties}, which shows the properties of a {@code RenderedImage}.
 * The panel contains the following tabs:
 * <p>
 * <ul>
 *   <li>A summary with informations about the {@linkplain java.awt.image.ColorModel color model},
 *       {@linkplain java.awt.image.SampleModel sample model}, image size, tile size, <i>etc.</i></li>
 *   <li>The image metadata, as provided by {@link IIOMetadataPanel}.</li>
 *   <li>An overview of the image, as provided by {@link ImagePane}.</li>
 * </ul>
 * <p>
 * All {@code setImage} methods defined in this class may be slow because they involve I/O
 * operations. It is recommanded to invoke them from an other thread than the Swing thread.
 *
 * {@section Using this component together with a File Chooser}
 * This component can be registered to a {@link JFileChooser} for listening to change events.
 * When the file selection change, the {@link #propertyChange(PropertyChangeEvent)} method is
 * automatically invoked. The default implementation invokes in turn {@link #setImage(File)} in
 * a background thread. This allows this {@code ImageFileProperties} to be updated automatically
 * when the user selection changed. Example:
 *
 * {@preformat java
 *     ImageFileChooser chooser = new ImageFileChooser("png");
 *     ImageFileProperties properties = new ImageFileProperties();
 *     chooser.addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, properties);
 *     //
 *     // Add the FileChooser and ImageFileProperties to some panel with the layout constraints
 *     // of your choice. The example below uses BorderLayout in such a way that, when resizing
 *     // the panel, only the properties pane is resized.
 *     //
 *     JPanel panel = new JPanel(new BorderLayout());
 *     panel.add(this, BorderLayout.WEST);
 *     panel.add(properties, BorderLayout.CENTER);
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @see ImageProperties
 * @see ImageFileChooser
 *
 * @since 3.05
 * @module
 */
@SuppressWarnings("serial")
public class ImageFileProperties extends ImageProperties implements PropertyChangeListener {
    /**
     * The preferred size of thumbnail.
     */
    private Dimension preferredThumbnailSize = new Dimension(256, 256);

    /**
     * The panel for image I/O metadata.
     */
    private final IIOMetadataPanel metadata;

    /**
     * The warnings
     */
    private final DefaultListModel warnings;

    /**
     * The index of the warning tab. Used in order to change the enabled
     * or disabled status of that tab.
     */
    private final int warningsTab;

    /**
     * If a worker is currently running, that worker. This is used for
     * cancelling a running action before to start a new one.
     */
    private transient volatile Worker worker;

    /**
     * Creates a new instance of {@code ImageFileProperties} with no image.
     * One of {@link #setImage(File) setImage(...)} methods must be
     * invoked in order to set the properties source.
     */
    public ImageFileProperties() {
        this(new IIOMetadataPanel());
    }

    /**
     * Creates a new instance of {@code ImageFileProperties} initialized to the given file.
     *
     * @param  file The image file.
     * @throws IOException If the file is not found, or no suitable image reader is found for the
     *         given file, or if an error occured while reading the metadata or the thumbnails.
     */
    public ImageFileProperties(final File file) throws IOException {
        this();
        setImage(file);
    }

    /**
     * Creates a new instance using the given metadata panel.
     */
    private ImageFileProperties(final IIOMetadataPanel metadata) {
        super(metadata);
        this.metadata = metadata;
        warningsTab = tabs.getTabCount();
        warnings = new DefaultListModel();
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        tabs.addTab(resources.getString(Vocabulary.Keys.WARNING), new JList(warnings));
        tabs.setEnabledAt(warningsTab, false);
    }

    /**
     * Adds the given lines to the warnings tab.
     */
    final void addWarnings(final List<String> chunks) {
        tabs.setEnabledAt(warningsTab, true);
        for (final String warning : chunks) {
            warnings.addElement(warning);
        }
    }

    /**
     * Clears the panels except the warnings one. We do not clear the warnings panel because
     * it is the result of the reading process we just finished and we want to show them.
     */
    @Override
    final void clear() {
        super.clear();
        metadata.clear();
    }

    /**
     * Sets the image to the given info. At the contrary of public {@code setImage} methods,
     * <strong>this method must be invoked in the Swing thread</strong>.
     *
     * @param streamMetadata The stream metadata, or {@code null} if none.
     * @param images The informations about each image.
     */
    private void setImage(final IIOMetadata streamMetadata, final Info[] images) {
        assert EventQueue.isDispatchThread();
        if (images.length == 0) {
            super.setImage((RenderedImage) null);
            return;
        }
        /*
         * Clear the metadata because we are going to define new values.
         * But do not clear the warnings, because they are the result of
         * the reading process we just finished and we want to show them.
         */
        metadata.clear();
        images[0].show(this);
        /*
         * Add non-null metadata.
         */
        IIOMetadata[] metadata = new IIOMetadata[images.length];
        int count = 0;
        for (final Info info : images) {
            final IIOMetadata m = info.metadata;
            if (m != null) {
                metadata[count++] = m;
            }
        }
        metadata = XArrays.resize(metadata, count);
        this.metadata.addMetadata(streamMetadata, metadata);
        /*
         * Sets the overview to the first image having a thumbnail.
         */
        BufferedImage thumbnail = null;
        for (final Info info : images) {
            thumbnail = info.thumbnail;
            if (thumbnail != null) {
                break;
            }
        }
        viewer.setImage(thumbnail);
    }

    /**
     * Sets the specified {@linkplain ImageReader image reader} as the source of metadata
     * and thumbnails. The information are extracted immediately and no reference to the
     * given image reader is retained. This method does not read the image.
     * <p>
     * This method can be invoked from any thread. Actually it is recommanded to invoke
     * it from an other thread than the Swing one.
     *
     * @param  reader The image reader from which to read the informations.
     * @throws IOException If an error occured while reading the metadata or the thumbnails.
     */
    public void setImage(final ImageReader reader) throws IOException {
        warnings.clear();
        tabs.setEnabledAt(warningsTab, false);
        final IIOReadWarningListener listener = worker;
        if (listener != null) {
            reader.addIIOReadWarningListener(listener);
        }
        try {
            final IIOMetadata metadata = reader.getStreamMetadata();
            final int numImages = reader.getNumImages(false);
            Info[] infos;
            if (numImages >= 0) {
                infos = new Info[numImages];
                for (int i=0; i<numImages; i++) {
                    infos[i] = new Info(reader, i, preferredThumbnailSize);
                }
            } else {
                /*
                 * numImages is -1 if this information is too costly to fetch. This occurs for example
                 * in animated GIF files. In such case the image I/O documentation recommands to fetch
                 * the images sequentially until an IndexOutOfBoundsException is thrown.
                 */
                final ArrayList<Info> list = new ArrayList<Info>();
                while (true) {
                    final Info info;
                    final int index = list.size();
                    try {
                        info = new Info(reader, index, preferredThumbnailSize);
                    } catch (IndexOutOfBoundsException e) {
                        // This is the expected exception, but log anyway.
                        Logging.recoverableException(ImageFileProperties.class, "setImage", e);
                        break;
                    }
                    list.add(info);
                }
                infos = list.toArray(new Info[list.size()]);
            }
            /*
             * At this point we have all informations we want.
             * Now declare those informations to the widget.
             */
            final Info[] images = infos;
            EventQueue.invokeLater(new Runnable() {
                @Override public void run() {
                    setImage(metadata, images);
                }
            });
        } finally {
            if (listener != null) {
                reader.removeIIOReadWarningListener(listener);
            }
        }
    }

    /**
     * Sets the specified {@linkplain File file} as the source of metadata and thumbnails. This
     * method creates a temporary image reader and delegates to {@link #setImage(ImageReader)}.
     * If the content of this pane change often, callers should use their own image reader and
     * recycle it instead.
     * <p>
     * This method can be invoked from any thread. Actually it is recommanded to invoke
     * it from an other thread than the Swing one.
     *
     * @param  file The image file.
     * @throws IOException If the file is not found, or no suitable image reader is found for the
     *         given file, or if an error occured while reading the metadata or the thumbnails.
     */
    public void setImage(final File file) throws IOException {
        setImageInput(file);
    }

    /**
     * Sets the specified image input as the source of metadata and thumbnails. This method creates
     * a temporary image reader and delegates to {@link #setImage(ImageReader)}. If the content of
     * this pane change often, callers should use their own image reader and recycle it instead.
     * <p>
     * This method can be invoked from any thread. Actually it is recommanded to invoke
     * it from an other thread than the Swing one.
     *
     * @param  input The image input.
     * @throws IOException If the file is not found, or no suitable image reader is found for the
     *         given input, or if an error occured while reading the metadata or the thumbnails.
     */
    public void setImageInput(final Object input) throws IOException {
        Formats.selectImageReader(input, getLocale(), new Formats.ReadCall() {
            @Override
            public void read(final ImageReader reader) throws IOException {
                setImage(reader);
            }

            @Override
            public void recoverableException(final Throwable error) {
                Logging.recoverableException(ImageFileProperties.class, "setImageInput", error);
            }
        });
    }

    /**
     * Returns the preferred size of the {@linkplain ImageReader#readThumbnail(int,int) thumbnail}.
     * If a file contains more than one thumbnail, then the one having the closest size to this
     * value will be selected.
     *
     * @return The preferred thumbnail size.
     */
    public Dimension getPreferredThumbnailSize() {
        return (Dimension) preferredThumbnailSize.clone();
    }

    /**
     * Sets the preferred size of the {@linkplain ImageReader#readThumbnail(int,int) thumbnail}.
     *
     * @param size The new preferred thumbnail size.
     */
    public void setPreferredThumbnailSize(final Dimension size) {
        final Dimension old = preferredThumbnailSize;
        if (!size.equals(old)) {
            preferredThumbnailSize = new Dimension(size);
            firePropertyChange("preferredThumbnailSize", old, size);
        }
    }

    /**
     * Invoked when the state of a {@link JFileChooser} (or any other component at caller choice)
     * changed. If the event {@linkplain PropertyChangeEvent#getPropertyName() property name} is
     * {@value javax.swing.JFileChooser#SELECTED_FILE_CHANGED_PROPERTY}, then this method invokes
     * {@link #setImage(File)} in a background thread.
     * <p>
     * This method is invoked automatically when this {@code ImageFileProperties} is registered
     * to a {@code JFileChooser} as an {@link PropertyChangeListener}. It can be invoked from
     * the <cite>Swing</cite> thread only.
     *
     * @param event The property change event.
     */
    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(event.getPropertyName())) {
            final Object input = event.getNewValue();
            if (input instanceof File) {
                final File file = (File) input;
                if (file.isFile()) {
                    Worker w = this.worker;
                    if (w != null) {
                        worker = null;
                        w.cancel(false);
                    }
                    w = new Worker(file);
                    worker = w; // Must be before execute.
                    w.execute();
                }
            }
        }
    }

    /**
     * The worker thread which will fetch image properties in background.
     * In case of failure, the {@link IOException} is ignored.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.05
     *
     * @since 3.05
     * @module
     */
    private final class Worker extends SwingWorker<Object,String> implements IIOReadWarningListener {
        /**
         * The file to read.
         */
        private final File input;

        /**
         * Creates a new worker thread for reading the given image.
         */
        Worker(final File input) {
            this.input = input;
        }

        /**
         * Loads the image in a background thread, then refreshes
         * the {@link ImageFileProperties} in the Swing thread.
         */
        @Override
        protected Object doInBackground() throws IOException {
            setImage(input);
            return null;
        }

        /**
         * Invoked when a warning occured while reading an image.
         */
        @Override
        public void warningOccurred(final ImageReader source, final String warning) {
            publish(warning);
        }

        /**
         * Invoked from the Swing thread for processing the warnings.
         */
        @Override
        protected void process(final List<String> chunks) {
            addWarnings(chunks);
        }

        /**
         * Invoked in the Swing thread when the task is completed for
         * cleaning the {@link ImageFileProperties#worker} reference.
         */
        @Override
        protected void done() {
            if (worker == this) {
                worker = null;
            }
            try {
                get();
            } catch (Exception e) {
                setImage((RenderedImage) null);
                addWarnings(Collections.singletonList(e.getLocalizedMessage()));
            }
        }
    }

    /**
     * Informations about an image. An instance of this structure is created for each
     * image in a stream.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.05
     *
     * @see ImageProperties
     *
     * @since 3.05
     * @module
     */
    private static final class Info {
        /**
         * The provider of the image reader. This information is not really image-specific,
         * but it is cheap to keep a reference here anyway since the same instance will be
         * shared by many {@code Info} structures.
         */
        private final ImageReaderSpi provider;

        /**
         * The image size and tile size.
         */
        private final int width, height, tileWidth, tileHeight;

        /**
         * The color and sample models.
         */
        private final ImageTypeSpecifier type;

        /**
         * The metadata, or {@code null} if none.
         */
        final IIOMetadata metadata;

        /**
         * The thumbnail, or {@code null} if none. If the image has many
         * thumbnails, only the first one is used.
         */
        final BufferedImage thumbnail;

        /**
         * Fetches the informations from the given image reader for the image at the given index.
         */
        @SuppressWarnings("fallthrough")
        Info(final ImageReader reader, final int index, final Dimension preferredThumbnailSize)
                throws IOException
        {
            provider    = reader.getOriginatingProvider();
            width       = reader.getWidth(index);
            height      = reader.getHeight(index);
            tileWidth   = reader.getTileWidth(index);
            tileHeight  = reader.getTileHeight(index);
            type        = reader.getRawImageType(index);
            metadata    = reader.getImageMetadata(index);
            final int n = reader.getNumThumbnails(index);
            int ti = 0;
            switch (n) {
                /*
                 * Search for the best thumbnail. Note that for any (n >= 2) cases, fetching the
                 * thumbnail width and height may be inefficient if the ImageReader in use didn't
                 * overrided the default getThumbnail[Width|Height] implementations. This is the
                 * raison why we skip the search if n=1.
                 */
                default: {
                    long best = Integer.MAX_VALUE;
                    for (int i=0; i<n; i++) {
                        final long dx = reader.getThumbnailWidth (index, i) - preferredThumbnailSize.width;
                        final long dy = reader.getThumbnailHeight(index, i) - preferredThumbnailSize.height;
                        long distance = dx*dx + dy*dy;
                        if (distance < best) {
                            best = distance;
                            ti = i;
                        }
                    }
                    // Fall through
                }
                case 1: {
                    thumbnail = reader.readThumbnail(index, ti);
                    break;
                }
                case 0: {
                    /*
                     * Special case in the absence of thumbnail: if the image is small enough,
                     * read it and use it directly as the thumbnail. If the image is too big,
                     * we don't read it even with subsampling because it may be a too costly
                     * operation.
                     */
                    if (width  / 2 <= preferredThumbnailSize.width &&
                        height / 2 <= preferredThumbnailSize.height)
                    {
                        thumbnail = reader.read(index);
                    } else {
                        thumbnail = null;
                    }
                    break;
                }
            }
        }

        /**
         * Shows the content of this {@code Info} object in the given properties pane.
         */
        final void show(final ImageProperties properties) {
            properties.setDescription(type.getColorModel(), type.getSampleModel(),
                    width, height, tileWidth, tileHeight,
                    (width  + tileWidth -1) / tileWidth,
                    (height + tileHeight-1) / tileHeight);
            properties.setDescription(provider);
        }
    }

    /**
     * Shows the properties for the specified image file in a frame.
     * This convenience method is mostly a helper for debugging purpose.
     *
     * @param image The image to display in a frame.
     */
    public static void show(final File image) {
        JComponent c;
        try {
            c = new ImageFileProperties(image);
        } catch (IOException e) {
            ExceptionMonitor.show(null, e);
            return;
        }
        SwingUtilities.show(c, image.getName());
    }
}
