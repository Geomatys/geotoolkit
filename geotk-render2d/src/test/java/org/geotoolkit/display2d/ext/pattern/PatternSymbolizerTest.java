/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.display2d.ext.pattern;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.JAXBException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.junit.Test;
import org.geotoolkit.filter.FilterFactory2;
import org.opengis.filter.Expression;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PatternSymbolizerTest {
    /**
     * Test Jaxb xml support.
     */
    @Test
    public void testXml() throws JAXBException, IOException {
        final MutableStyleFactory SF = GO2Utilities.STYLE_FACTORY;
        final FilterFactory2 FF = GO2Utilities.FILTER_FACTORY;

        final Map<Expression,List<Symbolizer>> ranges = new LinkedHashMap<>();
        ranges.put(FF.literal(-1000), Arrays.asList(SF.polygonSymbolizer(null, SF.fill(Color.BLUE), null)));
        ranges.put(FF.literal(-500), Arrays.asList(SF.polygonSymbolizer(null, SF.fill(Color.RED), null)));
        ranges.put(FF.literal(-100), Arrays.asList(SF.polygonSymbolizer(null, SF.fill(Color.GREEN), null)));
        ranges.put(FF.literal(100), Arrays.asList(SF.polygonSymbolizer(null, SF.fill(Color.YELLOW), null)));
        ranges.put(FF.literal(1000), Arrays.asList(SF.polygonSymbolizer(null, SF.fill(Color.GRAY), null)));

        final PatternSymbolizer ps = new PatternSymbolizer(FF.literal(0), ranges, ThreshholdsBelongTo.PRECEDING);

        final MutableStyle style = GO2Utilities.STYLE_FACTORY.style(ps);

        final Path path = Files.createTempFile("xml", ".xml");
        IOUtilities.deleteOnExit(path);
        new StyleXmlIO().writeStyle(path, style, Specification.StyledLayerDescriptor.V_1_1_0);
    }
}
