/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.filter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import javafx.scene.Node;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.geotoolkit.display2d.GO2Utilities;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Expression;

/**
 * Operator for {@link PropertyIsNull} filter.
 * 
 * @author Alexis Manin (Geomatys)
 */
public class FXNullOperator implements FXFilterOperator {

    // TODO : put traduction in bundle.
    private static final DefaultInternationalString TITLE;
    static {
        final HashMap<Locale, String> locales = new HashMap<>();
        locales.put(Locale.ENGLISH, "is null");
        locales.put(Locale.FRENCH, "est nul(le)");
        TITLE = new DefaultInternationalString(locales);
    }
    
    @Override
    public boolean canHandle(PropertyType target) {
        if (target instanceof AttributeType) {
            return !((AttributeType)target).getValueClass().isPrimitive();
        } else {
            return true;
        }
    }

    @Override
    public CharSequence getTitle() {
        return TITLE;
    }

    @Override
    public boolean canExtractSettings(PropertyType propertyType, Node settingsContainer) {
        return false;
    }
    
    @Override
    public Filter getFilterOver(Expression toApplyOn, Node editor) {
        return GO2Utilities.FILTER_FACTORY.isNull(toApplyOn);
    }

    @Override
    public Optional<Node> createFilterEditor(PropertyType target) {
        return Optional.empty();
    }
    
}
