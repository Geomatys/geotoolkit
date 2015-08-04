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
package org.geotoolkit.gui.javafx.style;

import java.awt.Color;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXColorExpression extends FXExpression {

    private final ColorPicker uiPicker = new ColorPicker();
    
    public FXColorExpression(){
        uiPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                value.set(getFilterFactory().literal(FXUtilities.toSwingColor(uiPicker.getValue())));
            }
        });
        uiPicker.setMaxWidth(Double.MAX_VALUE);
    }
    
    @Override
    public Expression newValue() {
        return StyleConstants.DEFAULT_FILL_COLOR;
    }

    @Override
    protected Node getEditor() {
        return uiPicker;
    }
    
    @Override
    protected boolean canHandle(Expression exp) {
        if(!(exp instanceof Literal)) return false;
        final Color color = exp.evaluate(null,Color.class);
        if(color!=null){
            uiPicker.setValue(FXUtilities.toFxColor(color));
            return true;
        }
        return false;
    }
    
}
