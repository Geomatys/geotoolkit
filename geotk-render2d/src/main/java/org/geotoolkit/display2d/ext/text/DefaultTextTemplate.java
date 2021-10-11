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

package org.geotoolkit.display2d.ext.text;

import org.geotoolkit.display2d.ext.BackgroundTemplate;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultTextTemplate implements TextTemplate {

    private final BackgroundTemplate background;
    private final String text;

    public DefaultTextTemplate(final BackgroundTemplate background, final String text) {
        this.background = background;
        this.text = text;
    }

    @Override
    public BackgroundTemplate getBackground() {
        return background;
    }

    @Override
    public String getText() {
        return text;
    }

}
