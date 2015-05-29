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
package org.geotoolkit.image.io.mosaic;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.Collection;
import javax.imageio.ImageIO;

import org.geotoolkit.util.Exceptions;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.GraphicsUtilities;
import static java.lang.StrictMath.*;


/**
 * Displays the image read by {@link MosaicImageReader}. This is used only for visual testing
 * of the {@link org.geotoolkit.image.io.mosaic} package. This is <strong>not</strong> intended
 * to be a building block of any application, since this class is keep simple on intend. For
 * example the image is reloaded every time the component is paint, which is very inefficient
 * but appropriate for the purpose of testing the image reader.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@SuppressWarnings("serial")
public final strictfp class MosaicImageViewer extends JPanel implements ChangeListener {
    /**
     * Initial size for the canvas. Used only at construction time;
     * user can resize the window after it has been made visible.
     */
    private static final int INITIAL_WIDTH = 800, INITIAL_HEIGHT = 600;

    /**
     * Property name for {@link PropertyChangeEvent}.
     */
    private static final String X_SUBSAMPLING = "xSubsampling", Y_SUBSAMPLING = "ySubsampling";

    /**
     * The tile manager.
     */
    private final TileManager tiles;

    /**
     * The image reader.
     */
    private final MosaicImageReader reader;

    /**
     * The last {@link RenderedImage} read, or an instance of {@link Exception}
     * if an error occurred, or {@code null} if a reading is under progress.
     */
    private transient Object image;

    /**
     * The image bounds.
     */
    private final Rectangle bounds;

    /**
     * The source image area used last time the image has been painted.
     * This is computed from ({@link #centerX}, {@link #centerY}) and
     * the subsampling.
     */
    private final Rectangle view;

    /**
     * The transform from image to display.
     */
    private final AffineTransform displayToImage;

    /**
     * The image to show when the region to read is empty.
     */
    private final RenderedImage empty;

    /**
     * Coordinates of the image pixel to put at the center of the widget area.
     */
    private int centerX, centerY;

    /**
     * Subsampling along X and Y axes.
     */
    private int xSubsampling, ySubsampling;

    /**
     * Options
     */
    private boolean synchronizeXY=true, subsamplingChangeAllowed=true;

    /**
     * {@code true} while the two spinners are adjusted to the same value.
     */
    private transient boolean isSynchronizing;

    /**
     * Creates a panel for displaying the image read from the given tiles. This constructor
     * creates only the image view area. In order to get a panel including the controller,
     * invoke {@link #createControlPanel}.
     *
     * @param  tiles The tiles to display.
     * @throws IOException if an I/O operation was required and failed.
     */
    protected MosaicImageViewer(final TileManager tiles) throws IOException {
        this.tiles = tiles;
        bounds = tiles.getGridGeometry().getExtent();
        view   = new Rectangle(bounds);
        reader = new MosaicImageReader();
        reader.setInput(tiles);
        displayToImage = new AffineTransform();
        centerX = bounds.x + bounds.width  / 2;
        centerY = bounds.y + bounds.height / 2;
        xSubsampling = ySubsampling = min(bounds.width/INITIAL_WIDTH, bounds.height/INITIAL_HEIGHT);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(final MouseEvent event) {
                final Point point = event.getPoint();
                displayToImage.transform(point, point);
                centerX = point.x;
                centerY = point.y;
                repaint();
            }
        });
        try (InputStream stop = ClassLoader.getSystemResourceAsStream("toolbarButtonGraphics/general/Stop24.gif")) {
            empty = ImageIO.read(stop);
        }
    }

    /**
     * Reads the image and paints it. The image is read again every time the component
     * needs to be repainted. This is inefficient, but the purpose of this class is
     * really to test {@link MosaicImageReader}...
     *
     * @param graphics The graphics where to paint the image.
     */
    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D gr = (Graphics2D) graphics;
        final int width  = getWidth();
        final int height = getHeight();
        final Object toShow = image;
        image = null;
        if (toShow == null) {
            view.width  = width  * xSubsampling;
            view.height = height * ySubsampling;
            view.x = max(bounds.x, centerX - view.width  / 2);
            view.y = max(bounds.y, centerY - view.height / 2);
            final Rectangle region = view.intersection(bounds);
            if (view.width > region.width) {
                view.x -= (view.width - region.width) / 2;
            }
            if (view.height > region.height) {
                view.y -= (view.height - region.height) / 2;
            }
            if (region.isEmpty()) {
                image = empty;
                return;
            }
            final MosaicImageReadParam param = reader.getDefaultReadParam();
            param.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);
            param.setSubsamplingChangeAllowed(subsamplingChangeAllowed);
            param.setSourceRegion(region);
            final SwingWorker<Object,Object> worker = new SwingWorker<Object,Object>() {
                /**
                 * Loads the image in a background thread.
                 */
                @Override protected Object doInBackground() throws IOException {
                    return reader.readAsRenderedImage(0, param);
                }

                /**
                 * Invoked after the loading is done. Note that the old values of x/y subsamplings
                 * are actually unknown, because they may have been changed by the user.
                 */
                @Override protected void done() {
                    try {
                        image = get();
                        xSubsampling = param.getSourceXSubsampling();
                        ySubsampling = param.getSourceYSubsampling();
                        MosaicImageViewer.this.firePropertyChange(X_SUBSAMPLING, null, xSubsampling);
                        MosaicImageViewer.this.firePropertyChange(Y_SUBSAMPLING, null, ySubsampling);
                    } catch (ExecutionException exception) {
                        image = exception.getCause();
                    } catch (InterruptedException exception) {
                        image = exception;
                    }
                    repaint();
                }
            };
            image = param; // Just a flag saying that the reader is busy.
            worker.execute();
        } else {
            if (toShow instanceof RenderedImage) {
                final RenderedImage image = (RenderedImage) toShow;
                final int dx = (width  - image.getWidth())  / 2;
                final int dy = (height - image.getHeight()) / 2;
                displayToImage.setToTranslation(dx, dy);
                gr.drawRenderedImage(image, displayToImage);
                displayToImage.setToTranslation(view.x, view.y);
                displayToImage.scale(xSubsampling, ySubsampling);
                displayToImage.translate(-dx, -dy);
                return;
            }
            if (toShow instanceof Throwable) {
                Exceptions.paintStackTrace(gr, getBounds(), (Throwable) toShow);
                return;
            }
        }
        gr.drawString("Loading...", width/2, height/2);
    }

    /**
     * Invoked when the subsampling changed. Users should never invoke this method directly since
     * this is implementation details.
     *
     * @param event The change event.
     */
    @Override
    public void stateChanged(final ChangeEvent event) {
        final JSpinner source = (JSpinner) event.getSource();
        final String name = source.getName();
        final int value = (Integer) source.getValue();
        if (X_SUBSAMPLING.equals(name)) xSubsampling = value;
        if (Y_SUBSAMPLING.equals(name)) ySubsampling = value;
        repaint();
    }

    /**
     * Creates a control panel with this image panel and its controllers.
     *
     * @return The control panel.
     * @throws IOException If an I/O operation was required and failed.
     */
    protected JComponent createControlPanel() throws IOException {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(this, BorderLayout.CENTER);

        final JPanel subsamplings = new JPanel(new GridLayout(3,3));
        subsamplings.add(new JLabel());
        subsamplings.add(new JLabel("Requested", JLabel.CENTER));
        subsamplings.add(new JLabel("Actual", JLabel.CENTER));
        subsamplings.setBorder(BorderFactory.createTitledBorder("Subsampling"));
        final JSpinner x = addControls(subsamplings, true);
        final JSpinner y = addControls(subsamplings, false);
        synchronize(x, y);
        synchronize(y, x);

        final Box options = Box.createVerticalBox();
        final JCheckBox sync = new JCheckBox("Synchronize X and Y subsamplings", synchronizeXY);
        final JCheckBox alch = new JCheckBox("Allow subsampling changes", subsamplingChangeAllowed);
        sync.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(final ChangeEvent event) {
                synchronizeXY = sync.isSelected();
            }
        });
        alch.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(final ChangeEvent event) {
                subsamplingChangeAllowed = alch.isSelected();
            }
        });
        options.add(sync);
        options.add(alch);
        options.setBorder(BorderFactory.createTitledBorder("Options"));
        panel.add(options, BorderLayout.EAST);

        final JPanel controls = new JPanel(new BorderLayout());
        controls.add(subsamplings, BorderLayout.WEST);
        controls.add(options, BorderLayout.EAST);
        controls.setBorder(BorderFactory.createEmptyBorder(6, 9, 9, 9));
        panel.add(controls, BorderLayout.SOUTH);
        panel.setOpaque(false);

        final JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Image", panel);
        if (tiles instanceof TreeTileManager) {
            final TreeTileManagerViewer intern = new TreeTileManagerViewer((TreeTileManager) tiles);
            tabs.addTab("TreeTileManager", intern.createControlPanel());
        }
        return tabs;
    }

    /**
     * Adds the label and controls for one axis.
     */
    private JSpinner addControls(final Container subsamplings, final boolean isX) {
        final String label, property;
        final int subsampling;
        if (isX) {
            label = "X:";
            property = X_SUBSAMPLING;
            subsampling = xSubsampling;
        } else {
            label = "Y:";
            property = Y_SUBSAMPLING;
            subsampling = ySubsampling;
        }
        final JSpinner request = new JSpinner();
        final SpinnerNumberModel model = (SpinnerNumberModel) request.getModel();
        model.setMinimum(1);
        model.setValue(subsampling);
        request.setName(property);
        request.addChangeListener(this);
        final JTextField actual = new JTextField(model.getValue().toString());
        addPropertyChangeListener(property, new PropertyChangeListener() {
            @Override public void propertyChange(final PropertyChangeEvent event) {
                actual.setText(String.valueOf(event.getNewValue()));
            }
        });
        actual.setEditable(false);
        actual.setHorizontalAlignment(JTextField.RIGHT);
        subsamplings.add(new JLabel(label, JLabel.CENTER));
        subsamplings.add(request);
        subsamplings.add(actual);
        return request;
    }

    /**
     * Sets {@code target} to the same value than {@code source} when the later changed.
     */
    private void synchronize(final JSpinner source, final JSpinner target) {
        source.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(final ChangeEvent event) {
                if (synchronizeXY && !isSynchronizing) {
                    isSynchronizing = true;
                    target.setValue(source.getValue());
                    isSynchronizing = false;
                }
            }
        });
    }

    /**
     * Displays this viewer in a frame. This is a convenience method only.
     *
     * @throws IOException If an I/O operation was required and failed.
     */
    public void showInFrame() throws IOException {
        final JFrame frame = new JFrame("MosaicImageReader");
        frame.add(createControlPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    /**
     * Loads a serialized {@link TileManager} and display it.
     *
     * @param  args A single command-line argument which is the name of the serialized mosaic.
     * @throws IOException If the tiles can not be deserialized.
     * @throws ClassNotFoundException If the serialized stream contains an unknown class.
     */
    public static void main(final String[] args) throws IOException, ClassNotFoundException {
        final Object tiles;
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(args[0])))) {
            tiles = in.readObject();
        }
        final TileManager manager;
        if (tiles instanceof TileManager) {
            manager = (TileManager) tiles;
        } else if (tiles instanceof Tile[]) {
            manager = TileManagerFactory.DEFAULT.create((Tile[]) tiles)[0];
        } else if (tiles instanceof Collection<?>) {
            @SuppressWarnings({"unchecked","rawtypes"})
            final Collection<Tile> c = (Collection) tiles;
            manager = TileManagerFactory.DEFAULT.create(c)[0];
        } else {
            System.out.println("Unsupported type: " + tiles.getClass());
            return;
        }
        GraphicsUtilities.setLookAndFeel(MosaicImageViewer.class, "main");
        final MosaicImageViewer viewer = new MosaicImageViewer(manager);
        viewer.showInFrame();
    }
}
