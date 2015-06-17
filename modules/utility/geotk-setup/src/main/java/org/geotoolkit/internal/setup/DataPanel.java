/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.setup;

import java.io.*;
import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteOrder;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.concurrent.ExecutionException;

import org.opengis.util.FactoryException;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.referencing.operation.transform.NTv2Transform;


/**
 * The panel displaying available data and giving the opportunity to download them.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
final class DataPanel extends JComponent {
    /**
     * Index of items to reports. The {@code COUNT} field is the total number of items.
     */
    static final int EPSG=0, NADCON=1, RGF93=2, COUNT=3;

    /**
     * User for fetching localized words.
     */
    final Vocabulary resources;

    /**
     * The status about item to reports.
     */
    private final JProgressBar[] status = new JProgressBar[COUNT];

    /**
     * The download buttons.
     */
    private final JButton[] downloads = new JButton[COUNT];

    /**
     * The panel which contains connection parameter to the EPSG database.
     * Used when the client click on the "Install" button. This must be
     * set by the caller after construction.
     */
    EPSGPanel epsgPanel;

    /**
     * Creates the panel.
     */
    DataPanel(final Vocabulary resources) {
        this.resources = resources;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 6));
        final GridBagConstraints c = new GridBagConstraints();
        c.insets.left = c.insets.right = 6;
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        for (int i=0; i<COUNT; i++) {
            c.gridy = i;
            final String label;
            final short button;
            switch (i) {
                case EPSG: {
                    label = resources.getString(Vocabulary.Keys.DataBase_1, "EPSG");
                    button = Vocabulary.Keys.Install;
                    break;
                }
                case NADCON: {
                    label = resources.getString(Vocabulary.Keys.Data_1, "NADCON");
                    button = Vocabulary.Keys.Download;
                    break;
                }
                case RGF93: {
                    label = resources.getString(Vocabulary.Keys.Data_1, "RGF93");
                    button = Vocabulary.Keys.Download;
                    break;
                }
                default: throw new AssertionError(i);
            }
            final int type = i;
            final JProgressBar state = status[i] = new JProgressBar();
            state.setStringPainted(true);
            final JButton download = downloads[i] = new JButton(resources.getString(button));
            download.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    new Download(type).execute();
                }
            });
            c.insets.top = c.insets.bottom = 0;
            c.weightx = 0; c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0; add(new JLabel(label + ':'), c);
            c.gridx = 2; add(download, c);

            c.insets.top = c.insets.bottom = 2;
            c.weightx = 1; c.fill = GridBagConstraints.BOTH;
            c.gridx = 1; add(state, c);
        }
        addComponentListener(new LoadWhenShown());
    }

    /**
     * Determines the presence of data only when first needed, which may never happen.
     * One advantage of this deferred loading mechanism is to popup the error dialog box
     * (if they was a SQL error) only if the user actually wanted to see this widget.
     */
    private final class LoadWhenShown extends ComponentAdapter {
        @Override public void componentShown(final ComponentEvent e) {
            removeComponentListener(this);
            refresh();
        }
    }

    /**
     * Refreshes all data. The operation is run in a background thread.
     */
    final void refresh() {
        for (int i=0; i<COUNT; i++) {
            final JProgressBar state = status[i];
            state.setEnabled(false);
            downloads[i].setEnabled(false);
            state.setString(resources.getMenuLabel(Vocabulary.Keys.Verifying));
        }
        refresh(RGF93, NADCON, EPSG); // Fastest refrest first.
    }

    /**
     * Refreshes the data for the given items. The items must be one or some of
     * the {@link #EPSG}, {@link #NADCON} or similar constants.
     */
    final void refresh(final int... items) {
        new SwingWorker<Object,Integer>() {
            /**
             * The exception to shown on failure, or {@code null} if none.
             */
            private Exception failure;

            /*
             * Invoked in a background thread for checking the existence of various items.
             */
            @Override
            protected Object doInBackground() {
                for (final int item : items) {
                    boolean found = false;
                    switch (item) {
                        case EPSG: {
                            try {
                                found = epsgPanel.installer().exists();
                            } catch (FactoryException ex) {
                                failure = ex;
                            }
                            break;
                        }
                        case NADCON: {
                            final File directory = Installation.NADCON.directory(true);
                            if (new File(directory, "conus.las").isFile() &&
                                new File(directory, "conus.los").isFile())
                            {
                                found = true;
                            }
                            break;
                        }
                        case RGF93: {
                            final File directory = Installation.NTv2.directory(true);
                            if (new File(directory, NTv2Transform.RGF93).isFile()) {
                                found = true;
                            }
                            break;
                        }
                    }
                    publish(found ? item : ~item);
                }
                return null;
            }

            /**
             * Processes in the Swing thread the results sent by {@link #doInBackground()}.
             * The item is positive if presents, negative (all bits inverted) if absent.
             */
            @Override
            protected void process(final List<Integer> results) {
                for (int item : results) {
                    final boolean found = (item >= 0);
                    if (!found) item = ~item;
                    final JProgressBar state = status[item];
                    state.setEnabled(true);
                    state.setString(resources.getString(found ?
                            Vocabulary.Keys.DataArePresent : Vocabulary.Keys.Nodata));
                    state.setValue(found ? 100 : 0);
                    downloads[item].setEnabled(!found);
                }
            }

            /**
             * Invoked in the Swing thread when we are done. If we got an exception, show it now.
             */
            @Override
            protected void done() {
                if (failure != null) {
                    JOptionPane.showMessageDialog(DataPanel.this, failure.getLocalizedMessage(),
                            failure.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);

                }
            }
        }.execute();
    }

    /**
     * The action to be executed in a background thread when the user
     * pressed the "Download" button. In the particular case of EPSG
     * data, this is actually an "Install" action.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.05
     *
     * @since 3.00
     * @module
     */
    private final class Download extends SwingWorker<Object,Object> implements PropertyChangeListener {
        /**
         * The button that has been clicked.
         */
        private final int item;

        /**
         * Creates a new download action.
         */
        Download(final int item) {
            this.item = item;
            status[item].setString(resources.getMenuLabel(Vocabulary.Keys.Downloading));
            downloads[item].setEnabled(false);
            addPropertyChangeListener(this);
            /*
             * Some action can not report progress. Set the status bar of those
             * to the indeterminate state.
             */
            switch (item) {
                case EPSG: {
                    status[item].setIndeterminate(true);
                }
            }
        }

        /**
         * Processes to the download in a background thread.
         */
        @Override
        protected Object doInBackground() throws Exception {
            switch (item) {
                case EPSG: {
                    epsgPanel.installer().call();
                    break;
                }
                case NADCON: {
                    final File directory = Installation.NADCON.validDirectory(true);
                    unzip(new URL("http://www.ngs.noaa.gov/PC_PROD/NADCON/GRIDS.zip"), directory);
                    break;
                }
                case RGF93: {
                    final File directory = Installation.NTv2.validDirectory(true);
                    String url = "http://lambert93.ign.fr/fileadmin/files/" + NTv2Transform.RGF93;
                    if (ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder())) {
                        final int split = url.lastIndexOf('.');
                        url = new StringBuilder(url.length() + 2).append(url, 0, split)
                                .append("_b").append(url, split, url.length()).toString();
                    }
                    copy(new URL(url), new File(directory, NTv2Transform.RGF93));
                    break;
                }
            }
            return null;
        }

        /**
         * Invoked in the Swing thread when the downloading is finished.
         * Displays an error message if the operation failed.
         */
        @Override
        protected void done() {
            status[item].setIndeterminate(false);
            try {
                get();
            } catch (InterruptedException e) {
                // Should not happen since we are done.
            } catch (ExecutionException e) {
                JOptionPane.showMessageDialog(DataPanel.this, e.getCause().toString(),
                        resources.getString(Vocabulary.Keys.Error), JOptionPane.ERROR_MESSAGE);
            }
            refresh(item);
        }

        /**
         * Copies the given stream to the given target file.
         *
         * @param  in The input stream to copy. The stream will be closed.
         * @param  target The destination file.
         * @throws IOException If an error occurred while copying the entries.
         */
        private void copy(final URL url, final File target) throws IOException {
            final URLConnection connection = url.openConnection();
            final int progressDivisor = connection.getContentLength() / 100;
            try (InputStream in = connection.getInputStream();
                 OutputStream out = new FileOutputStream(target))
            {
                int done = 0;
                final byte[] buffer = new byte[4096];
                int n; while ((n = in.read(buffer)) > 0) {
                    out.write(buffer, 0, n);
                    if (progressDivisor > 0) {
                        setProgress(Math.min(100, (done += n) / progressDivisor));
                    }
                }
            }
        }

        /**
         * Unzip the given stream to the given target directory.
         *
         * @param  in The input stream to unzip. The stream will be closed.
         * @param  target The destination directory.
         * @throws IOException If an error occurred while unzipping the entries.
         */
        private void unzip(final URL url, final File target) throws IOException {
            final URLConnection connection = url.openConnection();
            final int progressDivisor = connection.getContentLength() / 100;
            int done = 0;
            try (ZipInputStream in = new ZipInputStream(connection.getInputStream())) {
                final byte[] buffer = new byte[4096];
                ZipEntry entry;
                while ((entry = in.getNextEntry()) != null) {
                    final File file = new File(target, entry.getName());
                    try (OutputStream out = new FileOutputStream(file)) {
                        int n;
                        while ((n = in.read(buffer)) >= 0) {
                            out.write(buffer, 0, n);
                            if (progressDivisor > 0) {
                                setProgress(Math.min(100, (done += n) / progressDivisor));
                            }
                        }
                    }
                    final long time = entry.getTime();
                    if (time >= 0) {
                        file.setLastModified(time);
                    }
                    in.closeEntry();
                }
            }
        }

        /**
         * Reports progress.
         */
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            if ("progress".equals(event.getPropertyName())) {
                 status[item].setValue((Integer) event.getNewValue());
             }
        }
    }
}
