/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.gui.swing.referencing;

import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.util.FactoryException;

import org.geotoolkit.test.gui.SwingBase;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;


/**
 * Tests the {@link AuthorityCodesComboBox}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.02
 */
public final class AuthorityCodesComboBoxTest extends SwingBase<AuthorityCodesComboBox> {
    /**
     * Constructs the test case.
     */
    public AuthorityCodesComboBoxTest() {
        super(AuthorityCodesComboBox.class);
    }

    /**
     * Creates the widget.
     *
     * @throws FactoryException Should never happen.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected AuthorityCodesComboBox create() throws FactoryException {
        AuthorityCodesComboBox chooser;
        try {
            final CRSAuthorityFactory factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", null);
            chooser = new AuthorityCodesComboBox(factory, ProjectedCRS.class, GeographicCRS.class);
        } catch (FactoryNotFoundException e) {
            /*
             * This happen if the JDBC driver (typically Derby) required for accessing
             * the EPSG database is not on the classpath.
             */
            return null;
        }
        chooser.setSelectedCode("4326");
        return chooser;
    }
}
