/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.operator.ScaleDescriptor;
import javax.swing.SwingWorker;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import org.geotoolkit.gui.swing.ZoomPane;
import org.geotoolkit.internal.GraphicsUtilities;


/**
 * A simple image viewer. This widget accepts either {@linkplain RenderedImage rendered} or
 * {@linkplain RenderableImage renderable} image. Rendered image are displayed immediately,
 * while renderable image will be rendered in a background thread when first requested.
 * <p>
 * This widget may scale down images for faster rendering. This is convenient for image
 * previews, but should not be used as a "real" (i.e. robust and accurate) renderer.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.05
 *
 * @see ImageProperties
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class ImagePane extends ZoomPane {
    /**
     * The default size for rendered image produced by a {@link RenderableImage}.
     * This is also the maximum size for a {@link RenderedImage}; bigger image
     * will be scaled down for faster rendering.
     */
    private final int renderedSize;

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
        super(UNIFORM_SCALE | TRANSLATE_X | TRANSLATE_Y | ROTATE | RESET | DEFAULT_ZOOM);
        setResetPolicy(true);
        this.renderedSize = renderedSize;
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
            final float size = (float) renderedSize;
            final float scale = Math.min((size) / image.getWidth(), (size) / image.getHeight());
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
            return new Rectangle(rendered.getMinX(),  rendered.getMinY(),
                                 rendered.getWidth(), rendered.getHeight());
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
        GraphicsUtilities.paintStackTrace(graphics, getZoomableBounds(null), error);
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
            return producer.createScaledRendering(renderedSize, 0, null);
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
