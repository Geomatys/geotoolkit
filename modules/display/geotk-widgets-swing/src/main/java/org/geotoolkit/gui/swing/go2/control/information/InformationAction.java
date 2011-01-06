/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.control.information;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class InformationAction extends AbstractAction {

    private static final ImageIcon ICON_INFO_16 = IconBundle.getIcon("16_deco_info");
    private static final ImageIcon ICON_INFO_24 = IconBundle.getIcon("24_deco_info");

    private JMap2D map = null;
    private InformationPresenter presenter = null;

    public InformationAction(){
        this(false);
    }

    public InformationAction(boolean big){
        super("",(big) ? ICON_INFO_24 : ICON_INFO_16);
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("map_information"));
    }

    public InformationPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(InformationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null) {
            final InformationHandler handler = new InformationHandler(map);
            if(presenter != null){
                handler.setPresenter(presenter);
            }
            map.setHandler(handler);
        }
    }

    public JMap2D getMap() {
        return map;
    }

    public void setMap(JMap2D map) {
        this.map = map;
        setEnabled(map != null);
    }
}
