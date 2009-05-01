/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.geom.Point2D;


/**
 * An interface for viewers that may be deformed by some artefacts. For example the {@link ZoomPane}
 * viewer is capable to show a {@linkplain ZoomPane#setMagnifierVisible magnifying glass} on top of
 * the usual content. The presence of a magnifying glass deforms the viewer in that the apparent
 * position of pixels within the glass are moved. This interface allows for corrections of apparent
 * pixel position in order to get the position we would have if no deformations existed.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.0
 *
 * @since 2.0
 * @module
 */
public interface DeformableViewer {
    /**
     * Corrects a pixel's coordinates by removing the effect of deformations induced by magnifying
     * glass or similar effects. Without this method, transformations from pixels to geographic
     * coordinates would not give exact results for pixels inside the magnifying glass since the
     * glass moves the pixel's apparent position. Invoking this method will remove any deformation
     * effects using the following steps:
     * <p>
     * <ul>
     *   <li>If the pixel's coordinate {@code point} is outside the deformed area (for example
     *       outside the magnifying glass), then this method do nothing.</li>
     *   <li>Otherwise this method update {@code point} in such a way that it contains the
     *       position that the same pixel would have in the absence of deformations.</li>
     * </ul>
     *
     * @param point On input, a pixel coordinate as it appears on the screen. On output, the
     *        coordinate that the same pixel would have if the deformation wasn't presents.
     */
    void correctApparentPixelPosition(Point2D point);
}
