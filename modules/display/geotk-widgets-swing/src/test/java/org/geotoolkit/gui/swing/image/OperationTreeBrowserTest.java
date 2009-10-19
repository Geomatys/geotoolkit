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

import java.awt.image.RenderedImage;
import javax.media.jai.operator.AddConstDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.GradientMagnitudeDescriptor;
import javax.media.jai.operator.MultiplyConstDescriptor;

import org.geotoolkit.gui.test.SwingBase;


/**
 * Tests the {@link OperationTreeBrowser}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 2.3
 */
public final class OperationTreeBrowserTest extends SwingBase<OperationTreeBrowser> {
    /**
     * Constructs the test case.
     */
    public OperationTreeBrowserTest() {
        super(OperationTreeBrowser.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected OperationTreeBrowser create() {
        RenderedImage image;
        final Float size = new Float(200);
        final Byte value = new Byte((byte)10);
        image = ConstantDescriptor.create(size,size, new Byte[]{value}, null);
        image = MultiplyConstDescriptor.create(image, new double[] {2}, null);
        image = GradientMagnitudeDescriptor.create(image, null, null, null);
        image = AddConstDescriptor.create(image, new double[] {35}, null);
        return new OperationTreeBrowser(image);
    }
}
