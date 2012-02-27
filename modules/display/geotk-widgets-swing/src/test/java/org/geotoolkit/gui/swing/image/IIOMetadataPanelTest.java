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
package org.geotoolkit.gui.swing.image;

import org.geotoolkit.test.gui.SwingTestBase;


/**
 * Tests the {@link IIOMetadataPanel}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 */
public final strictfp class IIOMetadataPanelTest extends SwingTestBase<IIOMetadataPanel> {
    /**
     * Constructs the test case.
     */
    public IIOMetadataPanelTest() {
        super(IIOMetadataPanel.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected IIOMetadataPanel create(final int index) {
        final IIOMetadataPanel test = new IIOMetadataPanel();
        test.addDefaultMetadataFormats();
        return test;
    }
}
