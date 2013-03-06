package org.geotoolkit.data.mif.style;

import org.opengis.style.Symbolizer;

/**
 * Base class to represent MIF styles.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 01/03/13
 */
public interface MIFSymbolizer extends Symbolizer {


    public String toMIFText();
}
