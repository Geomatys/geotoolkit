/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.wms.map;

import java.awt.geom.NoninvertibleTransformException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.statefull.StatefullCoverageLayerJ2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.go2.control.information.presenter.InformationPresenter;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.wms.WMSCoverageReference;
import org.jdesktop.swingx.JXHyperlink;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Presenter for WMS layer, this will send a getFeatureInfo query to retrieve more information.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSPresenter implements InformationPresenter{

    @Override
    public JComponent createComponent(final Object graphic,
            final RenderingContext2D context, final SearchAreaJ2D area) {
        
        if (!(graphic instanceof StatefullCoverageLayerJ2D)) {
            return null;
        }

        final StatefullCoverageLayerJ2D coveragegraphic = (StatefullCoverageLayerJ2D) graphic;
        
        if(!(coveragegraphic.getUserObject().getCoverageReference() instanceof WMSCoverageReference)){
            return null;
        }
        
        final URL url;
        try {
            url = getFeatureInfo(coveragegraphic.getUserObject(), context, area, "application/vnd.ogc.gml", 20);

            final JXHyperlink link = new JXHyperlink();
            link.setURI(url.toURI());
            final JPanel panel = new JPanel();
            panel.add(link);
            return panel;

        } catch (TransformException ex) {
            Logger.getLogger(WMSPresenter.class.getName()).log(Level.WARNING, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(WMSPresenter.class.getName()).log(Level.WARNING, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(WMSPresenter.class.getName()).log(Level.WARNING, null, ex);
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(WMSPresenter.class.getName()).log(Level.WARNING, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(WMSPresenter.class.getName()).log(Level.WARNING, null, ex);
        }


        return null;
    }

    public URL getFeatureInfo(final CoverageMapLayer layer, final RenderingContext context, final SearchArea mask, 
                final String infoFormat, final int featureCount)
                throws TransformException, FactoryException,
                MalformedURLException, NoninvertibleTransformException{
            
            final WMSCoverageReference reference = (WMSCoverageReference) layer.getCoverageReference();
            
            final RenderingContext2D ctx2D = (RenderingContext2D) context;
            final DirectPosition center = mask.getDisplayGeometry().getCentroid();

            final URL url;
                url = reference.queryFeatureInfo(
                        ctx2D.getCanvasObjectiveBounds(),
                        ctx2D.getCanvasDisplayBounds().getSize(),
                        (int) center.getOrdinate(0),
                        (int) center.getOrdinate(1),
                        reference.getLayerNames(),
                        infoFormat,featureCount);

            return url;
        }
    
}
