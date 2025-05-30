/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.util;

import java.util.UUID;
import javax.xml.namespace.QName;
import org.apache.sis.util.iso.DefaultNameFactory;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.lang.Static;
import org.opengis.metadata.Identifier;
import org.opengis.util.GenericName;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;


/**
 * GenericName utilities.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class NamesExt extends Static {

    /**
     * Create a random generic name based on a random UUID;
     */
    public static LocalName createRandomUUID() {
        final String uuid = UUID.randomUUID().toString();
        return Names.createLocalName(null, null, uuid);
    }

    public static GenericName create(final QName qname) {
        return create(qname.getNamespaceURI(), qname.getLocalPart());
    }

    public static GenericName create(final String local) {
        return create(null,local);
    }

    /**
     *
     * @param namespace if null or empty will not be used for the name
     * @param local mandatory
     */
    public static GenericName create(final String namespace, final String local) {

        // WARNING: DefaultFactories.NAMES is not a public API and may change in any future SIS version.
        if(namespace==null || namespace.isEmpty()){
            return DefaultNameFactory.provider().createGenericName(null, local);
        }else{
            return DefaultNameFactory.provider().createGenericName(null, namespace, local);
        }
    }

    /**
     * Parse a string value that can be expressed in 2 different forms :
     * JSR-283 extended form : {uri}localpart
     * Separator form : uri:localpart
     * QName XPath form : Q{uri}localpart
     *
     * if the given string do not match any, then a Name with no namespace will be
     * created and the localpart will be the given string.
     */
    public static GenericName valueOf(final String candidate){

        if(candidate.startsWith("{")){
            //name is in extended form
            return toSessionNamespaceFromExtended(candidate);
        } else if(candidate.startsWith("Q{")){
            //name is in extended form
            return toSessionNamespaceFromExtended(candidate.substring(1));
        }

        int index = candidate.lastIndexOf(':');

        if(index <= 0){
            return NamesExt.create(null, candidate);
        }else{
            final String uri = candidate.substring(0,index);
            final String name = candidate.substring(index+1,candidate.length());
            return NamesExt.create(uri, name);
        }

    }

    private static GenericName toSessionNamespaceFromExtended(final String candidate) {
        final int index = candidate.indexOf('}');

        if(index == -1) throw new IllegalArgumentException("Invalide extended form : "+ candidate);

        final String uri = candidate.substring(1, index);
        final String name = candidate.substring(index+1, candidate.length());

        return NamesExt.create(uri, name);
    }

    public static String toExtendedForm(final GenericName name){
        final String ns = NamesExt.getNamespace(name);
        if(ns==null || ns.isEmpty()){
            return name.toString();
        }else{
            return new StringBuilder(ns).append(':').append(name.tip()).toString();
        }
    }

    public static String toExpandedString(final GenericName name){
        String ns = getNamespace(name);
        if(ns==null){
            return name.tip().toString();
        }else{
            return new StringBuilder("{").append(ns).append('}').append(name.tip()).toString();
        }
    }

    /**
     * Transform a Generic name into an XPath form used in filter property.
     * Example: Q{my-namespace}my_property
     *
     * @param name A generic name.
     * @return A String with XPath form.
     */
    public static String toXpathForm(final GenericName name) {
        String ns = getNamespace(name);
        if (ns == null) {
            return name.tip().toString();
        } else {
            return new StringBuilder("Q{").append(ns).append('}').append(name.tip()).toString();
        }
    }

    /**
     * Tests that the given string representation matches the given name.
     * String can be written with only the local part or in extendedform or JCR
     * extended form.
     *
     * @return true if the string match the name
     */
    public static boolean match(final GenericName name, final String candidate){
        if(candidate.startsWith("{")){
            //candidate is in extended form
            return candidate.equals(toExpandedString(name));
        }

        final int index = candidate.lastIndexOf(':');

        if(index <= 0){
            return candidate.equals(name.tip().toString());
        }else{
            final String uri = candidate.substring(0,index);
            final String local = candidate.substring(index+1,candidate.length());
            return uri.equals(getNamespace(name)) && local.equals(name.tip().toString());
        }
    }

    public static boolean match(final GenericName name, final GenericName candidate){
        final String ns1 = getNamespace(name);
        final String ns2 = getNamespace(candidate);
        if(ns1==null || ns2==null){
            //compare only localpart
            return name.tip().toString().equals(candidate.tip().toString());
        }else{
            return name.toString().equals(candidate.toString());
        }
    }

    public static String getNamespace(GenericName name){
        if (name instanceof ScopedName) {
            return ((ScopedName) name).path().toString();
        } else if (name instanceof Identifier) {
            return ((Identifier) name).getCodeSpace();
        } else if (name instanceof LocalName ln && !ln.scope().isGlobal()) {
            return ln.scope().name().toString();
        }
        return null;
    }
}
