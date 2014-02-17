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
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.process.chain.model.DataLink;
import org.geotoolkit.process.chain.model.event.EventChain;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ProviderWidgetHover implements TwoStateHoverProvider {

    public ProviderWidgetHover() {
    }

    @Override
    public void unsetHovering(Widget widget) {

        final ChainScene scene = getWidgetScene(widget);

//        if (widget instanceof WElementProcess) {
//            final WElementProcess elem = (WElementProcess) widget;
//            elem.setBorder(BorderFactory.createRoundedBorder(20, 20, 8, 8, elem.getBgColor(), elem.getBgColor()));
//        } else if (widget instanceof WElementCondition) {
//            final WElementCondition elem = (WElementCondition) widget;
//            elem.setBorder(BorderFactory.createRoundedBorder(20, 20, 8, 8, elem.getBgColor(), elem.getBgColor()));
//        }

        if (scene != null) {
            final EventChain chain = scene.getChain();
            for (DataLink link : chain.getDataLinks()) {
                final ConnectionWidget linkWidget = (ConnectionWidget) scene.getWidget(link);

                if (linkWidget != null) {
                    linkWidget.setLineColor(DEFAULT_LINE_COLOR);
                    linkWidget.setStroke(new BasicStroke(1.0f));
                }
            }

            scene.validate();
        }

    }

    @Override
    public void setHovering(Widget widget) {
        final List<ConnectionWidget> widgetLinks = getWidgetLinks(widget);
        final ChainScene scene = getWidgetScene(widget);

//        if (widget instanceof WElementProcess) {
//            final WElementProcess elem = (WElementProcess) widget;
//            elem.setBorder(BorderFactory.createRoundedBorder(20, 20, 8, 8, elem.getBgColor(), SELECT_BORDER_COLOR));
//        } else if (widget instanceof WElementCondition) {
//            final WElementCondition elem = (WElementCondition) widget;
//            elem.setBorder(BorderFactory.createRoundedBorder(20, 20, 8, 8, elem.getBgColor(), SELECT_BORDER_COLOR));
//        }

        if (scene != null) {
            final EventChain chain = scene.getChain();
            for (DataLink link : chain.getDataLinks()) {
                final ConnectionWidget linkWidget = (ConnectionWidget) scene.getWidget(link);

                if (linkWidget != null) {
                    if (widgetLinks.contains(linkWidget)) {
                        linkWidget.setLineColor(SELECT_LINE_COLOR);
                        linkWidget.setStroke(new BasicStroke(2.5f));
                    } else {
                        linkWidget.setLineColor(UNSELECT_LINE_COLOR);
                        linkWidget.setStroke(new BasicStroke(0.1f));
                    }
                }
            }
            scene.validate();
        }
    }

    private ChainScene getWidgetScene(final Widget widget) {
        if (widget instanceof WElementProcess) {
            final WElementProcess elem = (WElementProcess) widget;
            return (ChainScene) elem.getScene();
        } else if (widget instanceof WConstant) {
            final WConstant elem = (WConstant) widget;
            return (ChainScene) elem.getScene();
        } else if (widget instanceof WElementCondition) {
            final WElementCondition elem = (WElementCondition) widget;
            return (ChainScene) elem.getScene();
        }
        return null;
    }

    private List<ConnectionWidget> getWidgetLinks(final Widget widget) {

        final List<ConnectionWidget> widgetLinks = new ArrayList<ConnectionWidget>();

        final ChainScene scene = getWidgetScene(widget);
        if (scene != null) {
            final EventChain chain = scene.getChain();

            if (widget instanceof WElementProcess) {
                final WElementProcess elem = (WElementProcess) widget;

                final List<WReceivingParameter> inputs = elem.getInputsParameters();
                final List<WOfferingParameter> outputs = elem.getOutputsParameters();

                for (WReceivingParameter in : inputs) {
                    final List<DataLink> links = chain.findDataLink(elem.getId(), in.getCode(), false);
                    if (!links.isEmpty() && links.size() == 1) {
                        final ConnectionWidget linkWidget = (ConnectionWidget) scene.getWidget(links.get(0));
                        if (linkWidget != null) {
                            widgetLinks.add(linkWidget);
                        }
                    }
                }

                for (WOfferingParameter out : outputs) {
                    final List<DataLink> links = chain.findDataLink(elem.getId(), out.getCode(), true);
                    if (!links.isEmpty()) {
                        for (DataLink link : links) {
                            final ConnectionWidget linkWidget = (ConnectionWidget) scene.getWidget(link);
                            if (linkWidget != null) {
                                widgetLinks.add(linkWidget);
                            }
                        }
                    }
                }

            } else if (widget instanceof WConstant) {
                final WConstant elem = (WConstant) widget;

                final WOfferingParameter out = elem.getOffering();
                final List<DataLink> links = chain.findDataLink(out.getId(), null, true);
                if (!links.isEmpty()) {
                    for (DataLink link : links) {
                        final ConnectionWidget linkWidget = (ConnectionWidget) scene.getWidget(link);
                        if (linkWidget != null) {
                            widgetLinks.add(linkWidget);
                        }
                    }
                }
            } else if (widget instanceof WElementCondition) {
                final WElementCondition elem = (WElementCondition) widget;
                final List<DataLink> links = chain.getOutputLinks(elem.getId());
                for (DataLink link : links) {
                    final ConnectionWidget linkWidget = (ConnectionWidget) scene.getWidget(link);
                    if (linkWidget != null) {
                        widgetLinks.add(linkWidget);
                    }
                }
            }
        }

        return widgetLinks;
    }
}