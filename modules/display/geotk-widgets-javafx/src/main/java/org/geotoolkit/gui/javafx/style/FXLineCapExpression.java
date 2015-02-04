/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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

import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javax.imageio.ImageIO;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLineCapExpression extends FXExpression {

    private static final Image IMG_ROUND;
    private static final Image IMG_SQUARE;
    private static final Image IMG_BUTT;
    static {
        try{
            //NOTE : for some unknown reason javafx fails to load images if we use new Image(url) in scenebuilder but not in a normal execution
            IMG_ROUND = SwingFXUtils.toFXImage(ImageIO.read(FXLineCapExpression.class.getResource("/org/geotoolkit/gui/javafx/icon/crystalproject/16x16/actions/cap_round.png")), null);
            IMG_SQUARE = SwingFXUtils.toFXImage(ImageIO.read(FXLineCapExpression.class.getResource("/org/geotoolkit/gui/javafx/icon/crystalproject/16x16/actions/cap_square.png")), null);
            IMG_BUTT = SwingFXUtils.toFXImage(ImageIO.read(FXLineCapExpression.class.getResource("/org/geotoolkit/gui/javafx/icon/crystalproject/16x16/actions/cap_butt.png")), null);
        }catch(IOException ex){
            throw new RuntimeException("Failed to load line cap icons.");
        }
    }

    private final ToggleGroup group = new ToggleGroup();
    private final ToggleButton uiRound = new ToggleButton(null, new ImageView(IMG_ROUND));
    private final ToggleButton uiSquare = new ToggleButton(null, new ImageView(IMG_SQUARE));
    private final ToggleButton uiButt = new ToggleButton(null, new ImageView(IMG_BUTT));
    private final HBox hbox = new HBox(uiRound,uiSquare,uiButt);
        
    public FXLineCapExpression(){
        uiRound.setToggleGroup(group);
        uiSquare.setToggleGroup(group);
        uiButt.setToggleGroup(group);
        
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue==uiButt){
                    value.set(StyleConstants.STROKE_CAP_BUTT);
                }else if(newValue==uiRound){
                    value.set(StyleConstants.STROKE_CAP_ROUND);
                }else if(newValue==uiSquare){
                    value.set(StyleConstants.STROKE_CAP_SQUARE);
                }
            }
        });
    }

    @Override
    public Expression newValue() {
        return StyleConstants.STROKE_CAP_BUTT;
    }

    @Override
    protected Node getEditor() {
        return hbox;
    }

    @Override
    protected boolean canHandle(Expression exp) {
        final Toggle selected = group.getSelectedToggle();
        if(StyleConstants.STROKE_CAP_BUTT.equals(exp)){
            if(selected!=uiButt) group.selectToggle(uiButt);
            return true;
        }else if(StyleConstants.STROKE_CAP_ROUND.equals(exp)){
            if(selected!=uiRound) group.selectToggle(uiRound);
            return true;
        }else if(StyleConstants.STROKE_CAP_SQUARE.equals(exp)){
            if(selected!=uiSquare) group.selectToggle(uiSquare);
            return true;
        }
        return false;
    }

}
