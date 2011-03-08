/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.gui.swing.go2.control.information.presenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.spi.ServiceRegistry;
import javax.swing.JComponent;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.util.collection.UnmodifiableArrayList;


/**
 * Merge available presenter registered in META-INF services
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultInformationPresenter implements InformationPresenter{

    private static final List<InformationPresenter> PRESENTERS;

    static {
        final Iterator<InformationPresenter> ite = ServiceRegistry.lookupProviders(InformationPresenter.class);

        final List<InformationPresenter> presenters = new ArrayList<InformationPresenter>();
        while(ite.hasNext()){
            presenters.add(ite.next());
        }
        PRESENTERS = UnmodifiableArrayList.wrap(presenters.toArray(new InformationPresenter[presenters.size()]));
    }

    /**
     * Create a user interface component to display the given object.
     * @param candidate , object to display
     * @return JComponent or null if no component appropriate.
     */
    @Override
    public JComponent createComponent(final Object graphic, final RenderingContext2D context, final SearchAreaJ2D area){

        for(InformationPresenter presenter : PRESENTERS){
            final JComponent comp = presenter.createComponent(graphic, context, area);
            if(comp != null){
                return comp;
            }
        }

        return null;
    }

}
