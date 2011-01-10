/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.display2d.ext.image;

import java.awt.image.BufferedImage;
import org.geotoolkit.display2d.ext.BackgroundTemplate;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultImageTemplate implements ImageTemplate {

    private final BackgroundTemplate background;
    private final BufferedImage image;

    public DefaultImageTemplate(final BackgroundTemplate background, final BufferedImage image) {
        this.background = background;
        this.image = image;
    }

    @Override
    public BackgroundTemplate getBackground() {
        return background;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

}
