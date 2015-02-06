package org.geotoolkit.gui.javafx.render2d;

import java.awt.geom.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.javafx.render2d.navigation.AbstractMouseHandler;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FXPanMouseListen extends AbstractMouseHandler {

    private static final double DEFAULT_ZOOM_FACTOR = 2;

    private double startX;
    private double startY;
    private double lastX;
    private double lastY;
    
    protected MouseButton mousebutton = null;

    protected final FXAbstractNavigationHandler owner;
    private final double zoomFactor;

    public FXPanMouseListen(final FXAbstractNavigationHandler owner) {
        this(owner, DEFAULT_ZOOM_FACTOR);
    }

    public FXPanMouseListen(final FXAbstractNavigationHandler owner, final double zoomFactor) {
        ArgumentChecks.ensureNonNull("Parent map handler", owner);
        this.owner = owner;
        if (zoomFactor >= 0 && zoomFactor <= Double.MAX_VALUE) {
            this.zoomFactor = zoomFactor;
        } else {
            this.zoomFactor = DEFAULT_ZOOM_FACTOR;
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        lastX = startX;
        lastY = startY;
        mousebutton = e.getButton();
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        lastX = 0;
        lastY = 0;
        mousebutton = e.getButton();
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        double endX = e.getX();
        double endY = e.getY();

        if (!owner.isStateFull()) {
            owner.decorationPane.setBuffer(null);

            if (mousebutton == MouseButton.PRIMARY || mousebutton == MouseButton.SECONDARY) {
                owner.decorationPane.setFill(false);
                owner.decorationPane.setCoord(-10, -10, -10, -10, false);
                owner.processDrag(startX, startY, endX, endY);
            }
        }

        lastX = 0;
        lastY = 0;
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        owner.decorationPane.setFill(false);
        owner.decorationPane.setCoord(-10, -10, -10, -10, true);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if ((lastX != 0) || (lastY != 0)) {
            
            double dx = lastX - startX;
            double dy = lastY - startY;

            if (owner.isStateFull()) {
                if (mousebutton == MouseButton.PRIMARY || mousebutton == MouseButton.SECONDARY) {
                    owner.processDrag(lastX, lastY, x, y);
                }
            } else {
                if (owner.decorationPane.getBuffer() == null) {
                    owner.decorationPane.setBuffer(owner.map.getCanvas().getSnapShot());
                }                
                owner.decorationPane.setFill(true);
                owner.decorationPane.setCoord(dx, dy, owner.map.getWidth() + dx, owner.map.getHeight() + dy, true);
            }
        }

        lastX = x;
        lastY = y;
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        lastX = startX;
        lastY = startY;
    }

    @Override
    public void mouseWheelMoved(final ScrollEvent e) {
        double rotate = -e.getDeltaY();
        if (rotate < 0) {
            owner.scale(new Point2D.Double(startX, startY), zoomFactor);
        } else if (rotate > 0) {
            owner.scale(new Point2D.Double(startX, startY), 1d / zoomFactor);
        }
    }
}
