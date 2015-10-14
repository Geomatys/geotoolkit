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

import static org.geotoolkit.gui.swing.etl.ChainEditorConstants.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.processing.chain.model.DataLink;
import org.geotoolkit.processing.chain.model.ElementCondition;
import org.geotoolkit.processing.chain.model.FlowLink;
import org.geotoolkit.processing.chain.model.Parameter;
import org.geotoolkit.processing.chain.model.Positionable;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class WElementCondition extends Widget implements WPositionable {


    private WName w_title;
    private WName w_success;
    private WName w_failed;
    private final ElementCondition condition;
    private final Map<String, WReceivingParameter> inputsWidgets = new HashMap<String, WReceivingParameter>();
    private final Boolean editable;
    private final Widget w_center;
    private Color bgColor;

    public WElementCondition(final ElementCondition condition, final ChainScene scene, final boolean showParameters, final boolean editable) {
        super(scene);
        this.condition = condition;
        this.editable = editable;

        bgColor = CHAIN_CONDITIONAL_ELEMENT_COLOR;

        setOpaque(true);

        getActions().addAction(ActionFactory.createMoveAction());
        if (editable) {
            getActions().addAction(ActionFactory.createPopupMenuAction(new ProcessPopup()));
            getActions().addAction(ActionFactory.createEditAction(new ConditionEditor()));
        }

        setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY,2));
        setBorder(BorderFactory.createRoundedBorder(20, 20, 8, 8, Color.WHITE, Color.DARK_GRAY));

        Dimension leftExecDim = new Dimension(75, 18);
        Dimension rightExecDim = new Dimension(40, 18);

        //////////////// NORTH WIDGET
        final Widget w_north = new Widget(scene);
        w_north.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.CENTER,5));
        w_north.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));

        final String title = MessageBundle.format("conditionalTitle");
        w_title = new WName(scene, title, editable, false, true, this);
        w_title.setPreferredSize(leftExecDim);
        w_title.setAlignment(LabelWidget.Alignment.LEFT);
        w_title.setFont(CHAIN_ELEMENT_EXECUTION_TITLE_FONT);

        final Widget w_separator1 = new LabelWidget(scene, "");
        w_separator1.setBorder(BorderFactory.createLineBorder(0, 1, 0, 0, CHAIN_TITLE_UNDERLINE_COLOR));

        final String success = MessageBundle.format("conditionalSuccess");
        w_success = new WName(scene, success, editable, true, false, this);
        w_success.setPreferredSize(rightExecDim);
        w_success.setAlignment(LabelWidget.Alignment.RIGHT);
        w_success.setFont(CHAIN_ELEMENT_EXECUTION_TITLE_FONT);

        w_north.addChild(w_title);
        w_north.addChild(w_separator1);
        w_north.addChild(w_success);

        //////////////// CENTER WIDGET
        w_center = new Widget(scene);
        w_center.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP,4));
        w_center.setBorder(BorderFactory.createEmptyBorder(2,0,2,8));

        if (showParameters) {
            final List<Parameter> input = condition.getInputs();
            for (final Parameter param : input) {
                addInput(param);
            }
        }

        //////////////// SOUTH WIDGET
        final Widget w_south = new Widget(scene);
        w_south.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));
        w_south.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.CENTER,5));
        final String fail = MessageBundle.format("conditionalFailed");
        w_failed = new WName(scene, fail, editable, true, false, this);
        w_failed.setPreferredSize(rightExecDim);
        w_failed.setAlignment(LabelWidget.Alignment.RIGHT);
        w_failed.setFont(CHAIN_ELEMENT_EXECUTION_TITLE_FONT);

        final Widget w_separator2 = new LabelWidget(scene, "");
        w_separator2.setBorder(BorderFactory.createLineBorder(0, 1, 0, 0, CHAIN_TITLE_UNDERLINE_COLOR));
        Widget w_push = new Widget(scene);
        w_push.setPreferredSize(leftExecDim);
        w_south.addChild(w_push);
        w_south.addChild(w_separator2);
        w_south.addChild(w_failed);

        addChild(w_north);
        addChild(w_center);
        addChild(w_south);

        getScene().validate();

    }

    @Override
    public Positionable getObject() {
        return condition;
    }

    public Integer getId() {
        if (condition != null) {
            return condition.getId();
        }
        return -1;
    }

    public List<FlowLink> getSuccessLinks() {
        return condition.getSuccess();
    }

    public List<FlowLink> getFailedLinks() {
        return condition.getFailed();
    }

    public WName getWExecutionTitle () {
        return w_title;
    }

    public WName getWExecutionSuccess () {
        return w_success;
    }

    public WName getWExecutionFailed () {
        return w_failed;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Widget getInputWidget (final String parameterName) {
        return inputsWidgets.get(parameterName);
    }

    final void addInput(final Parameter param) {
        final WReceivingParameter widget = new WReceivingParameter((ChainScene)getScene(), param);
        w_center.addChild(0,widget);
        inputsWidgets.put(param.getCode(), widget);
        getScene().validate();
    }

    final void removeInput(final String paramCode) {

        Widget removedWidget = null;
        for (final Widget widget : w_center.getChildren()) {
            final WReceivingParameter wParam = (WReceivingParameter) widget;
            if(wParam.getCode().equals(paramCode)) {
                removedWidget = wParam;
                break;
            }
        }

        if (removedWidget != null) {
            w_center.removeChild(removedWidget);
            inputsWidgets.remove(paramCode);
        }
        getScene().validate();
    }

    @Override
    protected void paintWidget() {
        super.paintWidget();
        getObject().setX(getLocation().x);
        getObject().setY(getLocation().y);
    }

    private class ProcessPopup implements PopupMenuProvider{

        @Override
        public JPopupMenu getPopupMenu(final Widget widget, final Point point) {
            final JPopupMenu menu = new JPopupMenu();
            menu.add(new JMenuItem(
                 new AbstractAction("Configure") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final WElementCondition wcondition = (WElementCondition) widget;
                            final ElementCondition condition = (ElementCondition)wcondition.getObject();
                            showEditorDialog(condition);

                        }
                    }
                ));
            menu.add(new JMenuItem(
                 new AbstractAction("Delete") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ((ChainScene)getScene()).getChain().getElements().remove((ElementCondition) getObject());
                        }
                    }
                ));

            return menu;
        }
    }

    private class ConditionEditor implements EditProvider {

        @Override
        public void edit(Widget widget) {
            final WElementCondition wcondition = (WElementCondition) widget;
            final ElementCondition condition = (ElementCondition)wcondition.getObject();
            showEditorDialog(condition);
        }
    }

    private void showEditorDialog (final ElementCondition conditionDto) {

        final ChainScene scene = (ChainScene) getScene();
        final List<DataLink> links = scene.getChain().getOutputLinks(getId());
        if(true)return;
        //TODO
//        final JGroovyEditorPanel panel = new JGroovyEditorPanel(conditionDto, editable);
//        final JDialog optionPaneDialog = new JDialog();
//        final JOptionPane optPane = new JOptionPane(panel,
//            JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
//
//        optPane.addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent e) {
//                if (e.getPropertyName().equals("value")) {
//
//                    switch ((Integer) e.getNewValue()) {
//                        case JOptionPane.OK_OPTION:
//                            conditionDto.setExpression(panel.getGroovyScript());
//                            w_center.removeChildren();
//                            inputsWidgets.clear();
//
//                            for (final Parameter in : conditionDto.getInputs()) {
//                                addInput(in);
//                            }
//
//                            //update links
//                            scene.getChain().getDataLinks().removeAll(links);
//
//                            final List<DataLink> linkToRemove = new ArrayList<DataLink>();
//                            for (final DataLink linkDto : links) {
//                                boolean targetParamFound = false;
//                                for (final Parameter in : conditionDto.getInputs()) {
//                                    if (linkDto.getTargetCode().equals(in.getCode())) {
//                                        targetParamFound = true;
//                                        break;
//                                    }
//                                }
//                                if (!targetParamFound) {
//                                    linkToRemove.add(linkDto);
//                                }
//                            }
//                            links.removeAll(linkToRemove);
//                            for (final DataLink linkDto : links) {
//                                scene.getChain().getDataLinks().add(linkDto);
//                            }
//
//                            optionPaneDialog.dispose();
//                            getScene().validate();
//                            break;
//                        case JOptionPane.CANCEL_OPTION:
//                            optionPaneDialog.dispose();
//                            getScene().validate();
//                            break;
//                    }
//                }
//            }
//        });
//        optionPaneDialog.setTitle("Conditional Edition");
//        optionPaneDialog.setContentPane(optPane);
//        optionPaneDialog.pack();
//        optionPaneDialog.setResizable(true);
//        optionPaneDialog.setLocationRelativeTo(null);
//        optionPaneDialog.setModal(true);
//        optionPaneDialog.setVisible(true);
    }

}
