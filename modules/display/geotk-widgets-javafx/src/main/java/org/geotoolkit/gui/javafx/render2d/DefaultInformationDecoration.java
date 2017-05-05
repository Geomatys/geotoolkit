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
package org.geotoolkit.gui.javafx.render2d;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javax.swing.Timer;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;

/**
 * Default information decoration
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultInformationDecoration extends BorderPane implements FXInformationDecoration {

    private static final Image ICO_ERROR = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_EXCLAMATION_TRIANGLE, 16, Color.RED),null);
    private static final Image ICO_WARNING = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_EXCLAMATION_TRIANGLE, 16, Color.YELLOW),null);
    private static final Image ICO_INFO = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_EXCLAMATION_TRIANGLE, 16, Color.BLUE),null);

    private FXMap map = null;

    private final VBox messagesPanel = new VBox();
    private final ProgressIndicator guiPainting = new ProgressIndicator();
    private boolean lowlevel = true;

    public DefaultInformationDecoration() {
        setBackground(new Background(new BackgroundFill(null,null,null)));
        messagesPanel.setBackground(new Background(new BackgroundFill(new javafx.scene.paint.Color(0, 0, 0, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        guiPainting.setVisible(true);
        guiPainting.setMaxWidth(60);
        guiPainting.setMaxHeight(60);
        guiPainting.setBackground(new Background(new BackgroundFill(new javafx.scene.paint.Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        setCenter(guiPainting);
        setBottom(messagesPanel);
    }

    @Override
    public void setPaintingIconVisible(final boolean b) {
        guiPainting.setVisible(b);
        guiPainting.setProgress(-1);
    }

    @Override
    public void refresh() {
    }

    @Override
    public Node getComponent() {
        return this;
    }

    @Override
    public void setMap2D(final FXMap map) {
        this.map = map;
    }

    @Override
    public FXMap getMap2D() {
        return map;
    }

    @Override
    public boolean isPaintingIconVisible() {
        return guiPainting.isVisible();
    }

    @Override
    public void displayMessage(final String text, final int time, final FXInformationDecoration.LEVEL level) {

        if(!lowlevel){
            if (level != FXInformationDecoration.LEVEL.ERROR && level != FXInformationDecoration.LEVEL.WARNING) {
                return;
            }
        }

        final Label label = new Label();
        label.setTextFill(javafx.scene.paint.Color.WHITE);
        switch(level){
            case NORMAL :   label.setGraphic(new ImageView(ICO_INFO)); break;
            case INFO :     label.setGraphic(new ImageView(ICO_INFO)); break;
            case WARNING :  label.setGraphic(new ImageView(ICO_WARNING)); break;
            case ERROR :    label.setGraphic(new ImageView(ICO_ERROR)); break;
        }
        label.setText(text);
        messagesPanel.getChildren().add(label);

        final Timer tim = new Timer(time, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messagesPanel.getChildren().remove(label);
            }
        });
        tim.setRepeats(false);
        tim.start();
    }

    @Override
    public void displayLowLevelMessages(final boolean display) {
        lowlevel = display;
    }

    @Override
    public boolean isDisplayingLowLevelMessages() {
        return lowlevel;
    }

    @Override
    public void dispose() {
    }

}
