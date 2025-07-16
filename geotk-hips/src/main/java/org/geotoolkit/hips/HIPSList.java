/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.hips;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.geotoolkit.nio.IOUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @see https://www.ivoa.net/documents/HiPS/20170519/REC-HIPS-1.0-20170519.pdf part 5.2
 */
public final class HIPSList extends ArrayList<HIPSProperties>{

    public void read(InputStream in) throws IOException {
        final String string = IOUtilities.toString(in);
        final String[] lines = string.split("\n");

        HIPSProperties props = null;
        for (String line : lines) {
            line = line.trim();
            if (line.isBlank()) {
                //new entry
                if (props != null && !props.isEmpty()) {
                    add(props);
                    props = null;
                }
            } else if (line.startsWith("#")) {
                //comment, ignore it
            } else {
                if (props == null) {
                    props = new HIPSProperties();
                }
                props.append(line);
            }
        }

        //last properties
        if (props != null && !props.isEmpty()) {
            add(props);
            props = null;
        }
    }
}
