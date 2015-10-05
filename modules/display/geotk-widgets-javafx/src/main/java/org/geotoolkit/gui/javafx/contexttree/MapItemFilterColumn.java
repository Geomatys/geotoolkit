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

package org.geotoolkit.gui.javafx.contexttree;

import java.util.logging.Level;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.gui.javafx.filter.FXCQLEditor;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemFilterColumn extends TreeTableColumn{

    public MapItemFilterColumn() {                
        setCellValueFactory(param -> ((CellDataFeatures)param).getValue().valueProperty());     
        setCellFactory((Object param) -> new FilterCell());
        setEditable(true);
        setPrefWidth(26);
        setMinWidth(26);
        setMaxWidth(26);
    }

    private final class FilterCell extends TreeTableCell{

        public FilterCell() {
            setFont(FXUtilities.FONTAWESOME);
            setOnMouseClicked(this::mouseClick);
            setTooltip(new Tooltip(GeotkFX.getString(MapItemFilterColumn.class, "tooltip")));
            setTextAlignment(TextAlignment.CENTER);
            setAlignment(Pos.CENTER);
        }

        private void mouseClick(MouseEvent event){
            event.consume();
            if(!isEditing()){
                getTreeTableView().edit(getTreeTableRow().getIndex(), getTableColumn());
            }
            Object candidate = getItem();
            if(candidate instanceof FeatureMapLayer){
                try{
                    final FeatureMapLayer layer = (FeatureMapLayer) candidate;
                    Filter filter = layer.getQuery().getFilter();
                    filter = FXCQLEditor.showFilterDialog(this, layer, filter);

                    if(filter!=null){
                        final QueryBuilder qb = new QueryBuilder(layer.getQuery());
                        qb.setFilter(filter);
                        layer.setQuery(qb.buildQuery());
                    }
                }catch(CQLException ex){
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if(!empty && item instanceof FeatureMapLayer){
                setText(FontAwesomeIcons.ICON_FILTER);
            }else{
                setText(null);
            }
        }

    }

}
