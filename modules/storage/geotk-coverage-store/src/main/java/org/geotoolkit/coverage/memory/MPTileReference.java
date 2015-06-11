/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.coverage.memory;

import java.awt.Point;
import java.awt.image.RenderedImage;
import org.geotoolkit.storage.coverage.DefaultTileReference;

/**
 *
 * @author rmarechal
 */
public class MPTileReference extends DefaultTileReference {

    public MPTileReference(RenderedImage input, int imageIndex, Point position) {
        super(IImageReader.IISpi.INSTANCE, input, imageIndex, position);
    }

    @Override
    public RenderedImage getInput() {
        return (RenderedImage) super.getInput();
    }
}
