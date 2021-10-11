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
package org.geotoolkit.test.gui;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Locale;
import java.util.prefs.Preferences;
import java.util.concurrent.CountDownLatch;
import java.awt.Desktop;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameAdapter;

import static java.lang.StrictMath.*;
import static org.junit.Assert.*;


/**
 * The desktop pane where to put the widgets to be tested.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.05
 */
@SuppressWarnings("serial")
final strictfp class DesktopPane extends JDesktopPane {
    /**
     * The key for screenshot directory in the user preferences.
     */
    private static final String SCREENSHOT_DIRECTORY_PREFS = "Screenshots";

    /**
     * The desktop which contain the internal frame for each widget. Will be created only if
     * the "{@code org.geotoolkit.showWidgetTests}" system property is set to {@code true}.
     */
    private static DesktopPane desktop;

    /**
     * The menu for creating new windows.
     */
    private final JMenu newMenu;

    /**
     * A lock used for waiting that the {@linkplain #desktop} has been closed.
     */
    private final CountDownLatch lock;

    /**
     * The last active component.
     */
    private JComponent active;

    /**
     * Creates the desktop.
     */
    private DesktopPane() {
        newMenu = new JMenu("New");
        lock = new CountDownLatch(1);
    }

    /**
     * If the widgets are to be show, prepares the desktop pane which will contain them.
     * This method is invoked from JUnit test cases by methods annotated with {@code @BeforeTest}.
     *
     * @throws HeadlessException If the current environment does not allow the display of widgets.
     */
    static synchronized void prepareDesktop() throws HeadlessException {
        desktop = new DesktopPane();
        desktop.createFrame().setVisible(true);
    }

    /**
     * If a frame has been created, wait for its disposal.
     * This method is invoked from JUnit test cases by methods annotated with {@code @AfterTest}.
     *
     * @throws InterruptedException If the current thread has been interrupted while
     *         we were waiting for the frame disposal.
     */
    static void waitForFrameDisposal() throws InterruptedException {
        final DesktopPane desktop;
        synchronized (SwingTestBase.class) {
            desktop = DesktopPane.desktop;
        }
        if (desktop != null) {
            desktop.lock.await();
            synchronized (SwingTestBase.class) {
                DesktopPane.desktop = null;
            }
        }
    }

    /**
     * Shows the given component, if the test is allowed to display widgets and
     * the given component is not null.
     *
     * @param  component The component to show, or {@code null} if none.
     * @return {@code true} if the component has been shown.
     */
    static synchronized boolean show(final JComponent component) {
        boolean added = false;
        if (desktop != null && component != null) {
            desktop.show(component, 0, 1);
            added = true;
        }
        return added;
    }

    /**
     * Shows the given components, if the test is allowed to display widgets and
     * the given component is not null.
     *
     * @param  testCase The test case for which the component is added.
     * @param  components The components to show, or {@code null} if none.
     * @return {@code true} if the component has been shown.
     */
    static synchronized boolean show(final SwingTestBase<?> testCase, final JComponent... components) {
        boolean added = false;
        if (desktop != null) {
            desktop.addTestCase(testCase);
            for (int i=0; i<components.length; i++) {
                final JComponent component = components[i];
                if (component != null) {
                    desktop.show(component, i, components.length);
                    added = true;
                }
            }
        }
        return added;
    }

    /**
     * Adds a test case to be show in the "New" menu.
     * A {@code null} argument cause the addition of a separator.
     */
    private void addTestCase(final SwingTestBase<?> testCase) {
        if (testCase != null) {
            newMenu.add(new AbstractAction(getTitle(testCase.testing)) {
                @Override public void actionPerformed(final ActionEvent event) {
                    show(testCase);
                }
            });
        } else {
            newMenu.addSeparator();
        }
    }

    /**
     * Creates the frame for this desktop. This frame is initially invisible;
     * the {@link JFrame#setVisible(boolean)} method must be invoked by the caller.
     * This method shall be invoked only once.
     *
     * @return A new frame in which the desktop will be shown.
     */
    private JFrame createFrame() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(newMenu);
        if (true) {
            final JMenu menu = new JMenu("View");
            if (true) {
                final JMenu sub = new JMenu("L&F");
                final ButtonGroup group = new ButtonGroup();
                final String current = UIManager.getLookAndFeel().getName();
                for (final UIManager.LookAndFeelInfo lf : UIManager.getInstalledLookAndFeels()) {
                    final String cn = lf.getClassName();
                    final String name = lf.getName();
                    final JRadioButtonMenuItem item = new JRadioButtonMenuItem(new AbstractAction(name) {
                        @Override public void actionPerformed(final ActionEvent event) {
                            try {
                                UIManager.setLookAndFeel(cn);
                                SwingUtilities.updateComponentTreeUI(DesktopPane.this);
                            } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
                                warning(e);
                            }
                        }
                    });
                    item.setSelected(name.equals(current));
                    group.add(item);
                    sub.add(item);
                }
                menu.add(sub);
            }
            if (false) { // Doesn't seem to work...
                final JMenu sub = new JMenu("Language");
                final Locale[] locales = new Locale[] {
                    Locale.CANADA,
                    Locale.FRANCE
                };
                final String current = Locale.getDefault(Locale.Category.DISPLAY).getDisplayLanguage();
                final ButtonGroup group = new ButtonGroup();
                for (final Locale locale : locales) {
                    final String name = locale.getDisplayLanguage();
                    final JRadioButtonMenuItem item = new JRadioButtonMenuItem(new AbstractAction(name) {
                        @Override public void actionPerformed(final ActionEvent event) {
                            setLocale(locale);
                        }
                    });
                    item.setSelected(name.equals(current));
                    group.add(item);
                    sub.add(item);
                }
                menu.add(sub);
            }
            menuBar.add(menu);
        }
        if (true) {
            final JMenu menu = new JMenu("Tools");
            menu.add(new AbstractAction("Screenshot") {
                @Override public void actionPerformed(final ActionEvent event) {
                    screenshot();
                }
            });
            menu.add(new AbstractAction("Preferences") {
                @Override public void actionPerformed(final ActionEvent event) {
                    preferences();
                }
            });
            menuBar.add(menu);
        }
        if (true) {
            final JMenu menu = new JMenu("Windows");
            menu.add(new AbstractAction("List") {
                @Override public void actionPerformed(final ActionEvent event) {
                    listWindows();
                }
            });
            menuBar.add(menu);
        }
        final JFrame frame = new JFrame("Geotoolkit.org widget tests");
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(final WindowEvent event) {
                frame.removeWindowListener(this);
                lock.countDown();
                frame.dispose();
            }
        });
        frame.setJMenuBar(menuBar);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(this);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null); // Put at screen center.
        return frame;
    }

    /**
     * Returns the title to use for the widget frame.
     * This is also the filename of the screenshot.
     *
     * @param  type The widget class.
     * @return The frame title, or screenshot filename (without extension).
     */
    static String getTitle(Class<?> type) {
        while (type.isAnonymousClass()) {
            type = type.getSuperclass();
        }
        return type.getSimpleName();
    }

    /**
     * Shows the widget created by the given test case.
     */
    private void show(final SwingTestBase<?> testCase) {
        try {
            for (int i=0; i<testCase.numTests; i++) {
                show(testCase.create(i), i, testCase.numTests);
            }
        } catch (Exception e) {
            warning(e);
        }
    }

    /**
     * Show a warning dialog box for the given exception.
     */
    private void warning(final Exception e) {
        JOptionPane.showInternalMessageDialog(this, e.getLocalizedMessage(),
                e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows the given component in a frame.
     *
     * @param  component The component to show.
     */
    private void show(final JComponent component, final int index, final int numTests) {
        String title = getTitle(component.getClass());
        if (numTests != 1) {
            title = title + " (" + index + ')';
        }
        final JInternalFrame frame = new JInternalFrame(title, true, true, true, true);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override public void internalFrameActivated(final InternalFrameEvent event) {
                active = component;
            }

            @Override public void internalFrameClosed(final InternalFrameEvent event) {
                if (active == component) {
                    active = null;
                }
            }
        });
        frame.add(component);
        frame.pack();
        final Dimension size = frame.getMinimumSize();
        if (size != null) {
            frame.setSize(max(frame.getWidth(),  size.width),
                          max(frame.getHeight(), size.height));
        }
        final int numCols = (int) ceil(sqrt(numTests));
        final int numRows = (numTests + numCols - 1) / numCols;
        final int deltaX  = getWidth()  / numCols;
        final int deltaY  = getHeight() / numRows;
        frame.setLocation(deltaX * (index % numRows) + (deltaX - frame.getWidth())  / 2,
                          deltaY * (index / numRows) + (deltaY - frame.getHeight()) / 2);
        frame.setVisible(true);
        add(frame);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            warning(e); // Should never happen, but is not critical anyway.
        }
        System.out.println("Showing " + title);
    }

    /**
     * List windows known to this desktop.
     */
    private void listWindows() {
        final Component[] components = getComponents();
        final String[] titles = new String[components.length];
        for (int i=0; i<components.length; i++) {
            Component c = components[i];
            String title = String.valueOf(c.getName());
            if (c instanceof JInternalFrame) {
                final JInternalFrame ci = (JInternalFrame) c;
                title = String.valueOf(ci.getTitle());
                c = ci.getRootPane().getComponent(0);
            }
            final Dimension size = c.getSize();
            titles[i] = title + " : " + c.getClass().getSimpleName() +
                    '[' + size.width + " \u00D7 " + size.height + ']';
        }
        final JInternalFrame frame = new JInternalFrame("Windows", true, true, true, true);
        frame.add(new JScrollPane(new JList<>(titles)));
        frame.pack();
        frame.setVisible(true);
        add(frame);
    }

    /**
     * Popups a dialog box for setting the preferences.
     */
    private void preferences() {
        final Preferences prefs = Preferences.userNodeForPackage(DesktopPane.class);
        final JFileChooser chooser = new JFileChooser(prefs.get(SCREENSHOT_DIRECTORY_PREFS, null));
        chooser.setDialogTitle("Output directory for screenshots");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        switch (chooser.showOpenDialog(this)) {
            case JFileChooser.APPROVE_OPTION: {
                final File directory = chooser.getSelectedFile();
                if (directory != null) {
                    prefs.put(SCREENSHOT_DIRECTORY_PREFS, directory.getPath());
                }
                break;
            }
        }
    }

    /**
     * Takes a screenshot of the currently active component.
     */
    private void screenshot() {
        final JComponent active = this.active;
        if (active != null && active.isValid()) {
            final BufferedImage image = new BufferedImage(active.getWidth(), active.getHeight(), BufferedImage.TYPE_INT_RGB);
            final Graphics2D handler = image.createGraphics();
            active.print(handler);
            handler.dispose();
            File file = new File(Preferences.userNodeForPackage(DesktopPane.class).get(SCREENSHOT_DIRECTORY_PREFS, "."));
            file = new File(file, getTitle(active.getClass()) + ".png");
            try {
                assertTrue(ImageIO.write(image, "png", file));
                file = file.getParentFile();
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                JOptionPane.showInternalMessageDialog(active, e.getLocalizedMessage(),
                        e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showInternalMessageDialog(this, "No active window.", "Screenshot", JOptionPane.WARNING_MESSAGE);
        }
    }
}
