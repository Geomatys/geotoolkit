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

import java.awt.Dimension;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.api.wizard.WizardDisplayer;

import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.gui.swing.image.MosaicChooser;
import org.geotoolkit.gui.swing.image.MosaicBuilderEditor;


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
     * The default size for content panes.
     */
    private static final Dimension SIZE = new Dimension(800, 400);

    /**
     * The page for tile selection.
     */
    @SuppressWarnings("serial")
    public static final class Select extends WizardPage {
        /**
         * The content of this wizard page.
         */
        private final MosaicChooser content;

        /**
         * Creates the "Select source tiles" page.
         */
        public Select() {
            super("Select", getDescription(), false);
            content = new MosaicChooser();
            content.setPreferredSize(SIZE);
            add(content);
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
     * The page for configuring the tile layout.
     */
    @SuppressWarnings("serial")
    public static final class Layout extends WizardPage {
        /**
         * The content of this wizard page.
         */
        private final MosaicBuilderEditor content;

        /**
         * Creates the "Set target tiles layout" page.
         */
        public Layout() {
            super("Select", getDescription(), false);
            content = new MosaicBuilderEditor();
            content.setPreferredSize(SIZE);
            add(content);
        }

        /**
         * Returns a localized description of that page.
         *
         * @return The localized description.
         */
        public static String getDescription() {
            return "Define pyramid tiling";
        }
    }

    /**
     * Displays this wizard.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Registry.setDefaultCodecPreferences();
        Wizard wiz = WizardPage.createWizard(new Class[] {
            Select.class,
            Layout.class
        });
        WizardDisplayer.showWizard(wiz);
        System.exit(0);
    }
}
