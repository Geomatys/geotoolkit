/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.test;

import org.junit.*;
import org.geotoolkit.gui.swing.*;
import org.geotoolkit.gui.swing.image.*;
import org.geotoolkit.gui.swing.coverage.*;
import org.geotoolkit.gui.swing.referencing.*;
import org.geotoolkit.test.gui.SwingTestBase;


/**
 * Group in a single place a selection of widget tests.
 * <p>
 * By default the widgets are not visible; the test suite merely test that no exception is
 * thrown during construction. To make the widgets visible, the following system property
 * must be set to {@code true}: {@code org.geotoolkit.showWidgetTests}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.05
 */
public final strictfp class SwingSuite extends SwingTestBase<About> {
    /**
     * Creates a new test suite.
     */
    public SwingSuite() {
        super(About.class);
    }

    /**
     * Returns the {@link About} dialog box.
     */
    @Override
    protected About create(final int index) {
        return new About();
    }

    /**
     * Display all widgets to be tested.
     */
    @Test
    @Override
    @SuppressWarnings("unchecked")
    public void display() throws Exception {
        super.display();
        show(
            // General purpose widgets
            DisjointListsTest.class,
            ZoomPaneTest.class,
            Plot2DTest.class,
            null,

            // Image-related widgets
            ColorRampTest.class,
            PaletteComboBoxTest.class,
            MultiColorChooserTest.class,
            IIOMetadataPanelTest.class,
            ImagePaneTest.class,
            ImageFileChooserTest.class,
            ImageFilePropertiesTest.class,
            MosaicChooserTest.class,
            KernelEditorTest.class,
            GradientKernelEditorTest.class,
            OperationTreeBrowserTest.class,
            null,

            // Coverage-related widgets
            SampleDimensionPanelTest.class,
            null,

            // CRS-related widgets
            AuthorityCodesComboBoxTest.class,
            CoordinateChooserTest.class
        );
    }

    /**
     * Shows the widget provided by all the above test cases.
     */
    @SafeVarargs
    private static void show(final Class<? extends SwingTestBase<?>>... tests) throws ReflectiveOperationException {
        for (final Class<? extends SwingTestBase<?>> type : tests) {
            show(type != null ? type.newInstance() : null);
        }
    }
}
