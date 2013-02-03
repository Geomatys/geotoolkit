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
package org.geotoolkit.gui.swing;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.tree.DefaultMutableTreeNode;

import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ImageReaderWriterSpi;

import java.util.Map;
import java.util.Date;
import java.util.Locale;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.media.jai.JAI;

import org.geotoolkit.util.Version;
import org.geotoolkit.util.Strings;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.internal.DaemonThread;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.gui.swing.image.RegisteredOperationBrowser;


/**
 * An "About" dialog box. This dialog box contains the application's title and some system
 * informations (Java and OS version, free memory, image readers and writers, running threads,
 * etc.). The application version can be fetched from a {@link Manifest} object, usually build
 * from the {@code META-INF/Manifest.mf} file. This manifest should contains entries for
 * {@code Implementation-Title}, {@code Implementation-Version} and {@code Implementation-Vendor}
 * values, as suggested in the
 * <A HREF="http://java.sun.com/docs/books/tutorial/jar/basics/manifest.html#versioning">Java
 * tutorial</A>.
 * <p>
 * In addition to the above-cited standard entries, the {@code About} class understand also
 * an optional {@code Implementation-Date} entry. This entry can contains the product date
 * in the <code>"yyyy-MM-dd HH:mm:ss"</code> pattern. If presents, this date will be localized
 * according user's locale and appended to the version number.
 * <p>
 * If none of the above information is available, then {@link Version#GEOTOOLKIT} is used.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.12
 *
 * @since 2.0
 * @module
 */
@SuppressWarnings("serial")
public class About extends JComponent implements Dialog {
    /**
     * The amount of bytes in one "unit of memory" to be displayed.
     */
    private static final float HEAP_SIZE_UNIT = (1024f * 1024f);

    /**
     * The entry for timestamp in the manifest file.
     */
    private static final String TIMESTAMP = "Implementation-Date";

    /**
     * Thread qui aura la charge de faire des mises à jour en arrière-plan.
     * Ce champ sera {@code null} s'il n'y en a pas.
     */
    private final ThreadList updater;

    /**
     * The localized resources to use.
     */
    private final Vocabulary resources;

    /**
     * Constructs a new dialog box with the Geotk logo.
     */
    public About() {
        this("org/geotoolkit/resources/Geotoolkit.png", About.class, Threads.GEOTOOLKIT);
    }

    /**
     * Constructs a new dialog box for the specified application class. This constructor
     * uses the class loader for loading the manifest file. It also uses the class package
     * to find the right entry into the manifest.
     *
     * @param logo The application's logo. It may be a {@link JComponent}, an {@link Icon} object
     *        or an resource path (i.e. a file to be fetch in the classpath) as a {@link String}.
     * @param application The application's class. Application name will be fetch from the manifest
     *        file ({@code META-INF/Manifest.mf}).
     * @param tasks Group of running threads, or {@code null} if there is none.
     */
    public About(final Object logo, final Class<?> application, final ThreadGroup tasks) {
        this(logo, getAttributes(application), application.getClassLoader(), tasks);
    }

    /**
     * Constructs a new dialog box from the specified manifest attributes.
     *
     * @param logo The application's logo. It may be a {@link JComponent}, an {@link Icon} object
     *        or an resource path (i.e. a file to be fetch in the classpath) as a {@link String}.
     * @param attributes The manifest attributes containing application name and version number.
     * @param tasks Group of running threads, or {@code null} if there is none.
     */
    public About(final Object logo, final Attributes attributes, final ThreadGroup tasks) {
        this(logo, attributes, null, tasks);
    }

    /**
     * Constructs a new dialog box.
     *
     * @param logo The application's logo. It may be a {@link JComponent}, an {@link Icon} object
     *        or an resource path (i.e. a file to be fetch in the classpath) as a {@link String}.
     * @param attributes The manifest attributes containing application name and version number.
     * @param loader The application's class loader.
     * @param tasks Group of running threads, or {@code null} if there is none.
     */
    private About(final Object logo, final Attributes attributes,
                  ClassLoader loader, final ThreadGroup tasks)
    {
        setLayout(new GridBagLayout());
        final Locale locale = getDefaultLocale();
        resources = Vocabulary.getResources(locale);
        if (loader == null) {
            loader = getClass().getClassLoader();
            // TODO: it would be nice to fetch the caller's class loader instead
        }
        /*
         * Get the free memory before any futher work.
         */
        final Runtime    system = Runtime.getRuntime(); system.gc();
        final float  freeMemory = system.freeMemory()  / HEAP_SIZE_UNIT;
        final float totalMemory = system.totalMemory() / HEAP_SIZE_UNIT;
        /*
         * Get application's name, version and vendor from the manifest attributes.
         * If an implementation date is specified, append it to the version string.
         */
        String application = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
        String version     = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        String vendor      = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
        try  {
            final String dateString = attributes.getValue(TIMESTAMP);
            if (dateString != null) {
                final Date         date = getDateFormat().parse(dateString);
                final DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);
                if (version != null && !version.trim().isEmpty()) {
                    StringBuffer buffer = new StringBuffer(version);
                    buffer.append(" (");
                    buffer = format.format(date, buffer, new FieldPosition(0));
                    buffer.append(')');
                    version = buffer.toString();
                }
                else {
                    version = format.format(date);
                }
            }
        } catch (ParseException exception) {
            /*
             * The implementation date can't be parsed. This is not a show-stopper;
             * the "About" dialog box will just not includes the implementation date.
             */
            Logging.unexpectedException(About.class, "<init>", exception);
        }
        if (application == null) {
            application = "<html><h2>Geotoolkit.org</h2></html>";
            if (version == null) {
                version = Version.GEOTOOLKIT.toString();
            }
        }
        /*
         * If the user supplied a logo, load it and display it in the dialog's upper part (NORTH).
         * The tabbed pane will be added below the logo, in the dialog's central part (CENTER).
         */
        final GridBagConstraints gc = new GridBagConstraints();
        if (logo != null) {
            final JComponent title;
            if (logo instanceof JComponent) {
                title = (JComponent) logo;
            } else if (logo instanceof Icon) {
                title = new JLabel((Icon) logo);
            } else {
                final String text = String.valueOf(logo);
                final URL url = loader.getResource(text);
                if (url == null) {
                    final JLabel label = new JLabel(text);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(BorderFactory.createEmptyBorder(
                                6/*top*/, 6/*left*/, 6/*bottom*/, 6/*right*/));
                    title = label;
                } else {
                    title = new JLabel(new ImageIcon(url));
                }
            }
            title.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(0/*top*/, 0/*left*/, 6/*bottom*/, 0/*right*/),
                            BorderFactory.createCompoundBorder(
                            BorderFactory.createLoweredBevelBorder(), title.getBorder())));
            gc.gridx=0; gc.gridy=0; gc.weightx=1;
            gc.insets.top=9;
            add(title, gc);
        }
        final JTabbedPane        tabs = new JTabbedPane();
        final JLabel totalMemoryLabel = new JLabel(resources.getString(Vocabulary.Keys.MEMORY_HEAP_SIZE_$1,  totalMemory));
        final JLabel percentUsedLabel = new JLabel(resources.getString(Vocabulary.Keys.MEMORY_HEAP_USAGE_$1, 1 - freeMemory/totalMemory));
        gc.gridx=0; gc.gridy=1; gc.weightx=1; gc.weighty=1; gc.fill=GridBagConstraints.BOTH;
        add(tabs, gc);
        /*
         * MAIN TAB (Application name and version informations)
         */
        if (true) {
            final JPanel pane = new JPanel(new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            c.gridx=0; c.weightx=1;
            c.gridy=0; c.insets.top=12;
            pane.add(new JLabel(application), c);
            c.gridy++; c.insets.top=0;
            pane.add(new JLabel(resources.getString(Vocabulary.Keys.VERSION_$1, version)), c);
            c.gridy++;
            pane.add(new JLabel(vendor), c);
            c.gridy++; c.insets.top=6;
            pane.add(new JLabel(resources.getString(Vocabulary.Keys.JAVA_VERSION_$1,
                    System.getProperty("java.version"))), c);
            c.gridy++; c.insets.top=0;
            pane.add(new JLabel(resources.getString(Vocabulary.Keys.JAVA_VENDOR_$1,
                    System.getProperty("java.vendor" ))), c);
            c.gridy++; c.insets.top=6;
            pane.add(new JLabel(resources.getString(Vocabulary.Keys.OS_NAME_$1,
                    System.getProperty("os.name"))), c);
            c.gridy++; c.insets.top=0;
            pane.add(new JLabel(resources.getString(Vocabulary.Keys.OS_VERSION_$2,
                    System.getProperty("os.version"), System.getProperty("os.arch"))), c);
            c.gridy++; c.insets.top=12;
            pane.add(new JLabel(resources.getString(Vocabulary.Keys.TILE_CACHE_CAPACITY_$1,
                 JAI.getDefaultInstance().getTileCache().getMemoryCapacity()/HEAP_SIZE_UNIT)), c);
            c.gridy++; c.insets.top=0;
            pane.add(totalMemoryLabel, c);
            c.gridy++; c.insets.bottom=12;
            pane.add(percentUsedLabel, c);
            pane.setOpaque(false);
            tabs.addTab(resources.getString(Vocabulary.Keys.SYSTEM), pane);
        }
        /*
         * RUNNING TASKS TAB
         */
        if (tasks != null) {
            updater = new ThreadList(tasks, totalMemoryLabel, percentUsedLabel, resources);
            final JPanel pane = new JPanel(new BorderLayout());
            final JList<String> list = new JList<>(updater);
            pane.add(new JLabel(resources.getString(Vocabulary.Keys.RUNNING_TASKS)), BorderLayout.NORTH);
            pane.add(new JScrollPane(list), BorderLayout.CENTER);
            pane.setBorder(BorderFactory.createEmptyBorder(9,9,9,9));
            pane.setOpaque(false);
            tabs.addTab(resources.getString(Vocabulary.Keys.TASKS), pane);
        } else {
            updater = null;
        }
        /*
         * IMAGE ENCODERS/DECODERS TAB
         */
        if (true) {
            final StringBuilder rootName = new StringBuilder();
            final Map<String, DefaultMutableTreeNode[]> mimes = new TreeMap<>();
            /*
             * The array in the above map will have a length of 2. The first element is for
             * readers, and the second element is for writer. The following loop is executed
             * twice: first for readers, then for writers.
             */
            for (int index=0; index<2; index++) {
                final int titleKey;
                final Class<? extends ImageReaderWriterSpi> category;
                switch (index) {
                    case 0: {
                        titleKey = Vocabulary.Keys.DECODERS;
                        category = ImageReaderSpi.class;
                        break;
                    }
                    case 1: {
                        titleKey = Vocabulary.Keys.ENCODERS;
                        category = ImageWriterSpi.class;
                        break;
                    }
                    default: throw new AssertionError(index);
                }
                final String title = resources.getString(titleKey);
                final Iterator<? extends ImageReaderWriterSpi> it =
                        IIORegistry.getDefaultInstance().getServiceProviders(category, true);
                while (it.hasNext()) {
                    final ImageReaderWriterSpi spi = it.next();
                    final String name = spi.getDescription(locale);
                    final String[] mimeTypes = patchMimes(spi.getMIMETypes());
                    for (final String mimeType : mimeTypes) {
                        DefaultMutableTreeNode[] childs = mimes.get(mimeType);
                        if (childs == null) {
                            childs = new DefaultMutableTreeNode[2];
                            mimes.put(mimeType, childs);
                        }
                        DefaultMutableTreeNode child = childs[index];
                        if (child == null) {
                            child = new DefaultMutableTreeNode(title);
                            childs[index] = child;
                        }
                        child.add(new DefaultMutableTreeNode(name, false));
                    }
                }
                if (rootName.length() != 0) {
                    rootName.append(" / ");
                }
                rootName.append(title);
            }
            final DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootName.toString());
            for (final Map.Entry<String, DefaultMutableTreeNode[]> entry : mimes.entrySet()) {
                final DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getKey());
                root.add(node);
                final DefaultMutableTreeNode[] childs = entry.getValue();
                for (int i=0; i<childs.length; i++) {
                    if (childs[i] != null) {
                        node.add(childs[i]);
                    }
                }
            }
            JComponent tree = new JTree(root);
            tree.setBorder(BorderFactory.createEmptyBorder(6,6,0,0));
            tree = new JScrollPane(tree);
            tabs.addTab(resources.getString(Vocabulary.Keys.IMAGES), setup(tree));
        }
        /*
         * JAI OPERATIONS TAB
         */
        if (true) {
            final JComponent tree = new RegisteredOperationBrowser();
            tabs.addTab(resources.getString(Vocabulary.Keys.OPERATIONS), setup(tree));
        }
    }

    /**
     * Setup the border for the specified component.
     */
    private static JComponent setup(JComponent component) {
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(3,3,3,3), component.getBorder()));
        component.setPreferredSize(new Dimension(200, 200));
        component.setOpaque(false);
        return component;
    }

    /**
     * Patch the mime type, replacing "" by "(untitled)" for JAI I/O codec.
     * This happen mostly for RAW format, but we have no guaranteed that it
     * doesn't happen for other format.
     */
    private String[] patchMimes(String[] mimes) {
        if (mimes == null) {
            mimes = new String[] {""};
        }
        for (int i=0; i<mimes.length; i++) {
            String name = mimes[i].trim();
            if (name.isEmpty()) {
                name = resources.getString(Vocabulary.Keys.UNTITLED);
            }
            mimes[i] = name;
        }
        return mimes;
    }

    /**
     * Returns attribute for the specified class.
     */
    private static Attributes getAttributes(final Class<?> classe) {
        InputStream stream = classe.getClassLoader().getResourceAsStream("META-INF/Manifest.mf");
        if (stream != null) try {
            final Manifest manifest = new Manifest(stream);
            stream.close();
            String name = classe.getName().replace('.','/');
            int index;
            while ((index=name.lastIndexOf('/'))>=0) {
                final Attributes attributes = manifest.getAttributes(name.substring(0, index+1));
                if (attributes!=null) return attributes;
                name = name.substring(0, index);
            }
            return manifest.getMainAttributes();
        } catch (IOException e) {
            Logging.unexpectedException(About.class, "getAttributes", e);
        }
        // Use empty manifest attributes.
        return new Attributes();
    }

    /**
     * Returns a neutral date format for timestamp.
     */
    private static DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
    }

    /**
     * List of active thread in a given thread group.
     * This list will update itself in a background thread.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.09
     *
     * @since 2.0
     * @module
     */
    @SuppressWarnings("serial")
    private static final class ThreadList extends AbstractListModel<String> implements Runnable {
        /**
         * The thread which update {@code ThreadList}, or {@code null} if none.
         * Setting this field to {@code null} will stop any current process.
         */
        transient DaemonThread worker;

        /**
         * The group of threads to display.
         */
        private final ThreadGroup tasks;

        /**
         * The list of threads to display. This list will be updated in a background
         * thread (the {@linkplain #worker}) on a regular basis.
         */
        private String[] names = Strings.EMPTY;

        /**
         * The label where to write the total amount of memory.
         */
        private final JLabel totalMemory;

        /**
         * The laber where to write the percentage of memory currently in use.
         */
        private final JLabel percentUsed;

        /**
         * The localized resources to use.
         */
        private final Vocabulary resources;

        /**
         * Creates a list of threads declared in the given thread group.
         */
        public ThreadList(final ThreadGroup tasks,  final JLabel totalMemory,
                          final JLabel percentUsed, final Vocabulary resources)
        {
            this.tasks       = tasks;
            this.totalMemory = totalMemory;
            this.percentUsed = percentUsed;
            this.resources   = resources;
        }

        /**
         * Returns the number of elements currently in this list.
         */
        @Override
        public int getSize() { // NO synchronized here
            return names.length;
        }

        /**
         * Returns the element at the given index in this list.
         */
        @Override
        public String getElementAt(final int index) { // NO synchronized here
            return names[index];
        }

        /**
         * Starts the thread, if not already running.
         */
        public synchronized void start() {
            if (worker == null) {
                worker = new DaemonThread(SwingUtilities.THREAD_GROUP, this, resources.getString(Vocabulary.Keys.ABOUT));
                worker.setPriority(Thread.NORM_PRIORITY - 1);
                worker.start();
            }
        }

        /**
         * Updates the content of the "About" pane on a regular basis. The loop can
         * be interrupted by setting the {@link #tasks} field to {@code null}.
         */
        @Override
        public synchronized void run() {
            String oldTotalMemory = null;
            String oldPercentUsed = null;
            while (worker == Thread.currentThread() && !worker.isKillRequested() &&
                    listenerList.getListenerCount() != 0)
            {
                final Runtime     system = Runtime.getRuntime();
                final float  freeMemoryN = system.freeMemory()  / HEAP_SIZE_UNIT;
                final float totalMemoryN = system.totalMemory() / HEAP_SIZE_UNIT;
                String totalMemoryText = resources.getString(Vocabulary.Keys.MEMORY_HEAP_SIZE_$1,
                        totalMemoryN);
                String percentUsedText = resources.getString(Vocabulary.Keys.MEMORY_HEAP_USAGE_$1,
                        1 - freeMemoryN/totalMemoryN);

                Thread[] threadArray = new Thread[tasks.activeCount()];
                String[] threadNames = new String[tasks.enumerate(threadArray)];
                int c=0; for (int i=0; i<threadNames.length; i++) {
                    if (threadArray[i] != worker) {
                        threadNames[c++] = threadArray[i].getName();
                    }
                }
                threadNames = ArraysExt.resize(threadNames, c);
                if (Arrays.equals(names, threadNames)) {
                    threadNames = null;
                }
                if (totalMemoryText.equals(oldTotalMemory)) {
                    totalMemoryText = null;
                } else {
                    oldTotalMemory = totalMemoryText;
                }
                if (percentUsedText.equals(oldPercentUsed)) {
                    percentUsedText = null;
                } else {
                    oldPercentUsed = percentUsedText;
                }
                if (threadNames != null || totalMemoryText != null || percentUsedText != null) {
                    final String[]     names = threadNames;
                    final String totalMemory = totalMemoryText;
                    final String percentUsed = percentUsedText;
                    EventQueue.invokeLater(new Runnable() {
                        @Override public void run() {
                            update(names, totalMemory, percentUsed);
                        }
                    });
                }
                try {
                    wait(4000);
                } catch (InterruptedException exception) {
                    // Someone asked for interruption. The panel will not be updated anymore.
                    break;
                }
            }
            worker = null;
        }

        /**
         * Updates the content of the thread list. This method shall be
         * invoked on the Swing thread only.
         */
        private synchronized void update(final String[] newNames,
                                         final String totalMemory,
                                         final String percentUsed)
        {
            if (newNames != null) {
                final int count = Math.max(names.length, newNames.length);
                names = newNames;
                fireContentsChanged(this, 0, count-1);
            }
            if (totalMemory != null) this.totalMemory.setText(totalMemory);
            if (percentUsed != null) this.percentUsed.setText(percentUsed);
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
     * Popups the dialog box and waits for the user. This method always invoke {@link #start}
     * before showing the dialog, and {@link #stop} after disposing it.
     *
     * @param owner The component which will be the owner of this component.
     */
    public void showDialog(final Component owner) {
        showDialog(owner, resources.getMenuLabel(Vocabulary.Keys.ABOUT));
    }

    /**
     * Popups the dialog box and waits for the user. This method always invoke {@link #start}
     * before showing the dialog, and {@link #stop} after disposing it.
     *
     * @param  owner The component which will be the owner of this component.
     * @param  title The title to write in the window bar.
     * @return Always {@code false} for this component.
     *
     * @since 3.00
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        try {
            start();
            SwingUtilities.showMessageDialog(owner, this, title, JOptionPane.PLAIN_MESSAGE);
        } finally {
            stop();
        }
        return false;
    }

    /**
     * Starts a daemon thread updating the information shown in this {@code About} widget. Updated
     * information include available memory and the list of running tasks. <strong>You <u>must</u>
     * invoke the {@link #stop} method after {@code start()}</strong> (typically in a {@code try
     * ... finally} construct) in order to free resources. {@code stop()} will not be automatically
     * invoked by the garbage collector.
     */
    protected void start() {
        final ThreadList updater = this.updater;
        if (updater != null) {
            updater.start();
        }
    }

    /**
     * Frees any resources used by this dialog box. <strong>This method must be invoked after
     * {@link #start}</strong> in order to free resources, since {@code stop()} is not invoked
     * automatically by the garbage collector.
     */
    protected void stop() {
        final ThreadList updater = this.updater;
        if (updater != null) {
            updater.worker = null; // Stop the thread.
        }
    }
}
