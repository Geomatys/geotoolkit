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

package org.geotoolkit.gui.javafx.parameter;

import java.util.Collection;
import java.util.Optional;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXParameterEditor extends BorderPane {

    private final TreeTableView treetable = new TreeTableView();
    private ParameterValueGroup parameter;

    public FXParameterEditor() {

        //this will cause the column width to fit the view area
        treetable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treetable.getStylesheets().add("org/geotoolkit/gui/javafx/parameter/parameters.css");

        final ScrollPane scroll = new ScrollPane(treetable);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        setCenter(scroll);

        treetable.getColumns().add(new ParamEnableColumn());
        treetable.getColumns().add(new ParamNameColumn());
        treetable.getColumns().add(new ParamValueColumn());

        setPrefSize(300, 300);
        setMinSize(250, 250);
    }

    public TreeTableView getTreetable() {
        return treetable;
    }

    public void setParameter(ParameterValueGroup parameter) {
        this.parameter = parameter;
        treetable.setRoot(toTree(null, parameter.getDescriptor(), parameter));
        treetable.setShowRoot(false);
    }

    public ParameterValueGroup getParameter() {
        return parameter;
    }

    private TreeItem toTree(ParameterValueGroup parent, GeneralParameterDescriptor desc, GeneralParameterValue parameter){

        final TreeItem<ParamEntry> root =
            new TreeItem<>(new ParamEntry(parent,desc,parameter));

        if(parameter!=null && desc instanceof ParameterDescriptorGroup){
            final ParameterDescriptorGroup descGroup = (ParameterDescriptorGroup) desc;
            final ParameterValueGroup group = (ParameterValueGroup) parameter;

            for(GeneralParameterDescriptor childDesc : descGroup.descriptors()){
                //TODO , handle multiplicity
                if(childDesc.getMaximumOccurs()>1) continue;
                GeneralParameterValue childVal = ParametersExt.getParameter(group, childDesc.getName().getCode());
                if(childVal==null && childDesc.getMinimumOccurs()>0){
                    childVal = ParametersExt.getOrCreateParameter(group, childDesc.getName().getCode());
                }

                final TreeItem item = toTree(group, childDesc, childVal);
                root.getChildren().add(item);
            }
        }

        return root;
    }

    private static class ParamEntry {

        public ParameterValueGroup parent;
        public GeneralParameterDescriptor desc;
        public final SimpleObjectProperty<GeneralParameterValue> value;

        public ParamEntry(ParameterValueGroup parent, GeneralParameterDescriptor desc, GeneralParameterValue value) {
            this.parent = parent;
            this.desc = desc;
            this.value = new SimpleObjectProperty<>(value);
        }

    }



    public class ParamNameColumn extends TreeTableColumn<ParamEntry,ParamEntry>{

        public ParamNameColumn() {
            super("Property");
            setCellValueFactory((CellDataFeatures<ParamEntry, ParamEntry> param) -> new SimpleObjectProperty<>(param.getValue().getValue()));
            setCellFactory((TreeTableColumn<ParamEntry, ParamEntry> param) -> new ParamNameCell());
            setEditable(false);
            setPrefWidth(200);
            setMinWidth(120);
        }

    }

    public class ParamValueColumn extends TreeTableColumn<ParamEntry,GeneralParameterValue>{
        public ParamValueColumn() {
            super("Value");
            setCellValueFactory(new Callback<CellDataFeatures<ParamEntry, GeneralParameterValue>, ObservableValue<GeneralParameterValue>>() {

                @Override
                public ObservableValue<GeneralParameterValue> call(CellDataFeatures<ParamEntry, GeneralParameterValue> param) {
                    return param.getValue().getValue().value;
                }
            });
            setCellFactory((TreeTableColumn<ParamEntry, GeneralParameterValue> param) -> new ParamValueCell());
            setEditable(true);
            setPrefWidth(200);
            setMinWidth(120);
        }
    }

    public class ParamEnableColumn extends TreeTableColumn<ParamEntry,GeneralParameterValue>{
        public ParamEnableColumn() {
            super("");
            setCellValueFactory(new Callback<CellDataFeatures<ParamEntry, GeneralParameterValue>, ObservableValue<GeneralParameterValue>>() {

                @Override
                public ObservableValue<GeneralParameterValue> call(CellDataFeatures<ParamEntry, GeneralParameterValue> param) {
                    return param.getValue().getValue().value;
                }
            });
            setCellFactory((TreeTableColumn<ParamEntry, GeneralParameterValue> param) -> new ParamEnableCell());
            setEditable(true);
            setPrefWidth(30);
            setMinWidth(30);
            setMaxWidth(30);
        }
    }

    private class ParamNameCell extends TreeTableCell<ParamEntry, ParamEntry>{

        @Override
        protected void updateItem(ParamEntry item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            setTooltip(null);
            setAlignment(Pos.CENTER_LEFT);

            if(!empty && item !=null){
                final GeneralParameterDescriptor desc = item.desc;

                //get name from alias
                String name = null;
                final Collection<GenericName> aliases = desc.getAlias();
                if(!aliases.isEmpty()){
                    final GenericName alias = aliases.iterator().next();
                    name = alias.toInternationalString().toString();
                }

                //use code if not defined
                if(name == null){
                    name = desc.getName().getCode();
                }
                setText(name);

                final InternationalString remarks = desc.getRemarks();
                if(remarks!=null){
                    setTooltip(new Tooltip(remarks.toString()));
                }
            }
        }
    }

    private class ParamValueCell extends TreeTableCell<ParamEntry, GeneralParameterValue>{

        public ParamValueCell() {
            setAlignment(Pos.CENTER_LEFT);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        @Override
        protected void updateItem(GeneralParameterValue item, boolean empty) {
            super.updateItem(item, empty);
            final ParamEntry entry = getTreeTableRow().getItem();

            setText(null);
            setGraphic(null);
            if (!empty && entry != null && entry.value.getValue() != null && entry.desc instanceof ParameterDescriptor) {
                Optional<FXValueEditor> opt = FXValueEditorSpi.findEditor((ParameterDescriptor) entry.desc);
                if (opt.isPresent()) {
                    final FXValueEditor valEditor = opt.get();
                    final ParameterValue pval = (ParameterValue) entry.value.getValue();
                    if(pval.getValue()!=null) valEditor.valueProperty().setValue(pval.getValue());
                    valEditor.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                        pval.setValue(newValue);
                    });

                    setGraphic(valEditor.getComponent());
                }
            }
        }
    }

    private class ParamEnableCell extends TreeTableCell<ParamEntry, GeneralParameterValue>{

        private final RadioButton cb = new RadioButton();

        public ParamEnableCell() {
            setAlignment(Pos.CENTER);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            cb.setSelected(false);
            cb.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    final ParamEntry entry = getTreeTableRow().getItem();
                    if(entry!=null){
                        if(cb.isSelected()){
                            if(entry.value.getValue()==null){
                                final ParameterValue val = ParametersExt.getOrCreateValue(entry.parent,entry.desc.getName().getCode());
                                entry.value.setValue(val);
                            }
                        }else{
                            if(entry.value.getValue()!=null){
                                entry.parent.values().remove(entry.value.getValue());
                                entry.value.setValue(null);
                            }
                        }
                    }
                }
            });
        }

        @Override
        protected void updateItem(GeneralParameterValue item, boolean empty) {
            super.updateItem(item, empty);
            final ParamEntry entry = getTreeTableRow().getItem();

            setText(null);
            setGraphic(null);
            if(!empty && entry!=null && entry.value != null){
                final int minOcc = entry.desc.getMinimumOccurs();
                final int maxOcc = entry.desc.getMaximumOccurs();

                if(minOcc==0 && maxOcc==1){
                    setGraphic(cb);
                    cb.setSelected(entry.value.getValue()!=null);
                }
            }
        }
    }


}
