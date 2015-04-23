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

package org.geotoolkit.gui.javafx.layer.style;

import java.io.File;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.FileChooser;
import javax.xml.bind.JAXBException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.gui.javafx.layer.FXLayerStylePane;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.sld.MutableLayer;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.MutableStyle;
import org.opengis.sld.LayerStyle;
import org.opengis.sld.NamedLayer;
import org.opengis.sld.UserLayer;
import org.opengis.style.Style;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleXMLPane extends FXLayerStylePane {
    
    @FXML
    private ChoiceBox<Specification.StyledLayerDescriptor> uiVersion;

    private MapLayer layer = null;
    
    public FXStyleXMLPane() {
        GeotkFX.loadJRXML(this,FXStyleXMLPane.class);
    }
    
    @FXML
    void importXMl(ActionEvent event) {
        final Specification.StyledLayerDescriptor version = uiVersion.getValue();
        if(layer != null){
            final FileChooser chooser = new FileChooser();
            final File result = chooser.showOpenDialog(null);

            parse:
            if(result != null){
                final StyleXmlIO tool = new StyleXmlIO();
                try {
                    final MutableStyledLayerDescriptor sld = tool.readSLD(result, version);

                    if(sld != null ){
                        for(MutableLayer sldLayer : sld.layers()){
                            if(sldLayer instanceof NamedLayer){
                                final NamedLayer nl = (NamedLayer) sldLayer;
                                for(LayerStyle ls : nl.styles()){
                                    if(ls instanceof MutableStyle){
                                        layer.setStyle((MutableStyle) ls);
                                    }
                                }
                            }else if(sldLayer instanceof UserLayer){
                                final UserLayer ul = (UserLayer) sldLayer;
                                for(Style ls : ul.styles()){
                                    if(ls instanceof MutableStyle){
                                        layer.setStyle((MutableStyle) ls);
                                    }
                                }
                            }
                        }
                    }
                    break parse;
                } catch (JAXBException ex) {
                    Logging.getLogger(FXStyleXMLPane.class).log(Level.FINEST,ex.getMessage(),ex);
                } catch (FactoryException ex) {
                    Logging.getLogger(FXStyleXMLPane.class).log(Level.FINEST,ex.getMessage(),ex);
                }

                try {
                    final MutableStyle style = tool.readStyle(result,
                            (version==Specification.StyledLayerDescriptor.V_1_0_0) ?
                            Specification.SymbologyEncoding.SLD_1_0_0 :
                            Specification.SymbologyEncoding.V_1_1_0);

                    layer.setStyle(style);

                    break parse;
                } catch (JAXBException ex) {
                    Logging.getLogger(FXStyleXMLPane.class).log(Level.FINEST,ex.getMessage(),ex);
                } catch (FactoryException ex) {
                    Logging.getLogger(FXStyleXMLPane.class).log(Level.FINEST,ex.getMessage(),ex);
                }

            }
        }
    }

    @FXML
    void exportXML(ActionEvent event) {
        final Specification.StyledLayerDescriptor version = uiVersion.getValue();
        if(layer != null && layer.getStyle() != null){
            final MutableStyle style = layer.getStyle();
            final FileChooser chooser = new FileChooser();
            final File result = chooser.showSaveDialog(null);

            if(result != null){
                final StyleXmlIO tool = new StyleXmlIO();
                try {
                    tool.writeStyle(result, style, version);
                } catch (JAXBException ex) {
                    Logging.getLogger(FXStyleXMLPane.class).log(Level.WARNING,ex.getMessage(),ex);
                }
            }
        }
    }
    
    @Override
    public String getTitle() {
        return GeotkFX.getString(this,"title");
    }

    @Override
    public String getCategory() {
        return GeotkFX.getString(this,"category");
    }
    
    /**
     * Called by FXMLLoader after creating controller.
     */
    public void initialize(){
        uiVersion.setItems(FXCollections.observableArrayList(Specification.StyledLayerDescriptor.values()));
        if(!uiVersion.getItems().isEmpty()){
            uiVersion.getSelectionModel().select(uiVersion.getItems().size()-1);
        }
    }
    
    @Override
    public boolean init(MapLayer candidate, Object StyleElement) {
        if(!(candidate instanceof MapLayer)) return false;     
        layer = (MapLayer) candidate;
        return true;
    }
    
    @Override
    public MutableStyle getMutableStyle() {
        if(layer!=null) return layer.getStyle();
        return null;
    }
        
}
