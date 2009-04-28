/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
import java.util.prefs.Preferences;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.spi.ImageReaderSpi;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXBusyLabel;

import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.internal.SwingUtilities;
import org.geotoolkit.gui.swing.ProgressWindow;
import org.geotoolkit.gui.swing.ExceptionMonitor;


/**
 * A chooser for a set of {@linkplain Tile tiles} to be used for creating a mosaic. This chooser
 * allows users to select tiles from a file or a directory and see the silhouette of selected tiles.
 * The result can be obtained by a call to {@link #getSelectedTiles()}.
 * <p>
 * The tiles are images in any format supported by Java Image I/O (TIFF, PNG, <cite>etc.</cite>),
 * accompagned by their <cite>world files</cite>. World Files must have the same name than the
 * images except for the extension ({@code ".tfw"}, {@code ".jpw"}, <cite>etc.</cite> depending
 * on the image format).
 * <p>
 * Users can define the mosaic either directly by choosing an arbitrary amount of image files,
 * or indirectly by selecting a single text file having {@code .txt}, {@code .lst} or {@code .csv}
 * extension. The text file shall contain a list of image files to use for the mosaic.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@SuppressWarnings("serial")
public class MosaicChooser extends JPanel implements Dialog {
    /**
     * The preference name for the format of tiles to be read.
     */
    private static final String INPUT_FORMAT = "InputTilesFormat";

    /**
     * The preference name for the directory of tiles to be read.
     */
    private static final String INPUT_DIRECTORY = "InputTilesDirectory";

    /**
     * The preference name for the directory of tiles to be writen. This is not used by this
     * class ({@link MosaicBuilderEditor} uses it), but is defined here for keeping it close
     * to {@link #INPUT_DIRECTORY}.
     */
    static final String OUTPUT_DIRECTORY = "OutputTilesDirectory";

    /**
     * The table of tiles.
     */
    private final MosaicTableModel tiles;

    /**
     * The main panel, with the tables of tiles on the left side and the graphical
     * representation of tiles on the right side.
     */
    private final JSplitPane pane;

    /**
     * The table of failures. Will be created only when needed.
     */
    private JTable failures;

    /**
     * The layout used for the right pane. Used for switching between the
     * "busy" pane and the pane displaying the mosaic.
     */
    private final CardLayout mosaicLayout;

    /**
     * The panel where the mosaic is painted.
     */
    private final MosaicPanel mosaic;

    /**
     * The label to animate when a computation is under progress for the right pane.
     */
    private final JXBusyLabel busy;

    /**
     * The loader task being run in background, or {@code null} if no such task is being executed.
     * This field shall be set in the Swing thread only, including its reinitialization to null.
     */
    private transient Loader loader;

    /**
     * Creates a new tiles chooser.
     */
    public MosaicChooser() {
        super(new BorderLayout());
        final Vocabulary resources = Vocabulary.getResources(null);
        final MosaicPanel mosaic = this.mosaic = new MosaicPanel();
        final MosaicTableModel tiles = this.tiles = new MosaicTableModel();
        /*
         * Builds the tables of tiles. At this stage we build only the table of successfully
         * created tiles. Later (in the "reportFailures" method), a table of failures may also
         * be created.
         */
        final JTable successTable = new JTable(tiles);
        final TableColumnModel columns = successTable.getColumnModel();
        columns.getColumn(0).setPreferredWidth(250); // Gives more space to the column of filenames.
        for (int i=2; i<=5; i++) {
            // Gives more space to the (width,height,x,y) columns,
            // since they typically contain big numbers.
            columns.getColumn(i).setPreferredWidth(100);
        }
        successTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override public void valueChanged(final ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    mosaic.setSelectedTiles(tiles.getElements(successTable.getSelectedRows()));
                }
            }
        });
        /*
         * Builds the "Add" and "Remove" buttons, together with their actions.
         * Those buttons will be inserted below the table created above.
         */
        final JButton add = new JButton(resources.getMenuLabel(Vocabulary.Keys.ADD));
        final JButton remove = new JButton(resources.getString(Vocabulary.Keys.REMOVE));
        add.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
                promptForTiles();
            }
        });
        final class RemoveAction implements ActionListener, ListSelectionListener {
            @Override public void actionPerformed(final ActionEvent event) {
                tiles.remove(successTable.getSelectedRows());
                runLoader(null);
            }
            @Override public void valueChanged(final ListSelectionEvent event) {
                remove.setEnabled(successTable.getSelectedRowCount() != 0);
            }
        }
        final RemoveAction control = new RemoveAction();
        remove.addActionListener(control);
        successTable.getSelectionModel().addListSelectionListener(control);
        remove.setEnabled(false);
        final JPanel buttons = new JPanel(new GridLayout(1,2));
        buttons.add(add);
        buttons.add(remove);
        final Box bb = Box.createHorizontalBox();
        bb.add(Box.createGlue());
        bb.add(buttons);
        bb.add(Box.createGlue());
        /*
         * Builds the left pane, which contains the following:
         *
         *   - The table of successfully loaded tiles.
         *   - The table of failures while loading tiles (DEFERRED).
         *   - The "add/remove" buttons.
         *
         * The creation of the failures table is deferred to the "reportFailures" method.
         * That method will assume that the table of successfully created tiles is the
         * component at index 1 of the left pane.
         */
        final JPanel leftPane = new JPanel(new BorderLayout());
        leftPane.add(bb, BorderLayout.SOUTH);
        leftPane.add(new JScrollPane(successTable), BorderLayout.CENTER); // Must be last (see above).
        /*
         * Builds the right pane, which contains the silhouette of the selected tiles. We use a
         * a CardLayout in order to switch between the busy state and the display of silhouettes.
         */
        mosaicLayout = new CardLayout();
        final JPanel rightPane = new JPanel(mosaicLayout);
        final JComponent mosaicPane = mosaic.createScrollPane();
        mosaicPane.setBorder(BorderFactory.createLoweredBevelBorder());
        busy = new JXBusyLabel(new Dimension(32, 32));
        busy.setVerticalAlignment(JLabel.CENTER);
        busy.setHorizontalAlignment(JLabel.CENTER);
        rightPane.add(busy, "Busy");
        rightPane.add(mosaicPane, "Mosaic");
        /*
         * Creates the split pane. The divider location has been empirically determined to
         * a value approximatively equals to the preferred table width, in order to allow
         * filename and (width,height,x,y) columns to be fully visible. If this value needs
         * to be changed, a convenient place where to display a value is in promptForTiles().
         *
         * We do not set a preferred size for the widget as a whole because the default size
         * seems good enough.
         */
        pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftPane, rightPane);
        pane.setDividerLocation(460);
        pane.setBorder(null);
        add(pane, BorderLayout.CENTER);
    }

    /**
     * Returns the table of tiles.
     *
     * @return The table of tiles.
     */
    public MosaicTableModel getTileTable() {
        return tiles;
    }

    /**
     * Returns the selected tiles as {@code TileManager} objects, or an empty array if none.
     * Only one tile manager is usually returned. However more managers may be returned if,
     * for example, {@link org.geotoolkit.image.io.mosaic.TileManagerFactory} failed to create
     * only one instance from a set of tiles.
     *
     * @return The selected tiles as {@code TileManager} objects, or an empty array if none.
     */
    public TileManager[] getSelectedTiles() {
        return (mosaic != null) ? mosaic.getTileManagers() : MosaicPanel.NO_TILES;
    }

    /**
     * Sets whatever the right pane should consider itself busy. The right pane is busy
     * after the tiles have been loaded and while the {@link TileManager} is in the process
     * of being computed.
     *
     * @param b {@code true} for displaying the busy label, or {@code false} for displaying
     *        the mosaic.
     */
    private void setBusy(final boolean b) {
        busy.setBusy(b);
        mosaicLayout.show((JComponent) pane.getRightComponent(), b ? "Busy" : "Mosaic");
    }

    /**
     * Asks the user to supply tiles. The default implementation popups an {@link ImageFileChooser}
     * and adds the selected files to the {@link MosaicTableModel}. Duplicated values are removed
     * and the remainder entries are sorted. If the creation of some tiles failed, an error dialog
     * box is displayed.
     */
    private void promptForTiles() {
        tiles.locale = getLocale();
        final Preferences prefs = Preferences.userNodeForPackage(MosaicChooser.class);
        /*
         * Setup the image chooser. The directory is set to the one
         * used last time this widget was executed, for convenience.
         */
        final ImageFileChooser fileChooser;
        fileChooser = new ImageFileChooser(prefs.get(INPUT_FORMAT, "tiff"));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setListFileFilterUsed(true);
        String home = prefs.get(INPUT_DIRECTORY, null);
        if (home != null) {
            final File directory = new File(home);
            if (directory.isDirectory()) {
                fileChooser.setCurrentDirectory(directory);
            }
        }
        if (fileChooser.showOpenDialog(this) != ImageFileChooser.APPROVE_OPTION) {
            return;
        }
        final String directory = fileChooser.getCurrentDirectory().getPath();
        prefs.put(INPUT_DIRECTORY, directory);
        runLoader(fileChooser);
        final String format = loader.getFormat();
        if (format != null) {
            prefs.put(INPUT_FORMAT, format);
        }
    }

    /**
     * Runs {@link Loader} in a background thread. This method is invoked when tiles are
     * added add when tiles are removed. In the later case, loading of tile headers can
     * be skipped.
     *
     * @param fileChooser The chooser from which to get the selected tiles. If {@code null},
     *        then loading of tile headers will be skipped - the thread will go directly to
     *        the creation of the tile manager instead.
     */
    private void runLoader(final ImageFileChooser fileChooser) {
        Loader loader = this.loader;
        if (loader != null) {
            loader.cancel = true;
        }
        this.loader = loader = new Loader(fileChooser);
        final Thread thread = new Thread(SwingUtilities.WORKER_THREADS, loader);
        thread.setDaemon(true); // Do not prevent the application to finish.
        thread.start();
    }

    /**
     * The inner class which perform the {@link MosaicChooser#promptForTiles()} work. The work is
     * performed by a single {@link #run} method, but is sliced in many sections to be executed
     * in Swing thread or in the background thread. We put everything in the same {@code run()}
     * method because the next step uses the results from the previous steps.
     */
    private final class Loader implements Runnable {
        private final File[]         files;
        private final ImageReaderSpi provider;
        private final ProgressWindow progress;

        private volatile TileManager[] managers;
        private volatile TableModel    failures;
        private volatile IOException   error;
        private volatile int           stage;
        volatile boolean cancel;

        /**
         * Creates a new tile loader.
         *
         * @param fileChooser The chooser from which to get the selected tiles. If {@code null},
         *        then loading of tile headers will be skipped - this thread will go directly to
         *        the creation of the tile manager instead.
         */
        Loader(final ImageFileChooser fileChooser) {
            if (fileChooser == null) {
                files    = null;
                provider = null;
                progress = null;
            } else {
                files    = fileChooser.getSelectedFiles();
                provider = (ImageReaderSpi) fileChooser.getCurrentProvider();
                progress = new ProgressWindow(MosaicChooser.this);
            }
        }

        /**
         * Returns the format name of the reader used for reading tile headers,
         * or {@code null} if it can not be determined.
         */
        public String getFormat() {
            final String[] names = provider.getFormatNames();
            return (names != null && names.length != 0) ? names[0] : null;
        }

        /**
         * Runs a portion of the task in a background thread. The {@link #stage} variable
         * said where we are in the code flow. Even numbers must be run in the background
         * thread while odd numbers must be run in the Swing thread.
         */
        @Override
        public void run() {
            while (!cancel) {
                final int s = stage;
                final boolean isDispatchThread = ((s & 1) != 0);
                assert EventQueue.isDispatchThread() == isDispatchThread;
                switch (s) {
                    /*
                     * Background thread: creates the Tile objects from the list of files selected
                     * by the user. Needs to be run in background because this method will open
                     * every files for fetching the image size and will read every TFW files.
                     */
                    case 0: {
                        if (files != null) {
                            failures = tiles.add(provider, files, progress);
                        }
                        break;
                    }
                    /*
                     * Swing thread: if the previous step reported some failures, creates a new
                     * JTable for displaying the list of failures. Next, set the right pane as
                     * busy before we compute the mosaic.
                     */
                    case 1: {
                        final TableModel failures = this.failures;
                        if (failures != null) {
                            reportFailures(files, failures);
                        }
                        setBusy(true);
                        break;
                    }
                    /*
                     * Background thread: computes the mosaic from the list of tiles.
                     * If we fail, remember the exception but do not process it yet.
                     */
                    case 2: {
                        try {
                            managers = tiles.getTileManager();
                        } catch (IOException e) {
                            error = e;
                        }
                        break;
                    }
                    /*
                     * Swing thread: remove duplicated tiles. Needs to be done after the mosaic
                     * has been built, because tile location has been determined only after the
                     * mosaic creation and may affect the result of Tile.equals(Object).
                     */
                    case 3: {
                        if (tiles.removeDuplicates() && error == null) {
                            stage = 2; // Creates TileManager again.
                            return;
                        }
                        setBusy(false);
                        if (error != null) {
                            ExceptionMonitor.show(MosaicChooser.this, error);
                        }
                        if (managers != null) {
                            mosaic.setTileManagers(managers);
                        }
                        loader = null; // Said to MosaicChooser that we are done.
                        break;
                    }
                    /*
                     * Background thread: we are done.
                     */
                    default: {
                        return;
                    }
                }
                /*
                 * Runs the next step in the Swing thread. If we were already in the Swing thread,
                 * we need to finish this method now in order to return to the background thread.
                 */
                stage = s+1;
                if (isDispatchThread) {
                    return;
                }
                SwingUtilities.invokeAndWait(this);
            }
        }
    }

    /**
     * If we failed to read some tiles, report the errors to the user. The report will
     * contains a table listing the tiles that we failed to read, together with the
     * exception message.
     */
    private void reportFailures(final File[] files, final TableModel failures) {
        if (this.failures != null) {
            this.failures.setModel(failures);
        } else {
            this.failures = new JTable(failures);
            final Locale locale = getLocale();
            final int count = failures.getRowCount();
            final JXHeader label = new JXHeader(
                    Vocabulary.getResources(locale).getString(Vocabulary.Keys.ERROR),
                    Descriptions.getResources(locale).getString(
                    Descriptions.Keys.ERROR_READING_SOME_FILES_$2, files.length-count, count));
            final JPanel panel = new JPanel(new BorderLayout());
            panel.add(label, BorderLayout.NORTH);
            panel.add(new JScrollPane(this.failures), BorderLayout.CENTER);
            /*
             * Inserts the failure table in the left pane. We extract the old table
             * ("successTable", which is actually a JScrollPane) from the left pane
             * and substitute it with a JSplitPane having both tables.
             */
            final JComponent leftPane = (JComponent) pane.getLeftComponent();
            final JComponent successTable = (JComponent) leftPane.getComponent(1);
            final JSplitPane tables = new JSplitPane(JSplitPane.VERTICAL_SPLIT, successTable, panel);
            // Note: adding "successTable" in "tables" has implicitly removed it from "leftPane".
            tables.setContinuousLayout(true);
            tables.setOneTouchExpandable(true);
            tables.setDividerLocation(200);
            leftPane.add(tables, BorderLayout.CENTER);
            leftPane.validate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        return SwingUtilities.showOptionDialog(owner, this, title);
    }
}
