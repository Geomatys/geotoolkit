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
package org.geotoolkit.internal.wizard;

import java.util.Map;
import java.util.prefs.Preferences;
import org.geotoolkit.resources.Errors;
import org.netbeans.spi.wizard.WizardPanelProvider;


/**
 * Base class of wizards provided in the {@link org.geotoolkit.internal.wizard} package.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.00
 * @module
 */
public abstract class AbstractWizard extends WizardPanelProvider {
    /**
     * Creates a new wizard.
     */
    AbstractWizard(final String title, final String[] steps, final String[] descriptions) {
        super(title, steps, descriptions);
    }

    /**
     * Adds the given (key,value) pair in the given map. It must be a new entry;
     * no value is allowed to exist for the given key prior this call.
     *
     * {@note The <code>Map</code> argument is not parameterized because current
     *        <code>org.netbeans.api.wizard</code> is designed for Java 4.}
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    static void addSetting(final Map settings, final String key, final Object value) {
        if (settings.put(key, value) != null) {
            throw new AssertionError(Errors.format(Errors.Keys.DuplicatedValuesForKey_1, key));
        }
    }

    /**
     * Returns the preferences node.
     */
    static Preferences preferences() {
        return Preferences.userRoot().node("/org/geotoolkit/gui/swing/wizard");
    }
}
