/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.storage.coverage;

import org.geotoolkit.storage.DataStoreFactory;

/**
 * Factory used to construct a CoverageStore from a set of parameters.
 *
 * <h2>Implementation Notes</h2>
 * <p>
 * An instance of this interface should exist for all data stores which want to
 * take advantage of the dynamic plug-in system. In addition to implementing
 * this factory interface each CoverageStore implementation should have a services file:
 * </p>
 *
 * <p>
 * <code>META-INF/services/org.geotoolkit.storage.DataStoreFactory</code>
 * </p>
 *
 * <p>
 * The file should contain a single line which gives the full name of the
 * implementing class.
 * </p>
 *
 * <p>
 * example:<br/><code>e.g.
 * org.geotoolkit.data.mytype.MyTypeDataSourceFacotry</code>
 * </p>
 *
 * <p>
 * The factories are never called directly by client code, instead the
 * CoverageStoreFinder class is used.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CoverageStoreFactory extends DataStoreFactory {

}
