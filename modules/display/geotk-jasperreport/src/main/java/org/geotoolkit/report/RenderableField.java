/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.report;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicLong;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sf.jasperreports.renderers.Renderable;

/**
 * Field
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class RenderableField implements Graphics2DRenderable, Renderable {

    private static final String ERROR_MSG = "Should not have been called. Check that the "
                                          + "appropriate JRFieldRenderer is registered for class ";
    private static final AtomicLong INC = new AtomicLong(0);

    private final String id = "RenderableField-"+INC.incrementAndGet();
    private Graphics2DRenderable delegate = null;

    @Override
    public String getId() {
        return id;
    }

    public void setDelegate(final Graphics2DRenderable delegate) {
        this.delegate = delegate;
    }

    public Graphics2DRenderable getDelegate() {
        return delegate;
    }

    @Override
    public void render(JasperReportsContext jrc, Graphics2D gd, Rectangle2D rd) throws JRException {
        if (delegate == null) {
            throw new IllegalStateException(ERROR_MSG + this.getClass());
        }
        delegate.render(jrc, gd, rd);
    }

}
