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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.RandomStyleBuilder;
import org.geotoolkit.style.StyleConstants;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory2;
import org.geotoolkit.storage.coverage.CoverageResource;

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
                str1 = ((FeatureType)o1).getName().tip().toString();
            }else if(o1 instanceof GenericName){
                str1 = ((GenericName)o1).tip().toString();
            }else{
                str1 = o1.toString();
            }

            if(o2 instanceof FeatureType){
                str2 = ((FeatureType)o2).getName().tip().toString();
            }else if(o2 instanceof GenericName){
                str2 = ((GenericName)o2).tip().toString();
            }else{
                str2 = o2.toString();
            }

            return str1.compareToIgnoreCase(str2);
        }
    };

    private Object source = null;

    public final ListView<Object> layerNames = new ListView<>();
    private final ScrollPane scroll = new ScrollPane(layerNames);

    public FXLayerChooser() {
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        setCenter(scroll);

        layerNames.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        layerNames.setCellFactory((ListView<Object> param) -> new LayerCell());
    }

    public List<MapLayer> getLayers() throws DataStoreException{

        final MutableStyleFactory styleFactory = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                            new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

        final FilterFactory2 FF = new DefaultFilterFactory2();
        final MutableStyleFactory SF = styleFactory;

        final List values = layerNames.getSelectionModel().getSelectedItems();
        final List<MapLayer> layers = new ArrayList<>();

        if(values != null){
            for(Object value : values){
                final GenericName name;
                if(value instanceof FeatureType){
                    name = ((FeatureType) value).getName();
                }else{
                    name = (GenericName) value;
                }

                if(source instanceof FeatureStore){
                    final FeatureStore store = (FeatureStore) source;
                    final DataStoreFactory factory = store.getFactory();
                    final Session session = store.createSession(true);
                    final FeatureCollection collection = session.getFeatureCollection(QueryBuilder.all(name.toString()));

                    final MutableStyle style;

                    if(factory.getMetadata().produceStyledFeature()){
                        //do not create a style, each feature defines it's own symbolizers
                        style = SF.style();

                    }else{
                        style = RandomStyleBuilder.createRandomVectorStyle(collection.getType());
                    }

                    final FeatureMapLayer layer = MapBuilder.createFeatureLayer(collection, style);
                    layer.setName(name.tip().toString());
                    layer.setDescription(styleFactory.description(name.tip().toString(), name.toString()));
                    layer.setUserProperty(MapLayer.USERKEY_STYLED_FEATURE, factory.getMetadata().produceStyledFeature());
                    layers.add(layer);

                }else if(source instanceof CoverageStore){
                    final CoverageStore store = (CoverageStore) source;
                    final CoverageResource ref = store.findResource(name);
                    final MutableStyle style = styleFactory.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
                    final CoverageMapLayer layer = MapBuilder.createCoverageLayer(ref, style);
                    layer.setName(name.tip().toString());
                    layer.setDescription(styleFactory.description(name.tip().toString(), name.toString()));
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
            for(GenericName name : store.getNames()){
                final FeatureType ft = store.getFeatureType(name.toString());
                final AttributeType<?> geomAtt = FeatureExt.castOrUnwrap(FeatureExt.getDefaultGeometry(ft))
                        .orElse(null);
                if(geomAtt != null){
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

        final Runnable setList = () -> layerNames.setItems(FXCollections.observableArrayList(firstCandidates));
        if (Platform.isFxApplicationThread()) {
            setList.run();
        } else {
            Platform.runLater(setList);
        }
    }

    public Object getSource() {
        return source;
    }

    private final class LayerCell extends ListCell{

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            setText("");
            setTooltip(null);
            setGraphic(null);

            Object value = item;

            if(value instanceof FeatureType){
                final FeatureType ft = (FeatureType) value;
                final FeatureStore store = (FeatureStore) getSource();

                final AttributeType<?> desc = FeatureExt.castOrUnwrap(FeatureExt.getDefaultGeometry(ft))
                        .orElse(null);
                if(desc != null){
                    BufferedImage icon;
                    final Class binding = desc.getValueClass();
                    if(Point.class.isAssignableFrom(binding)){
                        icon = GeotkFX.getBufferedImage("edit_single_point");
                    }else if(MultiPoint.class.isAssignableFrom(binding)){
                        icon = GeotkFX.getBufferedImage("edit_multi_point");
                    }else if(LineString.class.isAssignableFrom(binding)){
                        icon = GeotkFX.getBufferedImage("edit_single_line");
                    }else if(MultiLineString.class.isAssignableFrom(binding)){
                        icon = GeotkFX.getBufferedImage("edit_multi_line");
                    }else if(Polygon.class.isAssignableFrom(binding)){
                        icon = GeotkFX.getBufferedImage("edit_single_polygon");
                    }else if(MultiPolygon.class.isAssignableFrom(binding)){
                        icon = GeotkFX.getBufferedImage("edit_multi_polygon");
                    }else{
                        icon = (BufferedImage) GeotkFX.EMPTY_ICON_16;
                    }

                     boolean editable = false;
                    try {
                        if(store.isWritable(ft.getName().toString())){
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
                        final BufferedImage lock = GeotkFX.getBufferedImage("lock");
                        g.drawImage(lock, 0, 0, null);
                        icon = img;
                    }

                    setGraphic(new ImageView(SwingFXUtils.toFXImage(icon, null)));

                }

                value = ft.getName();
            }

            if(value instanceof GenericName){
                final GenericName name = (GenericName) value;
                setText(name.tip().toString());
                setTooltip(new Tooltip(NamesExt.toExpandedString(name)));
            }

        }

    }

}
