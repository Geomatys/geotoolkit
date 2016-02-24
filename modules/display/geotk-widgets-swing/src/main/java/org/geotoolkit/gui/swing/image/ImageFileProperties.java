/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.event.IIOReadProgressListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.EventQueue;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.DefaultListModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.geotoolkit.nio.IOUtilities;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.image.io.NamedImageStore;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SampleDimension;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.internal.swing.ExceptionMonitor;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.lang.Debug;


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
 * operations. It is recommended to invoke them from an other thread than the Swing thread.
 *
 * {@section Using this component together with a FileChooser}
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
 * @version 3.14
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
     * The warnings.
     */
    private final DefaultListModel<Object> warnings;

    /**
     * The index of the warning tab. Used in order to change the enabled
     * or disabled status of that tab.
     */
    private final int warningsTab;

    /**
     * If a worker is currently running, that worker. This is used by {@link #propertyChange}
     * (invoked when a new file is selected in the file chooser) for canceling a running action
     * before to start a new one. This is also used as progress and warning listeners to be
     * registered to the image reader.
     * <p>
     * This field shall be read and set in the Swing thread only.
     */
    private transient Worker worker;

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
     *         given file, or if an error occurred while reading the metadata or the thumbnails.
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
        warnings = new DefaultListModel<>();
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        final JComponent list = new JScrollPane(new JList<>(warnings));
        list.setOpaque(false);
        tabs.addTab(resources.getString(Vocabulary.Keys.Warning), list);
        tabs.setEnabledAt(warningsTab, false);
    }

    /**
     * Processes the messages sent by the background process. This method shall be invoked
     * in the Swing thread only. The method behavior depends on the object type:
     * <p>
     * <ul>
     *   <li>{@link Boolean#TRUE} means that the reading process started.</li>
     *   <li>{@link Boolean#FALSE} means that the reading process finished or has been canceled.</li>
     *   <li>{@link File} gives the filename to write in the label above the progress.</li>
     *   <li>{@link Integer} are progress as a percentage between 0 and 100.</li>
     *   <li>{@link Strings} are warnings.</li>
     * </ul>
     */
    final void processBackgroundMessages(final Worker caller, final List<?> chunks) {
        if (caller != worker) {
            return;
        }
        final ImagePane viewer = this.viewer;
        boolean hasWarnings = false;
        for (final Object chunk : chunks) {
            if (chunk instanceof Worker) {
                String label = IOUtilities.filename(((Worker) chunk).input);
                label = Vocabulary.getResources(getLocale()).getString(Vocabulary.Keys.Loading_1, label);
                viewer.setProgressLabel(label);
                viewer.setProgress(0);
            } else if (chunk instanceof Boolean) {
                viewer.setProgressVisible((Boolean) chunk);
            } else if (chunk instanceof Integer) {
                viewer.setProgress((Integer) chunk);
            } else {
                warnings.addElement(chunk);
                if (!hasWarnings) {
                    tabs.setEnabledAt(warningsTab, true);
                    hasWarnings = true;
                }
            }
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
     * Returns {@code true} if the running task has been cancelled.
     *
     * @param worker The worker which was set when the task has begun.
     */
    static boolean isCancelled(final Worker worker) {
        return worker != null && worker.isCancelled();
    }

    /**
     * Sets the specified {@linkplain ImageReader image reader} as the source of metadata
     * and thumbnails, reading the information at the given image index. The information
     * are extracted immediately and no reference to the given image reader is retained.
     *
     * {@section Resources management}
     * This method does <strong>not</strong> close the
     * {@linkplain javax.imageio.stream.ImageInputStream Image Input Stream} (if any).
     * It is caller responsibility to dispose reader resources (including closing the
     * input stream) after this method call, if desired.
     *
     * {@section Thread safety}
     * This method can be invoked from any thread. Actually it is recommended to invoke
     * it from an other thread than the <cite>Swing</cite> one.
     *
     * @param  reader The image reader from which to read the informations.
     * @param  imageIndex The index of the image to read (usually 0).
     * @throws IOException If an error occurred while reading the metadata or the thumbnails.
     *
     * @since 3.07
     */
    public void setImage(final ImageReader reader, final int imageIndex) throws IOException {
        final AtomicReference<Worker> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                ref.set(worker);
                warnings.clear();
                tabs.setEnabledAt(warningsTab, false);
            }
        });
        final Worker worker = ref.get();
        /*
         * Read only the metadata, and refresh the information panel as soon as those metadata
         * are available. Note that the list of metadata may contains null elements, which are
         * necessary in order to have metadata for image 'i' stored in the list at index 'i'.
         */
        final List<String> imageNames = (reader instanceof NamedImageStore) ?
                ((NamedImageStore) reader).getImageNames() : null;
        final IIOMetadata streamMetadata = reader.getStreamMetadata();
        final List<IIOMetadata> imageMetadata = new ArrayList<>();
        for (int i=0; i<imageIndex; i++) {
            if (isCancelled(worker)) return;
            imageMetadata.add(reader.getImageMetadata(i));
        }
        if (isCancelled(worker)) return;
        final Info info = new Info(reader, imageIndex);
        imageMetadata.add(info.metadata);
        /*
         * Update the Swing widget in the Swing thread. Note that we need to take a snapshot
         * of the list content - do not pass the list directly, because it is not thread safe.
         */
        final IIOMetadata[] snapshot = imageMetadata.toArray(new IIOMetadata[imageMetadata.size()]);
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                if (!isCancelled(worker)) {
                    info.show(ImageFileProperties.this);
                    metadata.imageNames = imageNames;
                    try {
                        metadata.setMetadata(streamMetadata, snapshot);
                    } finally {
                        metadata.imageNames = null;
                    }
                }
            }
        });
        /*
         * Read the thumbnail or the image now.
         */
        if (isCancelled(worker)) return;
        final BufferedImage thumbnail = info.readThumbnail(reader, imageIndex, preferredThumbnailSize);
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                if (!isCancelled(worker)) {
                    viewer.setImage(thumbnail);
                }
            }
        });
        /*
         * Read metadata for the remaining images, if any. If the number of image is unknown,
         * we will read every image until we get an IndexOutOfBoundsException. Otherwise such
         * exception will be considered as an error.
         */
        int index = imageIndex;
        boolean hasMore = false;
        final int numImages = reader.getNumImages(false);
        while (++index < numImages || numImages < 0) {
            if (isCancelled(worker)) return;
            final IIOMetadata md;
            try {
                md = reader.getImageMetadata(index);
            } catch (IndexOutOfBoundsException e) {
                if (numImages >= 0) {
                    // This exception is unexpected if the number of images was known.
                    throw new IIOException(e.getLocalizedMessage(), e);
                }
                break;
            }
            imageMetadata.add(md);
            hasMore |=  (md != null);
        }
        /*
         * Set metadata for the remaining images, if any. We need to set the previous
         * content of the list to 'null' in order to avoid adding the same metadata twice.
         */
        if (hasMore) {
            for (int i=0; i<=imageIndex; i++) {
                imageMetadata.set(i, null);
            }
            final IIOMetadata[] md = imageMetadata.toArray(new IIOMetadata[imageMetadata.size()]);
            EventQueue.invokeLater(new Runnable() {
                @Override public void run() {
                    if (!isCancelled(worker)) {
                        metadata.imageNames = imageNames;
                        try {
                            metadata.addMetadata(null, md);
                        } finally {
                            metadata.imageNames = null;
                        }
                    }
                }
            });
        }
    }

    /**
     * Sets the specified {@linkplain ImageReader image reader} as the source of metadata
     * and thumbnails. The information are extracted immediately and no reference to the
     * given image reader is retained.
     * <p>
     * The default implementation delegates to {@link #setImage(ImageReader, int)} with
     * an <cite>image index</cite> of 0. Subclasses should override this method if they
     * want to choose automatically an image index depending on the format and the input.
     *
     * {@section Thread safety}
     * This method can be invoked from any thread. Actually it is recommended to invoke
     * it from an other thread than the <cite>Swing</cite> one.
     *
     * @param  reader The image reader from which to read the informations.
     * @throws IOException If an error occurred while reading the metadata or the thumbnails.
     */
    public void setImage(final ImageReader reader) throws IOException {
        setImage(reader, 0);
    }

    /**
     * Sets the specified {@linkplain File file} as the source of metadata and thumbnails.
     * This is the method which is invoked in a background thread when a {@link JFileChooser}
     * is associated to this widget as described in the <a href="#overview">class javadoc</a>.
     * The default implementation delegates to {@link #setImageInput(Object)}, but subclasses
     * can override if they want to perform additional processing.
     *
     * {@section Thread safety}
     * This method can be invoked from any thread. Actually it is recommended to invoke
     * it from an other thread than the <cite>Swing</cite> one.
     *
     * @param  file The image file.
     * @throws IOException If the file is not found, or no suitable image reader is found for the
     *         given file, or if an error occurred while reading the metadata or the thumbnails.
     */
    public void setImage(final File file) throws IOException {
        setImageInput(file);
    }

    /**
     * Sets the specified image input as the source of metadata and thumbnails. This method gets
     * or creates an {@link ImageReader} for the given input as below:
     * <p>
     * <u>
     *   <li>If the given input is an instance of {@link ImageReader}, then it is used
     *       directly. Note that this method does <strong>not</strong> close the image
     *       input stream after usage, as specified by {@link #setImage(ImageReader, int)}.</li>
     *   <li>Otherwise this method creates a temporary image reader for the given input,
     *       passes it to {@link #setImage(ImageReader)}, then disposes the resources
     *       (reader and input stream).</li>
     * </u>
     * <p>
     * Then, this method delegates to {@link #setImage(ImageReader)}.
     *
     * {@section Thread safety}
     * This method can be invoked from any thread. Actually it is recommended to invoke
     * it from an other thread than the <cite>Swing</cite> one.
     *
     * @param  input The image input.
     * @throws IOException If the file is not found, or no suitable image reader is found for the
     *         given input, or if an error occurred while reading the metadata or the thumbnails.
     */
    public void setImageInput(final Object input) throws IOException {
        final ReadInvoker invoker = new ReadInvoker();
        SwingUtilities.invokeAndWait(invoker);
        if (input instanceof ImageReader) {
            invoker.read((ImageReader) input);
        } else {
            Formats.selectImageReader(input, invoker.locale, invoker);
        }
    }

    /**
     * Reads the metadata and thumbnails from the given image input in a background thread.
     * This method delegates to the {@link #setImage(File)} method if the given input is a
     * file, or to {@link #setImageInput(Object)} otherwise, from a background thread. If
     * the operation fails, then the {@link IOException} is painted in the quicklook area.
     * <p>
     * <b>NOTE:</b> This method must be invoked from the <cite>Swing</cite> thread.
     *
     * @param input The image input, or {@code null} if none.
     *
     * @since 3.12
     */
    public void setImageLater(final Object input) {
        Worker w = worker;
        if (w != null) {
            worker = null;
            w.cancel();
        }
        if (input == null) {
            setImage((RenderedImage) null);
        } else {
            w = new Worker(input);
            worker = w; // Must be before execute.
            w.execute();
        }
    }

    /**
     * The task to be run by {@link ImageFileProperties#setImageInput(Object) in the caller
     * (preferably a background) thread. The {@link Runnable} part is to be run in the Swing
     * thread. The {@link Formats.ReadCall} part is to be run in the caller thread.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.14
     *
     * @since 3.05
     * @module
     */
    private final class ReadInvoker implements Runnable, Formats.ReadCall {
        /**
         * A copy of the {@link ImageFileProperties#worker} field,
         * used in order to register listeners to the image reader.
         */
        private Worker listener;

        /**
         * The locale of the enclosing widget.
         */
        Locale locale;

        /**
         * Executed from the Swing thread in order to initialize {@link #listener} to the
         * value of {@link ImageFileProperties#worker}. Note that this is run from a thread
         * different than everything else declared in the {@code setImageInput} method body.
         */
        @Override
        public void run() {
            listener = worker;
            locale = getLocale();
        }

        /**
         * Reads the image. This is run from the caller (preferably a background) thread.
         * Note that if this method is called by {@link Formats#selectImageReader}, then
         * it will be disposed automatically after the call. Otherwise (if this method is
         * invoked directly), it is caller responsibility to dispose the reader.
         */
        @Override
        public void read(final ImageReader reader) throws IOException {
            final Worker listener = this.listener;
            if (listener != null) {
                reader.addIIOReadWarningListener (listener);
                reader.addIIOReadProgressListener(listener);
            }
            try {
                setImage(reader);
            } finally {
                if (listener != null) {
                    reader.removeIIOReadProgressListener(listener);
                    reader.removeIIOReadWarningListener (listener);
                }
            }
        }

        /**
         * Invoked when a recoverable error (not during image read) occurred.
         */
        @Override
        public void recoverableException(final Throwable error) {
            Logging.recoverableException(null, ImageFileProperties.class, "setImageInput", error);
        }
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
                    setImageLater(file);
                }
            }
        }
    }

    /**
     * The worker thread which will fetch image properties in background.
     * In the operation fails, the {@link IOException} is painted in the
     * quicklook area.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.05
     *
     * @since 3.05
     * @module
     */
    private final class Worker extends SwingWorker<Object,Object>
            implements IIOReadProgressListener, IIOReadWarningListener
    {
        /**
         * The file to read.
         */
        final Object input;

        /**
         * The image reader. Used only for cancelation.
         */
        private volatile ImageReader reader;

        /**
         * Creates a new worker thread for reading the given image.
         */
        Worker(final Object input) {
            this.input = input;
        }

        /**
         * Loads the image in a background thread, then refreshes
         * the {@link ImageFileProperties} in the Swing thread.
         */
        @Override
        protected Object doInBackground() throws IOException {
            publish(this); // A marker value for showing the input name in the widget.
            if (input instanceof File) {
                setImage((File) input);
            } else {
                setImageInput(input);
            }
            return null;
        }

        @Override public void sequenceStarted  (ImageReader r, int image)      {}
        @Override public void imageStarted     (ImageReader r, int image)      {publish(Boolean.TRUE); reader=r;}
        @Override public void thumbnailStarted (ImageReader r, int i, int t)   {publish(Boolean.TRUE); reader=r;}
        @Override public void imageProgress    (ImageReader r, float percent)  {publish(Math.round(percent));}
        @Override public void thumbnailProgress(ImageReader r, float percent)  {publish(Math.round(percent));}
        @Override public void warningOccurred  (ImageReader r, String warning) {publish(warning);}
        @Override public void readAborted      (ImageReader r)                 {publish(Boolean.FALSE);}
        @Override public void thumbnailComplete(ImageReader r)                 {publish(Boolean.FALSE);}
        @Override public void imageComplete    (ImageReader r)                 {publish(Boolean.FALSE);}
        @Override public void sequenceComplete (ImageReader r)                 {}

        /**
         * Invoked from the Swing thread for processing the progress or the warnings.
         */
        @Override
        protected void process(final List<Object> chunks) {
            processBackgroundMessages(this, chunks);
        }

        /**
         * Cancels the current reading operation.
         * This method can be invoked from any thread.
         */
        final void cancel() {
            final ImageReader reader = this.reader;
            if (reader != null) {
                reader.abort();
            }
            cancel(true);
        }

        /**
         * Invoked in the Swing thread when the task is completed for
         * cleaning the {@link ImageFileProperties#worker} reference.
         */
        @Override
        protected void done() {
            if (worker == this) try {
                get();
            } catch (final InterruptedException | ExecutionException e) {
                Throwable cause = e;
                if (e instanceof ExecutionException) {
                    cause = e.getCause();
                    if (cause == null) {
                        cause = e;
                    }
                }
                setImage((RenderedImage) null);
                processBackgroundMessages(this, Collections.singletonList(e.getLocalizedMessage()));
                viewer.setError(cause);
            } finally {
                worker = null; // Must be last.
            }
        }
    }

    /**
     * Informations about an image. An instance of this structure is created for each
     * image in a stream.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.09
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
         * The image metadata, or {@code null} if none.
         */
        final IIOMetadata metadata;

        /**
         * The coordinate reference system, or {@code null} if none.
         */
        private CoordinateReferenceSystem crs;

        /**
         * The cell size as a string, or {@code null} if none.
         */
        private String cellSize;

        /**
         * The range of valid geophysics values, or {@code null} if none.
         */
        private NumberRange<?> valueRange;

        /**
         * Fetches the informations from the given image reader for the image at the given index.
         * The thumbnail is not read yet; an explicit call to {@code readThumbnail} will be needed.
         */
        Info(final ImageReader reader, final int index) throws IOException {
            provider   = reader.getOriginatingProvider();
            width      = reader.getWidth(index);
            height     = reader.getHeight(index);
            tileWidth  = reader.getTileWidth(index);
            tileHeight = reader.getTileHeight(index);
            type       = reader.getRawImageType(index);
            metadata   = reader.getImageMetadata(index);
            if (metadata instanceof SpatialMetadata) {
                final SpatialMetadata sm = (SpatialMetadata) metadata;
                sm.setReadOnly(true);
                crs = sm.getInstanceForType(CoordinateReferenceSystem.class);
                final RectifiedGrid rg = sm.getInstanceForType(RectifiedGrid.class);
                final MetadataHelper helper = new MetadataHelper(sm);
                if (rg != null) {
                    cellSize = helper.formatCellDimension(rg,
                            (crs != null) ? crs.getCoordinateSystem() : null);
                }
                final SampleDimension sd = sm.getInstanceForType(SampleDimension.class);
                if (sd != null) {
                    valueRange = helper.getValidValues(sd);
                }
            }
        }

        /**
         * Reads the thumbnail, or the full image if there is no thumbnail. The reader and index
         * given to this method shall be the same than the ones given to the constructor.
         */
        @SuppressWarnings("fallthrough")
        final BufferedImage readThumbnail(final ImageReader reader, final int index,
                final Dimension preferredThumbnailSize) throws IOException
        {
            BufferedImage thumbnail;
            final int n = reader.getNumThumbnails(index);
            int ti = 0;
            switch (n) {
                /*
                 * Search for the best thumbnail. Note that for any (n >= 2) cases, fetching the
                 * thumbnail width and height may be inefficient if the ImageReader in use didn't
                 * overridden the default getThumbnail[Width|Height] implementations. This is the
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
                     * No thumbnail: read the image with a subsampling. Note that it may be a slow
                     * operation, but we are running this constructor in a background thread anyway.
                     */
                    final int xSubsampling = Math.max(1, width  / preferredThumbnailSize.width);
                    final int ySubsampling = Math.max(1, height / preferredThumbnailSize.height);
                    final ImageReadParam param = reader.getDefaultReadParam();
                    param.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);
                    thumbnail = reader.read(index, param);
                    break;
                }
            }
            return thumbnail;
        }

        /**
         * Shows the content of this {@code Info} object in the given properties pane.
         */
        final void show(final ImageFileProperties properties) {
            properties.setOperationDescription(provider);
            properties.setImageDescription(
                    (type != null) ? type.getColorModel()  : null,
                    (type != null) ? type.getSampleModel() : null,
                    width, height, tileWidth, tileHeight,
                    (width  + tileWidth -1) / tileWidth,
                    (height + tileHeight-1) / tileHeight);

            properties.setGeospatialDescription(crs, cellSize, valueRange);
            if (!(metadata instanceof SpatialMetadata)) {
                properties.setGeospatialDescription(false);
            }
        }
    }

    /**
     * Shows the properties for the specified image file in a frame.
     * This convenience method is mostly a helper for debugging purpose.
     *
     * @param image The image to display in a frame.
     */
    @Debug
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
