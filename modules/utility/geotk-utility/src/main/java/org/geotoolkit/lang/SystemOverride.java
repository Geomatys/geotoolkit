/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.lang;


/**
 * Marker interface for <cite>Service Providers</cite> that may override a JDK
 * service, or a service provided by a standard extension. For example the
 * {@link org.geotoolkit.image.io.plugin.RawImageReader.Spi} class provides a
 * {@code "raw"} image reader which can override the {@code "raw"} reader provided
 * by the <cite>Image I/O extension for JAI</cite> library.
 * <p>
 * The default behavior (override the standard provider or not) is provider-dependent.
 * However the behavior can be forced explicitly. For example Geotk can be prevented
 * from overriding any standard JDK or extension services in the two ways show below.
 * From the command line:
 *
 * {@preformat shell
 *     java -Dgeotk.system.override=false mypackage.MyMainClass
 * }
 *
 * or programmatically:
 *
 * {@preformat java
 *     package mypackage;
 *
 *     public class MyMainClass {
 *         public static void main(String[] args) {
 *             System.setProperty(SystemOverride.KEY_ALLOW_OVERRIDE, "false");
 *             // Launch the application now.
 *         }
 *     }
 * }
 *
 * For a complete list of Geotk services that can override standard services, see the
 * "<cite>Implemented by</cite>" section below.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
public interface SystemOverride {
    /**
     * The {@linkplain System#getProperties() system properties} key which control whatever
     * Geotk is allowed to override standard providers. The name of this property key is {@value}.
     * The value associated to this key can be {@code "true"} or {@code "false"}, case-insensitive.
     */
    String KEY_ALLOW_OVERRIDE = "geotk.system.override";
}
