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

package org.geotoolkit.gui.javafx.contexttree;

import java.util.function.Function;
import java.util.logging.Level;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.ImageView;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.gui.javafx.filter.FXCQLEditor;
import org.geotoolkit.gui.javafx.util.ButtonTreeTableCell;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapItem;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemFilterColumn extends TreeTableColumn<MapItem, MapItem>{

    public MapItemFilterColumn() {                
        setCellValueFactory(param -> ((CellDataFeatures)param).getValue().valueProperty());     
        setCellFactory((TreeTableColumn<MapItem, MapItem> p) -> new FilterButton());
        setEditable(true);
        setPrefWidth(26);
        setMinWidth(26);
        setMaxWidth(26);
    }
    
    private static class FilterButton extends ButtonTreeTableCell<MapItem, MapItem>{

        public FilterButton() {
            super(false, new ImageView(GeotkFX.ICON_FILTER), new Function<MapItem, Boolean>() {
                public Boolean apply(MapItem t) {
                    return t instanceof FeatureMapLayer;
                }
            },null);
            
        }

        @Override
        public MapItem actionPerformed(MapItem candidate) {
            if(candidate instanceof FeatureMapLayer){
                try{
                    final FeatureMapLayer layer = (FeatureMapLayer) candidate;
                    Filter filter = layer.getQuery().getFilter();
                    filter = FXCQLEditor.showFilterDialog(button, layer, filter);
                    
                    final QueryBuilder qb = new QueryBuilder(layer.getQuery());
                    qb.setFilter(filter);
                    layer.setQuery(qb.buildQuery());
                }catch(CQLException ex){
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            return candidate;
        }
        
    }
    
    
}
