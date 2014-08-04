/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.gui.javafx.style.FXSpecialExpressionButton;
import org.geotoolkit.map.MapLayer;
import org.opengis.filter.expression.Expression;

/**
 * TODO
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FXCQLEditor extends BorderPane {

    public static Expression showDialog(FXSpecialExpressionButton aThis, MapLayer layer, SimpleObjectProperty<Expression> exp) throws CQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
