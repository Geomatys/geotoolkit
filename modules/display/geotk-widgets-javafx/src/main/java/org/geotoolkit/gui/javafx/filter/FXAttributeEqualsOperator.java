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

import com.vividsolutions.jts.geom.Geometry;
import javafx.scene.Node;
import org.geotoolkit.display2d.GO2Utilities;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FXAttributeEqualsOperator extends FXComparableOperator {

    @Override
    public CharSequence getTitle() {
        return "=";
    }

    @Override
    public Filter getFilterOver(Expression toApplyOn, Node filterEditor) {
        Object editorValue = getEditorValue(filterEditor);
        if (editorValue instanceof Geometry) {
            return GO2Utilities.FILTER_FACTORY.equal(toApplyOn, GO2Utilities.FILTER_FACTORY.literal(editorValue));
        }
        return GO2Utilities.FILTER_FACTORY.equals(
                toApplyOn, GO2Utilities.FILTER_FACTORY.literal(editorValue));
    }
    
}
