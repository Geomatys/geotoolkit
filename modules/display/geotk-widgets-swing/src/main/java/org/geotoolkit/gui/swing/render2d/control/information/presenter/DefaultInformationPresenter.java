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

package org.geotoolkit.gui.swing.render2d.control.information.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import javax.swing.JComponent;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;


/**
 * Merge available presenter registered in META-INF services
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultInformationPresenter extends AbstractInformationPresenter{

    private static final List<InformationPresenter> PRESENTERS;

    static {
        final Iterator<InformationPresenter> ite = ServiceLoader.load(InformationPresenter.class).iterator();

        final List<InformationPresenter> presenters = new ArrayList<>();
        while(ite.hasNext()){
            presenters.add(ite.next());
        }

        Collections.sort(presenters);
        Collections.reverse(presenters);

        PRESENTERS = UnmodifiableArrayList.wrap(presenters.toArray(new InformationPresenter[presenters.size()]));
    }

    public DefaultInformationPresenter() {
        super(0);
    }

    /**
     * Create a user interface component to display the given object.
     * @param graphic , object to display
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
