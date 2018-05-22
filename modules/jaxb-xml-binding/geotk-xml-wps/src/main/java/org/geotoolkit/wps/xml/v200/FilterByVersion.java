package org.geotoolkit.wps.xml.v200;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
abstract class FilterByVersion<V> extends XmlAdapter<V,V> {

    static final ThreadLocal<Boolean> IS_LEGACY = new ThreadLocal<>();

    public static boolean isV1() {
        return Boolean.TRUE.equals(IS_LEGACY.get());
    }

    public static boolean isV2() {
        return !isV1();
    }

    @Override
    public final V unmarshal(V v) {
        return v;
    }
}
