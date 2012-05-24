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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.operator.ScaleDescriptor;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.geotoolkit.util.Exceptions;
import org.geotoolkit.gui.swing.ZoomPane;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.image.ImageUtilities;


/**
 * A simple image viewer. This widget accepts either {@linkplain RenderedImage rendered} or
 * {@linkplain RenderableImage renderable} image. Rendered image are displayed immediately,
 * while renderable image will be rendered in a background thread when first requested.
 * <p>
 * This widget may scale down images for faster rendering. This is convenient for image
 * previews, but should not be used as a "real" (i.e. robust and accurate) renderer.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.07
 *
 * @see ImageProperties
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class ImagePane extends ZoomPane {
    /**
     * The space to insert between the border of this component and the progress bar, if any.
     */
    private static final int MARGIN = 24;

    /**
     * The default size for rendered image produced by a {@link RenderableImage}.
     * This is also the maximum size for a {@link RenderedImage}; bigger image
     * will be scaled down for faster rendering.
     */
    private final Dimension renderedSize;

    /**
     * The renderable image, or {@code null} if none. If non-null, then the {@code Render}
     * will transform this renderable image into a rendered one when first requested.
     * Once the image is rendered, this field is set to {@code null}.
     */
    private RenderableImage renderable;

    /**
     * The rendered image, or {@code null} if none. This image may be explicitly set
     * by {@link #setImage(RenderedImage)}, or computed by {@code Render}.
     */
    private RenderedImage rendered;

    /**
     * If the rendering failed, the exception to paint in place of the image.
     *
     * @since 3.05
     */
    private Throwable error;

    /**
     * The progress pane (including a label and a progress bar), or {@code null} if none.
     * Will be created only when {@link #getProgressPane()} is first invoked.
     */
    private ProgressPane progressPane;

    /**
     * The task which is rendering a {@link RenderableImage} in a background thread.
     * This field shall be read and write from the <cite>Swing</cite> thread only.
     */
    private transient Future<RenderedImage> render;

    /**
     * Constructs an initially empty image pane with a default rendered image size.
     */
    public ImagePane() {
        this(512);
    }

    /**
     * Constructs an initially empty image pane with the specified rendered image size.
     * The {@code renderedSize} argument is the <em>maximum</em> width and height for
     * {@linkplain RenderedImage rendered image}. Images greater than this value will be
     * scaled down for faster rendering.
     *
     * @param renderedSize The maximal image width and height.
     */
    public ImagePane(final int renderedSize) {
        this(new Dimension(renderedSize, renderedSize));
    }

    /**
     * Constructs an initially empty image pane with the specified rendered image size.
     * The {@code renderedSize} argument is the <em>maximum</em> dimension for
     * {@linkplain RenderedImage rendered image}. Images greater than this value will be
     * scaled down for faster rendering.
     *
     * @param renderedSize The maximal image dimension before to scale down.
     *
     * @since 3.07
     */
    public ImagePane(final Dimension renderedSize) {
        super(UNIFORM_SCALE | TRANSLATE_X | TRANSLATE_Y | ROTATE | RESET | DEFAULT_ZOOM);
        setResetPolicy(true);
        this.renderedSize = new Dimension(renderedSize);
    }

    /**
     * Cancel computation tasks and reset all fields to {@code null}.
     * This is invoked when the user specify a different image.
     *
     * @return The old renderable or rendered image.
     */
    private Object clear() {
        Object old = renderable;
        if (old == null) {
            old = rendered;
        }
        if (render != null) {
            render.cancel(true);
            render = null;
        }
        renderable = null;
        rendered   = null;
        error      = null;
        return old;
    }

    /**
     * Sets the source renderable image. The given image will be
     * {@linkplain RenderableImage#createDefaultRendering() rendered}
     * in a background thread when first needed.
     *
     * @param image The image to display, or {@code null} if none.
     */
    public void setImage(final RenderableImage image) {
        final Throwable error = this.error;
        final Object old = clear();
        renderable = image;
        reset();
        if (error != null) {
            firePropertyChange("error", error, null);
        }
        firePropertyChange("image", old, image);
        repaint();
    }

    /**
     * Sets the source rendered image. If the given image is larger than the size
     * given at construction time, then it will be scaled down when first needed.
     * A {@code null} value remove the current image.
     *
     * @param image The image to display, or {@code null} if none.
     */
    public void setImage(RenderedImage image) {
        if (image != null) {
            final float scale = Math.min(
                    renderedSize.width  / (float) image.getWidth(),
                    renderedSize.height / (float) image.getHeight());
            if (scale < 1) {
                final Float sc = Float.valueOf(scale);
                final Float tr = 0f; // Seems mandatory, despite what JAI javadoc said.
                image = ScaleDescriptor.create(image, sc, sc, tr, tr, null, null);
            }
        }
        final Throwable error = this.error;
        final Object old = clear();
        rendered = image;
        reset();
        if (error != null) {
            firePropertyChange("error", error, null);
        }
        firePropertyChange("image", old, image);
        repaint();
    }

    /**
     * Removes the current image (if any) and paints the stack trace of the given exception
     * instead. This method is invoked when the client code failed to create the image to
     * display, typically because of an {@link java.io.IOException}.
     * <p>
     * The error is cleaned when a {@code setImage(...)} method is invoked.
     *
     * @param error The error to paint, or {@code null} if none.
     *
     * @since 3.05
     */
    public void setError(final Throwable error) {
        final Object old = this.error;
        final Object image = clear();
        this.error = error;
        reset();
        if (image != null) {
            firePropertyChange("image", image, null);
        }
        firePropertyChange("error", old, error);
        repaint();
    }

    /**
     * Returns the progress pane, creating it if needed.
     */
    private ProgressPane getProgressPane() {
        ProgressPane progressPane = this.progressPane;
        if (progressPane == null) {
            this.progressPane = progressPane = new ProgressPane(getLocale());
            setLayout(new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            c.gridx = c.gridy = 0; c.insets.left = c.insets.right = MARGIN;
            c.fill = GridBagConstraints.HORIZONTAL; c.weightx=1;
            add(progressPane, c);
            validate();
        }
        return progressPane;
    }

    /**
     * The panel showing the progress.
     * This panel paints a translucide background below the progress bar.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.12
     *
     * @since 3.07
     * @module
     */
    private static final class ProgressPane extends JComponent {
        /** The progress label. */ final JLabel label;
        /** The progress bar.   */ final JProgressBar bar;

        /** Creates a new panel with a label initialized to a default value from the given locale. */
        ProgressPane(final Locale locale) {
            setLayout(new GridLayout(2, 1, 0, 3));
            label = new JLabel(getDefaultProgressLabel(locale), JLabel.CENTER);
            bar   = new JProgressBar();
            setOpaque(false);
            setVisible(false);
            add(label);
            add(bar);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(6, 15, 9, 15)));
        }

        /** Paints a translucent rectangle before to paint the panel. */
        @Override protected void paintComponent(final Graphics graphics) {
            final Graphics2D gr = (Graphics2D) graphics;
            final Paint oldPaint = gr.getPaint();
            gr.setColor(new Color(240, 240, 240, 192));
            gr.fill(new Rectangle2D.Float(0, 0, getWidth(), getHeight()));
            gr.setPaint(oldPaint);
            super.paintComponent(graphics);
        }
    }

    /**
     * Returns the default progress label localized in the given locale.
     */
    static String getDefaultProgressLabel(final Locale locale) {
        return Vocabulary.getResources(locale).getString(Vocabulary.Keys.LOADING);
    }

    /**
     * Shows or hide the progress bar. This method should be invoked with the value {@code true}
     * before to invoke {@link #setProgress(int)}, and invoked again with the value {@code false}
     * when the operation is completed. This will not be done automatically.
     *
     * @param visible {@code true} for showing the progress pane, or {@code false} for hiding it.
     *
     * @since 3.07
     */
    public void setProgressVisible(final boolean visible) {
        if (visible) {
            getProgressPane().setVisible(true);
        } else {
            final ProgressPane progressPane = this.progressPane;
            if (progressPane != null) {
                progressPane.setVisible(false);
            }
        }
    }

    /**
     * Returns {@code true} if the progress bar is visible, or {@code false} otherwise.
     * This method returns the last value given to the {@link #setProgressVisible(boolean)}
     * method, or {@code false} if the value has never been set.
     *
     * @return {@code true} if the progress bar is currently visible.
     *
     * @since 3.07
     */
    public boolean isProgressVisible() {
        ProgressPane progressPane = this.progressPane;
        return (progressPane != null) && progressPane.isVisible();
    }

    /**
     * Sets the label to display on top of the progress bar. If this method has never been
     * invoked, then the default value is {@code "Loading..."} localized for the current locale.
     *
     * @param label The new label to print on top of the progress bar.
     *
     * @since 3.07
     */
    public void setProgressLabel(final String label) {
        getProgressPane().label.setText(label);
    }

    /**
     * Returns the current label to display on top of the progress bar. This is the last value
     * given to the {@link #setProgressLabel(String)} method, or {@code "Loading..."} localized
     * for the current locale if the value has never been set.
     *
     * @return The current label to print on top of the progress bar.
     *
     * @since 3.07
     */
    public String getProgressLabel() {
        ProgressPane progressPane = this.progressPane;
        return (progressPane != null) ? progressPane.label.getText() : getDefaultProgressLabel(getLocale());
    }

    /**
     * Sets the progress done, as a percentage between 0 and 100 inclusive. This method can
     * be invoked during lengthly operation like reading the image from a file. The lengthly
     * operation is typically run in a background thread, but this method shall be invoked
     * from the <cite>Swing</cite> thread only.
     * <p>
     * The {@link #setProgressVisible(boolean)} method should be invoked before to lengthly
     * operation begin, and when it is finished.
     *
     * @param percentageDone The percentage done as a number between 0 and 100 inclusive.
     *
     * @since 3.07
     */
    public void setProgress(final int percentageDone) {
        getProgressPane().bar.setValue(percentageDone);
    }

    /**
     * Returns the current progress, as a percentage between 0 and 100 inclusive. This is the last
     * value given to the {@link #setProgress(int)} method, or 0 if the value has never been set.
     *
     * @return The current progress percentage.
     *
     * @since 3.07
     */
    public int getProgress() {
        ProgressPane progressPane = this.progressPane;
        return (progressPane != null) ? progressPane.bar.getValue() : 0;
    }

    /**
     * Resets the default zoom. This method overrides the default implementation in
     * order to keep the <var>y</var> axis in its Java2D direction (<var>y</var>
     * value increasing down), which is the usual direction of most image.
     */
    @Override
    public void reset() {
        reset(getZoomableBounds(null), false);
        /*
         * If the image is smaller than the widget area, computes an additional transform for
         * getting a scale factor of approximatively one. In other words, get a unscaled image.
         */
        double scale = Math.min(zoom.getScaleX(), zoom.getScaleY());
        if (scale > 1) {
            final Rectangle2D area = getArea();
            if (area != null) {
                scale = 1/scale;
                final double cx = area.getCenterX();
                final double cy = area.getCenterY();
                final AffineTransform change = AffineTransform.getTranslateInstance(cx, cy);
                change.scale(scale, scale);
                change.translate(-cx, -cy);
                transform(change);
            }
        }
    }

    /**
     * Returns the image bounds, or {@code null} if none. This is used by
     * {@link ZoomPane} in order to set the initial zoom.
     */
    @Override
    public Rectangle2D getArea() {
        final RenderedImage rendered = this.rendered; // Protect from change in an other thread
        if (rendered != null) {
            return ImageUtilities.getBounds(rendered);
        }
        return null;
    }

    /**
     * Paints the image. If the image was a {@link RenderableImage}, then a {@link RenderedImage}
     * will be computed in a background thread when this method is first invoked. If the rendering
     * fails, then the exception stack trace will be painted.
     */
    @Override
    protected void paintComponent(final Graphics2D graphics) {
        if (error == null) {
            if (rendered == null) {
                if (renderable != null && render == null) {
                    final Render r = new Render();
                    render = r;
                    r.execute();
                }
                // Leave the canvas empty. A repaint event will be posted
                // later when the rendered image will be ready.
                return;
            }
            try {
                graphics.drawRenderedImage(rendered, zoom);
                return;
            } catch (RuntimeException e) {
                error = e;
                // Fallthrough the code below.
            }
        }
        graphics.setColor(getForeground());
        Exceptions.paintStackTrace(graphics, getZoomableBounds(null), error);
    }

    /**
     * The worker which will create a {@link RenderedImage} from a {@link RenderableImage}.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.05
     *
     * @since 3.05
     * @module
     */
    private final class Render extends SwingWorker<RenderedImage,Object> {
        /**
         * The renderable image, assigned from the Swing thread and used from the background
         * thread. We must do this assignment for protecting the value from concurrent change.
         */
        private final RenderableImage producer = renderable;

        /**
         * Creates the rendered image.
         */
        @Override
        protected RenderedImage doInBackground() {
            int width  = renderedSize.width;
            int height = renderedSize.height;
            /*
             * Setting one of the dimension to zero instruct createScaledRendering(...)
             * to compute it from the other one and the aspect ratio of the image.
             */
            if (width < height) {
                width = 0;
            } else {
                height = 0;
            }
            return producer.createScaledRendering(width, height, null);
        }

        /**
         * Invoked from the Swing thread when the image creation has been completed,
         * has been interrupted or failed. This method set the image of error field
         * accordingly.
         */
        @Override
        protected void done() {
            if (render == this) {
                render = null; // Declare the task as completed.
                try {
                    rendered = get();
                } catch (InterruptedException e) {
                    /*
                     * The task has been canceled, normally from the ImagePane.clear() method.
                     * Do not change the state and do not repaint, because the caller of clear()
                     * is going to set a new image anyway.
                     */
                    return;
                } catch (ExecutionException e) {
                    error = e.getCause();
                }
                repaint();
            }
        }
    }
}
