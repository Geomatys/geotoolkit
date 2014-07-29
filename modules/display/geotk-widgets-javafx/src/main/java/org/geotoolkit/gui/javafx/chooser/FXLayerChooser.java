/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.gui.javafx.chooser;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.internal.GeotkFXIconBundle;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.RandomStyleBuilder;
import org.geotoolkit.style.StyleConstants;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLayerChooser extends BorderPane{

    
    private static final Comparator SORTER = new Comparator() {

        @Override
        public int compare(Object o1, Object o2) {
            final String str1;
            final String str2;

            if(o1 instanceof FeatureType){
                str1 = ((FeatureType)o1).getName().getLocalPart();
            }else if(o1 instanceof Name){
                str1 = ((Name)o1).getLocalPart();
            }else{
                str1 = o1.toString();
            }

            if(o2 instanceof FeatureType){
                str2 = ((FeatureType)o2).getName().getLocalPart();
            }else if(o2 instanceof Name){
                str2 = ((Name)o2).getLocalPart();
            }else{
                str2 = o2.toString();
            }

            return str1.compareToIgnoreCase(str2);
        }
    };

    private Object source = null;
    
    
    private final ListView<Object>layerNames = new ListView<>();
    private final ScrollPane scroll = new ScrollPane(layerNames);
    
    public FXLayerChooser() {        
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        setCenter(scroll);
        
        layerNames.setCellFactory((ListView<Object> param) -> new LayerCell());
        
    }
    
    
    public List<MapLayer> getLayers() throws DataStoreException{

        final MutableStyleFactory styleFactory = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                            new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

        
        final List values = layerNames.getSelectionModel().getSelectedItems();
        final List<MapLayer> layers = new ArrayList<>();

        if(values != null){
            for(Object value : values){
                final Name name;
                if(value instanceof FeatureType){
                    name = ((FeatureType) value).getName();
                }else{
                    name = (Name) value;
                }

                if(source instanceof FeatureStore){
                    final FeatureStore store = (FeatureStore) source;
                    final Session session = store.createSession(true);
                    final FeatureCollection collection = session.getFeatureCollection(QueryBuilder.all(name));
                    final MutableStyle style = RandomStyleBuilder.createRandomVectorStyle(collection.getFeatureType());
                    final FeatureMapLayer layer = MapBuilder.createFeatureLayer(collection, style);
                    layer.setName(name.getLocalPart());
                    layer.setDescription(styleFactory.description(name.getLocalPart(), name.toString()));
                    layers.add(layer);

                }else if(source instanceof CoverageStore){
                    final CoverageStore store = (CoverageStore) source;
                    final CoverageReference ref = store.getCoverageReference(name);
                    final MutableStyle style = styleFactory.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
                    final CoverageMapLayer layer = MapBuilder.createCoverageLayer(ref, style);
                    layer.setName(name.getLocalPart());
                    layer.setDescription(styleFactory.description(name.getLocalPart(), name.toString()));
                    layers.add(layer);
                }
            }
        }

        return layers;
    }

    public void setSource(Object source) throws DataStoreException {
        this.source = source;

        final List firstCandidates = new ArrayList<>();
        final List secondCandidates = new ArrayList<>();

        if(source instanceof FeatureStore){
            final FeatureStore store = (FeatureStore) source;
            for(Name name : store.getNames()){
                final FeatureType ft = store.getFeatureType(name);
                if(ft.getGeometryDescriptor() != null){
                    firstCandidates.add(ft);
                }else{
                    secondCandidates.add(ft);
                }
            }
        }

        if(source instanceof CoverageStore){
            final CoverageStore store = (CoverageStore) source;
            firstCandidates.addAll(store.getNames());
        }


        Collections.sort(firstCandidates, SORTER);

        if(!secondCandidates.isEmpty()){
            Collections.sort(secondCandidates, SORTER);
            firstCandidates.addAll(secondCandidates);
        }

        layerNames.setItems(FXCollections.observableArrayList(firstCandidates));
    }

    public Object getSource() {
        return source;
    }
    
    private final class LayerCell extends ListCell{

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            Object value = item;

            if(value instanceof FeatureType){
                final FeatureType ft = (FeatureType) value;
                final FeatureStore store = (FeatureStore) getSource();

                final GeometryDescriptor desc = ft.getGeometryDescriptor();
                if(desc != null){
                    BufferedImage icon;
                    final Class binding = desc.getType().getBinding();
                    try{
                        if(Point.class.isAssignableFrom(binding)){
                            icon = GeotkFXIconBundle.getBufferedImage("edit_single_point");
                        }else if(MultiPoint.class.isAssignableFrom(binding)){
                            icon = GeotkFXIconBundle.getBufferedImage("edit_multi_point");
                        }else if(LineString.class.isAssignableFrom(binding)){
                            icon = GeotkFXIconBundle.getBufferedImage("edit_single_line");
                        }else if(MultiLineString.class.isAssignableFrom(binding)){
                            icon = GeotkFXIconBundle.getBufferedImage("edit_multi_line");
                        }else if(Polygon.class.isAssignableFrom(binding)){
                            icon = GeotkFXIconBundle.getBufferedImage("edit_single_polygon");
                        }else if(MultiPolygon.class.isAssignableFrom(binding)){
                            icon = GeotkFXIconBundle.getBufferedImage("edit_multi_polygon");
                        }else{
                            icon = (BufferedImage) GeotkFXIconBundle.EMPTY_ICON_16;
                        }
                        
                         boolean editable = false;
                        try {
                            if(store.isWritable(ft.getName())){
                                editable = true;
                            }
                        } catch (DataStoreException ex) {}

                        if(!editable){
                            final BufferedImage img = new BufferedImage(
                                                            icon.getWidth(),
                                                            icon.getHeight(),
                                                            BufferedImage.TYPE_INT_ARGB);
                            final Graphics2D g = img.createGraphics();
                            g.drawImage(icon, 0, 0, null);
                            final BufferedImage lock = GeotkFXIconBundle.getBufferedImage("lock");
                            g.drawImage(lock, 0, 0, null);
                            icon = img;
                        }

                        setGraphic(new ImageView(SwingFXUtils.toFXImage(icon, null)));
                        
                    }catch(IOException ex){
                        Loggers.JAVAFX.log(Level.WARNING,ex.getMessage(),ex);
                    }
                }

                value = ft.getName();
            }

            if(value instanceof Name){
                final Name name = (Name) value;
                setText(name.getLocalPart());
                setTooltip(new Tooltip(DefaultName.toJCRExtendedForm(name)));
            }

        }
    
    }
    
}
