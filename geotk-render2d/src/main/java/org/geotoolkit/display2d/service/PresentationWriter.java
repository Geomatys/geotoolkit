/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.display2d.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;
import org.apache.sis.map.Presentation;
import org.geotoolkit.display.PortrayalException;

/**
 * Writer interface for special scene presentation objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface PresentationWriter {

    String getMimeType();

    void write(final CanvasDef canvasDef, final SceneDef sceneDef, Stream<Presentation> presentations, OutputStream out) throws PortrayalException, IOException;
}
