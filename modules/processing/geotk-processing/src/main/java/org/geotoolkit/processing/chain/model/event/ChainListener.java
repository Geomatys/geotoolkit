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
package org.geotoolkit.processing.chain.model.event;

import java.util.EventListener;
import org.geotoolkit.util.collection.CollectionChangeEvent;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ChainListener extends EventListener{

    void constantChange(CollectionChangeEvent event);

    void descriptorChange(CollectionChangeEvent event);

    void linkChange(CollectionChangeEvent event);

    void executionLinkChange(CollectionChangeEvent event);

    void inputChange(CollectionChangeEvent event);

    void outputChange(CollectionChangeEvent event);

}
