package org.geotoolkit.ogc.xml;

import java.util.function.Function;
import javax.xml.bind.JAXBElement;
import org.geotoolkit.ogc.xml.v200.FilterType;
import org.opengis.filter.Filter;

/**
 *
 * @author Alexis Manin (Geomatys)
 * @param <T> Specialized version of XMLFilter. I.e {@link FilterType} or {@link org.geotoolkit.ogc.xml.v110.FilterType}
 * or {@link org.geotoolkit.ogc.xml.v100.FilterType}.
 */
public interface FilterToOGCConverter<T extends XMLFilter> extends Function<Filter, T> {

    JAXBElement<?> visit(final Filter opengisFilter);
}
