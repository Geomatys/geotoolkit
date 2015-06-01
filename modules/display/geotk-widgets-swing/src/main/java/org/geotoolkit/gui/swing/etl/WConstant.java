/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.gui.swing.etl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.propertyedit.JAttributeEditor;
import org.geotoolkit.processing.chain.ConstantUtilities;
import org.geotoolkit.processing.chain.model.ChainDataTypes;
import org.geotoolkit.processing.chain.model.Constant;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.InplaceEditorProvider;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.PropertyDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WConstant extends Widget implements WPositionable {

    private final WOfferingParameter off;

    public WConstant(final ChainScene scene, final Constant cst, final boolean editable) {
        super(scene);

        final InplaceEditorProvider t = new ConstantInplaceEditorProvider();

        getActions().addAction(ActionFactory.createMoveAction());
        getActions().addAction(ActionFactory.createPopupMenuAction(new ProcessPopup()));
        getActions().addAction(ActionFactory.createInplaceEditorAction(t));

        setBorder(BorderFactory.createRoundedBorder(20, 20, 8, 8, Color.WHITE, Color.DARK_GRAY));
        setLayout(LayoutFactory.createHorizontalFlowLayout());

        final LabelWidget place = new LabelWidget(scene,"    ");
        off = new WOfferingParameter(scene, cst, editable);
        addChild(place);
        addChild(off);
        revalidate(true);
    }

    @Override
    public Constant getObject(){
        return off.getConstant();
    }

    public WOfferingParameter getOffering() {
        return off;
    }

    private class ProcessPopup implements PopupMenuProvider{

        @Override
        public JPopupMenu getPopupMenu(Widget widget, Point point) {
            final JPopupMenu menu = new JPopupMenu();
            menu.add(new JMenuItem(
                 new AbstractAction("Delete") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ((ChainScene)getScene()).getChain().getConstants().remove(getObject());
                        }
                    }
                ));
            return menu;
        }

    }

    private final class ConstantInplaceEditorProvider implements InplaceEditorProvider{

        private final JPanel guiPane = new JPanel(new BorderLayout());
        private final JComboBox guiType = new JComboBox();
        private final JAttributeEditor guiEdit = new JAttributeEditor();
        private final JButton guiValidate = new JButton("ok");
        private ActionListener actionListener = null;
        private WConstant wc;

        public ConstantInplaceEditorProvider() {
            guiType.setModel(new ListComboBoxModel(ChainDataTypes.VALID_TYPES));
            guiType.setRenderer(new JClassCellRenderer());

            guiPane.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(210, 120, 120), 2));
            guiPane.add(BorderLayout.WEST,guiType);
            guiPane.add(BorderLayout.CENTER,guiEdit);
            guiPane.add(BorderLayout.EAST,guiValidate);

            guiType.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateEditor();
                }
            });

        }

        private void updateEditor(){
            if(wc !=null){
                final String value = wc.getObject().getValue();
                final Class type = (Class)guiType.getSelectedItem();

                final AttributeTypeBuilder atb = new AttributeTypeBuilder();
                atb.setName("");
                atb.setBinding(type);
                final AttributeType at = atb.buildType();
                final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
                adb.setType(at);
                adb.setName("");
                adb.setMinOccurs(1);
                adb.setMaxOccurs(1);
                final PropertyDescriptor adesc = adb.buildDescriptor();

                final Property property = FeatureUtilities.defaultProperty(adesc);
                if(value!=null && !value.isEmpty()){
                    try{
                        property.setValue(ConstantUtilities.stringToValue(value, type));
                    }catch(Exception ex){}
                }

                guiEdit.setProperty(property);
                guiEdit.setMinimumSize(new Dimension(100, 20));
                guiEdit.setPreferredSize(new Dimension(100, 20));
            }
        }

        @Override
        public void notifyOpened(final EditorController controller, final Widget widget, final JComponent c) {
            wc = (WConstant)widget;
            final Class type = wc.getObject().getType();

            guiType.setMinimumSize(new Dimension(100, 20));
            guiType.setSelectedItem(type);
            updateEditor();

            actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.closeEditor (true);
                }
            };
            guiValidate.addActionListener(actionListener);
        }

        @Override
        public void notifyClosing(EditorController ec, Widget widget, JComponent c, boolean bln) {
            wc = (WConstant)widget;
            final Constant cst = wc.getObject();

            final Object type = guiType.getSelectedItem();
            if(type instanceof Class){
                cst.setType((Class)type);
            }
            if(guiEdit.getProperty() != null){
                final String strValue = ConstantUtilities.valueToString(guiEdit.getProperty().getValue());
                cst.setValue(strValue);
            }

            String str = cst.getValue();
            if(str!=null && str.length()>20){
                str = str.substring(0, 20);
            }

            wc.getOffering().setLabel(JClassCellRenderer.getShortSymbol(cst.getType()) +" : "+ str);

            guiValidate.removeActionListener(actionListener);

            if (widget != null)
                widget.getScene ().validate ();
        }

        @Override
        public JComponent createEditorComponent(EditorController ec, Widget widget) {
            return guiPane;
        }

        @Override
        public Rectangle getInitialEditorComponentBounds(EditorController ec, Widget widget, JComponent c, Rectangle rctngl) {
            return null;
        }

        @Override
        public EnumSet<ExpansionDirection> getExpansionDirections(EditorController ec, Widget widget, JComponent c) {
            return EnumSet.of(ExpansionDirection.RIGHT, ExpansionDirection.BOTTOM);
        }

    }

    @Override
    protected void paintWidget() {
        super.paintWidget();
        off.getConstant().setX(getLocation().x);
        off.getConstant().setY(getLocation().y);
    }

}
