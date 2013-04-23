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

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import javax.swing.JDialog;
import javax.swing.JSplitPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;


/**
 * A file chooser for images. Compared to the standard {@code JFileChooser}, this class provides
 * the following additional functionalities:
 * <p>
 * <ul>
 *   <li>The list of {@linkplain FileFilter file filters} is determined automatically from
 *       the image formats known to {@link IIORegistry}.</li>
 *   <li>An optional pseudo-format called "<cite>List of files</cite>"
 *       {@linkplain #isListFileFilterUsed() can be added}. This pseudo-format allows the
 *       user to specify a text file listing the paths to many image files.</li>
 *   <li>The {@code showDialog(...)} methods display an {@link ImageFileProperties} pane
 *       at the right of the {@code ImageFileChooser}.</li>
 * </ul>
 * <p>
 * This class should typically be used as below (replace "{@code showOpenDialog}" by
 * "{@code showSaveDialog"} for saving an image instead than loading it):
 *
 * {@preformat java
 *     ImageFileChooser chooser = new ImageFileChooser("png", true);
 *     if (chooser.showOpenDialog(parent) == ImageFileChooser.APPROVE_OPTION) {
 *         File selected = chooser.getSelectedFile();
 *     }
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @see ImageFileProperties
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
public class ImageFileChooser extends JFileChooser {
    /**
     * The default format, or {@code null} if none.
     */
    private String defaultFormat;

    /**
     * Non-null if the list of providers should include a special entry
     * for loading a text file which contains a list of files.
     * <p>
     * We can not serialize this field, because {@link FileNameExtensionFilter}
     * is not serializable.
     */
    private transient FileFilter listFileFilter;

    /**
     * {@code true} if the list of providers should include a special entry
     * for loading a text file which contains a list of files.
     */
    private boolean listFileFilterUsed;

    /**
     * Selected files, cached for avoiding to parse the same "list of files" twice.
     */
    private transient File[] selectedFiles;

    /**
     * The providers for each file format listed in the file filter.
     */
    private final Map<FileFilter,ImageReaderWriterSpi> providers;

    /**
     * The panel showing the properties of the selected file, or {@code null} if none.
     *
     * @since 3.05
     */
    private ImageFileProperties propertiesPane;

    /**
     * Creates a new file chooser having the user's default directory as the initial directory.
     * This default depends on the operating system. It is typically the "<cite>My Documents</cite>"
     * folder on Windows, and the user's home directory on Unix.
     * <p>
     * The {@link #setDialogType(int)} method will be invoked implicitly by the
     * {@link #showOpenDialog showOpenDialog} and {@link #showSaveDialog showSaveDialog} methods.
     * If those methods are not going to be invoked, then callers should invoke
     * {@code setDialogType(int)} explicitly after construction in order to
     * {@linkplain #addChoosableFileFilter add file filters} appropriate for
     * the kind of operation (<cite>open</cite> or <cite>save</cite>) to be performed.
     *
     * @param defaultFormat The default format to be initially selected, or {@code null}
     *        for proposing all formats. If non-null, it should be an Image I/O format name
     *        like {@code "png"} or {@code "jpeg"}.
     */
    public ImageFileChooser(final String defaultFormat) {
        this(defaultFormat, false);
    }

    /**
     * Creates a new file chooser, optionally with an {@link ImageFileProperties} pane to be shown.
     * If {@code showProperties} is {@code true}, then the properties pane will be visible when the
     * {@link #showDialog showDialog} method or one of its variants is invoked. The properties pane
     * is not visible otherwise; see {@link #setPropertiesPane(ImageFileProperties)} for more
     * information.
     *
     * @param defaultFormat The default format to be initially selected, or {@code null} for all.
     * @param showProperties {@code true} for creating an {@link ImageFileProperties}. The default
     *        value is {@code false}.
     *
     * @since 3.05
     */
    public ImageFileChooser(final String defaultFormat, final boolean showProperties) {
        super();
        this.defaultFormat = defaultFormat;
        providers = new HashMap<FileFilter,ImageReaderWriterSpi>();
        addPropertyChangeListener(SELECTED_FILES_CHANGED_PROPERTY, new PropertyChangeListener() {
            @Override public void propertyChange(PropertyChangeEvent event) {
                selectedFiles = null;
            }
        });
        if (showProperties) {
            setPropertiesPane(new ImageFileProperties());
        }
    }

    /**
     * Returns {@code true} if this {@code ImageFileChooser} should proposes a filter
     * for list of files. A "list of files" is a file with {@code .txt}, {@code .lst}
     * or {@code .csv} extension which contains the actual list of images to select,
     * which may be spread over many directories.
     * <p>
     * By default this method returns {@code false}.
     *
     * @return {@code true} if this chooser should proposes the selection of {@code .txt},
     *         {@code .lst} or {@code .csv} files that contain a list of image files.
     *
     * @see #isAcceptAllFileFilterUsed
     */
    public boolean isListFileFilterUsed() {
        return listFileFilterUsed;
    }

    /**
     * Sets whatever this {@code ImageFileChooser} should proposes a filter for list of files.
     *
     * @param enabled {@code true} if this chooser should proposes the selection of {@code .txt},
     *        {@code .lst} or {@code .csv} files that contain a list of image files.
     *
     * @see #setAcceptAllFileFilterUsed
     */
    public void setListFileFilterUsed(final boolean enabled) {
        final boolean old = listFileFilterUsed;
        listFileFilterUsed = enabled;
        firePropertyChange("listFileFilterUsed", old, enabled);
    }

    /**
     * Resets the choosable {@linkplain FileFilter file filter} list to its starting state.
     */
    @Override
    public void resetChoosableFileFilters() {
        super.resetChoosableFileFilters();
        providers.clear();
    }

    /**
     * Returns the pane showing the properties of the selected file, or {@code null} if none.
     * This is different than the {@linkplain #getAccessory() accessory} pane provided by
     * {@code JFileChooser} in that this pane is located at the right side of the chooser,
     * because it is too big for fitting in the accessory area of the file chooser.
     *
     * @return The current pane showing image properties, or {@code null} if none.
     *
     * @see #getAccessory()
     *
     * @since 3.05
     */
    public ImageFileProperties getPropertiesPane() {
        return propertiesPane;
    }

    /**
     * Sets the pane showing the properties of the selected file, which can be {@code null}
     * for hiding the pane. This method automatically {@link #removePropertyChangeListener
     * unregister} the old pane (if any) from the list of property change listeners, and
     * {@linkplain #addPropertyChangeListener register} the new pane (if non null) instead.
     * <p>
     * The properties pane is shown when one of the {@link #showDialog showDialog} method
     * variants is invoked. If those methods are not going to be used for showing this file
     * chooser, then the caller shall adds the {@code ImageFileProperties} pane himself in
     * his own pane.
     *
     * @param properties The new pane showing image properties, or {@code null} if none.
     *
     * @see #setAccessory(JComponent)
     *
     * @since 3.05
     */
    public void setPropertiesPane(final ImageFileProperties properties) {
        final ImageFileProperties old = propertiesPane;
        if (old != null) {
            removePropertyChangeListener(SELECTED_FILE_CHANGED_PROPERTY, old);
        }
        if (properties != null) {
            addPropertyChangeListener(SELECTED_FILE_CHANGED_PROPERTY, properties);
        }
        propertiesPane = properties;
        firePropertyChange("propertiesPane", old, properties);
    }

    /**
     * Sets whatever this dialog is going to be used for reading or writing images. This method
     * resets the {@linkplain FileFilter file filters} to all image formats registered in
     * {@link IIORegistry}. Only formats available for reading or writing (depending on the value
     * of the {@code mode} argument) will be listed.
     *
     * @param mode {@link #OPEN_DIALOG OPEN_DIALOG} for a chooser to be used for reading images, or
     *             {@link #SAVE_DIALOG SAVE_DIALOG} for a chooser to be used for writing images.
     */
    @Override
    public void setDialogType(final int mode) {
        super.setDialogType(mode);
        final Locale locale = getLocale();
        final Class<? extends ImageReaderWriterSpi> category;
        switch (mode) {
            case OPEN_DIALOG: category = ImageReaderSpi.class; break;
            case SAVE_DIALOG: category = ImageWriterSpi.class; break;
            case CUSTOM_DIALOG: resetChoosableFileFilters(); return;
            default: throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, "mode", mode));
        }
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final Iterator<? extends ImageReaderWriterSpi> it = registry.getServiceProviders(category, true);
        final List<FileFilter> filters = new ArrayList<FileFilter>();
        final Map<String,String> suffixDone = new HashMap<String,String>();
        final Set<String> formatsDone = new HashSet<String>();
        final StringBuilder buffer = new StringBuilder();
        resetChoosableFileFilters();
        FileFilter preferred = null;
skip:   while (it.hasNext()) {
            boolean isPreferred = false;
            final ImageReaderWriterSpi spi = it.next();
            String longFormat = null;
            for (final String format : spi.getFormatNames()) {
                if (!formatsDone.add(format)) {
                    // Avoid declaring the same format twice (e.g. declaring
                    // both the JSE and JAI ImageReaders for the PNG format).
                    continue skip;
                }
                if (defaultFormat != null && defaultFormat.equalsIgnoreCase(format)) {
                    isPreferred = true;
                }
                // Remember the longest format string. If two of them
                // have the same length, favor the one in upper case.
                longFormat = ImageFormatEntry.longest(longFormat, format);
            }
            if (longFormat == null) {
                longFormat = spi.getDescription(locale);
            }
            /*
             * At this point, we have a provider to take in account. We need to get the list of
             * suffixes, but we don't need both the lower-case and upper-case flavors of the same
             * suffix. If those two flavors exist, then we will keep only the first one (which is
             * usually the lower-case flavor). The iteration is performed in reverse order for that
             * reason.
             */
            String[] suffix = spi.getFileSuffixes();
            for (int i=suffix.length; --i >= 0;) {
                final String s = suffix[i].trim();
                if (!s.isEmpty()) {
                    suffixDone.put(s.toLowerCase(locale), s);
                }
            }
            if (!suffixDone.isEmpty()) {
                suffix = suffixDone.values().toArray(new String[suffixDone.size()]);
                suffixDone.clear();
                buffer.setLength(0);
                buffer.append(longFormat);
                String separator = "  (";
                for (final String s : suffix) {
                    buffer.append(separator).append("*.").append(s);
                    separator = ", ";
                }
                buffer.append(')');
                final FileFilter filter = new FileNameExtensionFilter(buffer.toString(), suffix);
                filters.add(filter);
                providers.put(filter, spi);
                if (isPreferred) {
                    preferred = filter;
                }
            }
        }
        /*
         * Sorts the filters in alphabetical order before to add them to JFileChooser.
         */
        Collections.sort(filters, new Comparator<FileFilter>() {
            @Override public int compare(final FileFilter f1, final FileFilter f2) {
                return f1.getDescription().compareTo(f2.getDescription());
            }
        });
        /*
         * Adds the file filter for "file containing list of files" if the user allowed it.
         */
        if (isListFileFilterUsed()) {
            final Vocabulary resources = Vocabulary.getResources(getLocale());
            listFileFilter = new FileNameExtensionFilter(resources.getString(
                    Vocabulary.Keys.IMAGE_LIST), "txt", "lst", "csv");
            filters.add(listFileFilter);
        }
        for (final FileFilter filter : filters) {
            addChoosableFileFilter(filter);
        }
        setFileFilter(preferred); // Null is okay.
    }

    /**
     * Returns the selected file. If the user has selected a file which contains a list of images
     * (as proposed if {@code setListFileFilterUsed(true)} has been invoked), then this method
     * returns the list file itself, not its content since this method can only returns a single
     * file.
     */
    @Override
    public File getSelectedFile() {
        return super.getSelectedFile();
    }

    /**
     * Returns the selected file. If the user has selected a file which contains a list of images
     * (as proposed if {@code setListFileFilterUsed(true)} has been invoked), then this method
     * opens that file using the platform encoding and returns its content. If an I/O error occurred
     * while reading that file, then its content is not included in the returned array.
     *
     * @return The list of selected files, including the content of text files that are
     *         list of images.
     */
    @Override
    public File[] getSelectedFiles() {
        final FileFilter filter = listFileFilter;
        if (filter == null || !filter.equals(getFileFilter())) {
            File[] files = super.getSelectedFiles();
            if (files == null || files.length == 0) {
                final File file = getSelectedFile();
                if (file != null) {
                    files = new File[] {file};
                }
            }
            return files;
        }
        /*
         * At this point, the selected files (usually only 1) are actually text files
         * which contain a list of images. We need to parse the content of those files.
         */
        if (selectedFiles == null) {
            final File directory = getCurrentDirectory();
            final List<File> content = new ArrayList<File>();
            for (final File list : super.getSelectedFiles()) {
                if (!filter.accept(list)) {
                    content.add(list);
                } else try {
                    final BufferedReader in = new BufferedReader(new FileReader(list));
                    String line; while ((line = in.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty() && line.charAt(0) != '#') {
                            File file = new File(line);
                            if (!file.isAbsolute()) {
                                file = new File(directory, line);
                            }
                            content.add(file);
                        }
                    }
                    in.close();
                } catch (IOException e) {
                    Logging.unexpectedException(ImageFileChooser.class, "getSelectedFiles", e);
                }
            }
            selectedFiles = content.toArray(new File[content.size()]);
        }
        return selectedFiles.clone();
    }

    /**
     * Returns the image reader/writer provider for the {@linkplain #getFileFilter() currently
     * selected file filter}. If the {@linkplain #setDialogType(int) dialog type} has been set
     * to {@link #OPEN_DIALOG OPEN_DIALOG}, then this method returns either {@code null} or an
     * instance of {@link ImageReaderSpi}. Otherwise if the dialog type has been set to {@link
     * #SAVE_DIALOG SAVE_DIALOG}, then this method returns either {@code null} or an instance
     * of {@link ImageWriterSpi}.
     *
     * @return The image reader/writer provider for the currently selected file filter,
     *         or {@code null} if the current file filter is unknown to this method.
     */
    public ImageReaderWriterSpi getCurrentProvider() {
        ImageReaderWriterSpi provider = providers.get(getFileFilter());
        if (provider == null) {
            /*
             * Before to gives up, checks if all currently selected files could be read using
             * the same provider. This check is necessary since the selected files may actually
             * come from a selected text file which contains a list of images.
             */
            String[] suffixes = null;
verify:     for (final File file : getSelectedFiles()) {
                String ext = file.getName();
                int s = ext.lastIndexOf('.');
                if (s <= 0) {
                    // No extension - conservatively said that we don't know the provider.
                    return null;
                }
                ext = ext.substring(s+1);
                if (suffixes == null) {
                    for (final ImageReaderWriterSpi candidate : providers.values()) {
                        suffixes = candidate.getFileSuffixes();
                        if (ArraysExt.containsIgnoreCase(suffixes, ext)) {
                            provider = candidate;
                            continue verify;
                        }
                    }
                    return null;
                }
                if (!ArraysExt.containsIgnoreCase(suffixes, ext)) {
                    // Found image that would require a different provider.
                    return null;
                }
            }
        }
        return provider;
    }

    /**
     * Creates and returns a new dialog wrapping this {@code ImageFileChooser}, optionally
     * with its {@link ImageFileProperties} pane. This method is invoked automatically by
     * the {@link #showDialog showDialog} methods and is overridden for adding the optional
     * properties pane, if presents.
     *
     * @param  parent he parent component of the dialog, or {@code null}.
     * @return A dialog containing this file chooser, and optionally a properties pane.
     * @throws HeadlessException If the graphics environment is headlesss.
     *
     * @since 3.05
     */
    @Override
    protected JDialog createDialog(final Component parent) throws HeadlessException {
        final JDialog dialog = super.createDialog(parent);
        if (propertiesPane != null) {
            synchronized (dialog.getTreeLock()) {
                final Container contentPane = dialog.getContentPane();
                Component chooser = contentPane;
                if (contentPane.getComponentCount() == 1) {
                    chooser = contentPane.getComponent(0);
                    contentPane.remove(0);
                }
                dialog.setContentPane(new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT, true, chooser, propertiesPane));
            }
            /*
             * Add the file chooser and the properties panel with constraints choosed in
             * such a way that if the dialog box is resized, only the properties panel
             * will be resized accordingly.
             */
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
        }
        return dialog;
    }
}
