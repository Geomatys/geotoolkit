/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.wizard;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.api.wizard.WizardDisplayer;

import org.geotoolkit.gui.swing.image.MosaicChooser;


/**
 * Guides the user through the steps of creating a set of {@linkplain Tile tiles}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 *
 * @todo Needs localization.
 */
public final class MosaicWizard {
    /**
     * The page for tile selection.
     */
    @SuppressWarnings("serial")
    public static final class Select extends WizardPage {
        /**
         * Creates the "Select source tiles" page.
         */
        public Select() {
            super("Select", getDescription(), false);
            add(new MosaicChooser());
        }

        /**
         * Returns a localized description of that page.
         *
         * @return The localized description.
         */
        public static String getDescription() {
            return "Select source tiles";
        }
    }

    /**
     * Displays this wizard.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Wizard wiz = WizardPage.createWizard(new Class[] {
            Select.class
        });
        WizardDisplayer.showWizard(wiz);
        System.exit(0);
    }
}
