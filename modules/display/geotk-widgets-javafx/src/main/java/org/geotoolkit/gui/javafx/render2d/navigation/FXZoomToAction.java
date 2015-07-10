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

package org.geotoolkit.gui.javafx.render2d.navigation;

import java.util.Optional;
import java.util.logging.Level;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.crs.FXCRSButton;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapAction;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FXZoomToAction extends FXMapAction {
    public static final Image ICON = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_THUMB_TACK, 16, FontAwesomeIcons.DEFAULT_COLOR), null);
    
    public FXZoomToAction(FXMap map) {
        super(map,GeotkFX.getString(FXZoomToAction.class,"zoom_at"),GeotkFX.getString(FXZoomToAction.class,"zoom_at"),ICON);
    }
    
    @Override
    public void accept(ActionEvent event) {
        if (map != null) {
            
            final Alert alert = new Alert(Alert.AlertType.NONE);
            
            final FXCRSButton crsButton = new FXCRSButton();
            crsButton.crsProperty().set(CommonCRS.WGS84.normalizedGeographic());
            
            final GridPane grid = new GridPane();
            grid.setMaxHeight(Double.MAX_VALUE);
            grid.setMaxWidth(Double.MAX_VALUE);
            grid.getColumnConstraints().add(new ColumnConstraints());
            grid.getColumnConstraints().add(new ColumnConstraints());
            grid.getRowConstraints().add(new RowConstraints());
            grid.getRowConstraints().add(new RowConstraints());
            final ColumnConstraints rc = new ColumnConstraints();
            grid.setHgap(10);
            grid.setVgap(10);
            
            final Label lblx = new Label();
            final Label lbly = new Label();
            final Label lcrs = new Label(GeotkFX.getString(this, "crs"));
            fillLabel(lblx, crsButton.crsProperty().get().getCoordinateSystem().getAxis(0));
            fillLabel(lbly, crsButton.crsProperty().get().getCoordinateSystem().getAxis(1));
            
            crsButton.crsProperty().addListener(new ChangeListener<CoordinateReferenceSystem>() {
                @Override
                public void changed(ObservableValue<? extends CoordinateReferenceSystem> observable, CoordinateReferenceSystem oldValue, CoordinateReferenceSystem newValue) {
                    fillLabel(lblx, newValue.getCoordinateSystem().getAxis(0));
                    fillLabel(lbly, newValue.getCoordinateSystem().getAxis(1));
                }
            });
            
            final TextField fieldx = new TextField("0");
            final TextField fieldy = new TextField("0");
                        
            grid.add(lblx, 0, 0);
            grid.add(fieldx, 1, 0);
            grid.add(lbly, 0, 1);
            grid.add(fieldy, 1, 1);
            grid.add(lcrs, 0, 2);
            grid.add(crsButton, 1, 2);
            
            final DialogPane pane = new DialogPane();
            pane.setContent(grid);
            pane.setMaxHeight(Double.MAX_VALUE);
            pane.setMaxWidth(Double.MAX_VALUE);
            pane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
            alert.setDialogPane(pane);
            alert.setTitle(GeotkFX.getString(FXZoomToAction.class,"zoom_at"));
            
            final Optional<ButtonType> res = alert.showAndWait();
            if(ButtonType.OK.equals(res.get())){
                try {
                    final CoordinateReferenceSystem navCRS = crsButton.crsProperty().get();
                    final GeneralDirectPosition pos = new GeneralDirectPosition(navCRS);
                    pos.setOrdinate(0, Double.valueOf(fieldx.getText()));
                    pos.setOrdinate(1, Double.valueOf(fieldy.getText()));
                    map.getCanvas().setObjectiveCenter(pos);
                } catch (Exception ex) {
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            
        }
    }

    private static void fillLabel(Label label, CoordinateSystemAxis axis){
        final String abr = axis.getAbbreviation();
        final Identifier name = axis.getName();
        label.setText(name.getCode()+" ("+abr+")");

        final InternationalString remarks = axis.getRemarks();
        if(remarks!=null){
            label.setTooltip(new Tooltip(remarks.toString()));
        }else{
            label.setTooltip(null);
        }
    }

}
