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
package org.geotoolkit.gui.javafx.style;

import java.util.Objects;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.filter.FXCQLEditor;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class FXExpression extends FXStyleElementController<Expression> {

    private static final Image ICON_EDIT = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PENCIL, 16, FontAwesomeIcons.DEFAULT_COLOR),null);
    private static final Image ICON_ERASE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ERASER, 16, FontAwesomeIcons.DEFAULT_COLOR),null);

    private final TextField textfield = new TextField();
    private final Button guiEdit = new Button();
    private final Button guiErase = new Button();
    private final HBox hbox = new HBox(guiEdit, guiErase);

    public FXExpression() {
        super(false);
        setRight(hbox);
        setCenter(textfield);

        hbox.setBackground(Background.EMPTY);

        guiEdit.setBorder(Border.EMPTY);
        guiEdit.setBackground(Background.EMPTY);
        guiEdit.setGraphic(new ImageView(ICON_EDIT));

        guiErase.setBorder(Border.EMPTY);
        guiErase.setBackground(Background.EMPTY);
        guiErase.setGraphic(new ImageView(ICON_ERASE));

        textfield.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    value.set(CQL.parseExpression(textfield.getText()));
                }catch(CQLException ex){
                }
            }
        });

        guiEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                try{
                    final Expression ne = FXCQLEditor.showDialog(FXExpression.this, layer, value.get());
                    if(ne!=null && !Objects.equals(ne, value.get())){
                        value.set(ne);
                    }
                }catch(CQLException ex){
                    ex.printStackTrace();
                }
            }
        });

        guiErase.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                value.set(newValue());
            }
        });

        initialize();
    }

    @Override
    public Class<Expression> getEditedClass() {
        return Expression.class;
    }

    @Override
    protected final void updateEditor(Expression exp) {

        if(canHandle(exp)){
            guiErase.setVisible(false);
            Platform.runLater(() -> {setCenter(getEditor());});
        }else{
            guiErase.setVisible(true);
            textfield.setText(CQL.write(exp));
            Platform.runLater(() -> {setCenter(textfield);});
        }
    }

    protected abstract boolean canHandle(Expression exp);

    protected abstract Node getEditor();

}
