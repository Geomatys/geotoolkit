

package org.geotoolkit.display3d.controller;

import com.ardor3d.math.type.ReadOnlyVector3;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface LocationSensitiveGraphic {

    void update(ReadOnlyVector3 cameraPosition);

}
