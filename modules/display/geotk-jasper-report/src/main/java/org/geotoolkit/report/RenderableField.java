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
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicLong;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRenderable;

/**
 * Field
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class RenderableField implements JRRenderable {

    private static final String ERROR_MSG = "Should not have been called. Check that the "
                                          + "appropriate JRFieldRenderer is registered for class ";
    private static final AtomicLong INC = new AtomicLong(0);

    private final String id = "RenderableField-"+INC.incrementAndGet();
    private JRRenderable delegate = null;

    @Override
    public String getId() {
        return id;
    }

    public void setDelegate(JRRenderable delegate) {
        this.delegate = delegate;
    }

    public JRRenderable getDelegate() {
        return delegate;
    }

    @Override
    public byte getType() {
        if(delegate == null){
            throw new IllegalStateException(ERROR_MSG + this.getClass());
        }
        return delegate.getType();
    }

    @Override
    public byte getImageType() {
        if(delegate == null){
            throw new IllegalStateException(ERROR_MSG + this.getClass());
        }
        return delegate.getImageType();
    }

    @Override
    public Dimension2D getDimension() throws JRException {
        if(delegate == null){
            throw new IllegalStateException(ERROR_MSG + this.getClass());
        }
        return delegate.getDimension();
    }

    @Override
    public byte[] getImageData() throws JRException {
        if(delegate == null){
            throw new IllegalStateException(ERROR_MSG + this.getClass());
        }
        return delegate.getImageData();
    }

    @Override
    public void render(Graphics2D gd, Rectangle2D rd) throws JRException {
        if(delegate == null){
            throw new IllegalStateException(ERROR_MSG + this.getClass());
        }
        delegate.render(gd, rd);
    }

}
