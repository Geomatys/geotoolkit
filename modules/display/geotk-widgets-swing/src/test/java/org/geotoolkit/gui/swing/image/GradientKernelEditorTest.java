/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import org.geotoolkit.test.gui.SwingTestBase;
import org.apache.sis.test.DependsOn;


/**
 * Tests the {@link GradientKernelEditor}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 2.3
 */
@DependsOn(KernelEditorTest.class)
public final strictfp class GradientKernelEditorTest extends SwingTestBase<GradientKernelEditor> {
    /**
     * Constructs the test case.
     */
    public GradientKernelEditorTest() {
        super(GradientKernelEditor.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected GradientKernelEditor create(final int index) {
        final GradientKernelEditor test = new GradientKernelEditor();
        test.addDefaultKernels();
        return test;
    }
}
