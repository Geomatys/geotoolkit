

package org.geotoolkit.display3d.controller;

import org.opengis.display.canvas.CanvasController;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public interface CanvasController3D extends CanvasController{

    double[] getCameraPosition();

    void setCameraPosition(double x, double y, double z);

    void setCameraSpeed(double speed);

    CoordinateReferenceSystem getObjectiveCRS();

    void addLocationSensitiveGraphic(LocationSensitiveGraphic graphic, double distance);

}
