/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.render3d;

import java.awt.FlowLayout;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.geotoolkit.display3d.Map3D;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JMap3dConfigPanel extends JPanel{

    public JMap3dConfigPanel(final JMap3D map3d) {
        super(new FlowLayout());
        final JTerrainConfigPanel terrainpane = new JTerrainConfigPanel(map3d);
        final JTerrainShadowPanel shadowpane = new JTerrainShadowPanel(map3d);

        final BoundedRangeModel sliderModel = new DefaultBoundedRangeModel(1, 0, 0, 100);
        final JSlider exageration = new JSlider(sliderModel);
        exageration.setOrientation(JSlider.HORIZONTAL);
        exageration.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final Object obj = e.getSource();
                if (obj instanceof JSlider) {
                    final JSlider slider = (JSlider) obj;
                    final Map3D map = map3d.getMap3D();

                    map.getCamera().getScale3d().z = (double)slider.getValue();
                }
            }
        });

        add(terrainpane);
        add(shadowpane);
        add(exageration);
    }

}
