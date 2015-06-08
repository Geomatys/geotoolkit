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
package org.geotoolkit.feature.type;

import javax.xml.namespace.QName;
import org.apache.sis.internal.system.DefaultFactories;
import org.geotoolkit.lang.Static;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.opengis.util.ScopedName;


/**
 * GenericName utilities.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class NamesExt extends Static {

    public static GenericName create(final QName qname) {
        return create(qname.getNamespaceURI(), qname.getLocalPart());
    }

    public static GenericName create(final String local) {
        return create(null,local);
    }
    
    public static GenericName create(final String namespace, final String local) {

        // WARNING: DefaultFactories.NAMES is not a public API and may change in any future SIS version.
        if(namespace==null){
            return DefaultFactories.forBuildin(NameFactory.class).createGenericName(null, local);
        }else{
            return DefaultFactories.forBuildin(NameFactory.class).createGenericName(null, namespace, local);
        }
    }

    /**
     * Parse a string value that can be expressed in 2 different forms :
     * JSR-283 extended form : {uri}localpart
     * Separator form : uri:localpart
     *
     * if the given string do not match any, then a Name with no namespace will be
     * created and the localpart will be the given string.
     *
     * @param candidate
     * @return Name
     */
    public static GenericName valueOf(final String candidate){

        if(candidate.startsWith("{")){
            //name is in extended form
            return toSessionNamespaceFromExtended(candidate);
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
            return new StringBuilder(ns).append(':').append(name.tip().toString()).toString();
        }
    }

    public static String toExpandedString(final GenericName name){
        String ns = getNamespace(name);
        if(ns==null){
            return name.tip().toString();
        }else{
            return new StringBuilder("{").append(ns).append('}').append(name.tip().toString()).toString();
        }
    }

    /**
     * Tests that the given string representation matches the given name.
     * String can be written with only the local part or in extendedform or JCR
     * extended form.
     *
     * @param name
     * @param candidate
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
        return (name instanceof ScopedName) ? ((ScopedName)name).path().toString() : null;
    }

}
