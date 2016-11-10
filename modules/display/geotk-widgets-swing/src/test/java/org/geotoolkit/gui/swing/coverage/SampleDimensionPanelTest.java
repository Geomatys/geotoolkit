/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.gui.swing.coverage;

import java.util.List;
import java.util.Arrays;
import java.text.ParseException;

import org.apache.sis.measure.Units;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.test.gui.SwingTestBase;

import static org.junit.Assert.*;


/**
 * Tests the {@link SampleDimensionPanel}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.13
 */
public final strictfp class SampleDimensionPanelTest extends SwingTestBase<SampleDimensionPanel> {
    /**
     * Constructs the test case.
     */
    public SampleDimensionPanelTest() {
        super(SampleDimensionPanel.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected SampleDimensionPanel create(final int index) {
        final GridSampleDimension band1 = new GridSampleDimension("Temperature",
            new Category[] {
                new Category("No data",     null, 0),
                new Category("Land",        null, 7),
                new Category("Clouds",      null, 3),
                new Category("Temperature", null, 10, 100, 0.1, 5),
                new Category("Foo",         null, 100, 120, -1, 3)
            }, Units.CELSIUS);
        final GridSampleDimension band2 = new GridSampleDimension("Sea Level Anomaly",
            new Category[] {
                new Category("No data",     null, 0),
                new Category("SLA",         null, 1, 255, 0.25, -32)
            }, Units.CENTIMETRE);
        final List<GridSampleDimension> bands = Arrays.asList(band1, band2);

        final SampleDimensionPanel panel = new SampleDimensionPanel();
        panel.setSampleDimensions(bands);
        try {
            assertEquals(bands, panel.getSampleDimensions());
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
        return panel;
    }
}
