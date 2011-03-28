/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.feature;

import java.io.Serializable;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;
import org.opengis.feature.type.Name;


/**
 * Simple implementation of Name.
 * <p>
 * This class emulates QName, and is used as the implementation of both AttributeName and
 * TypeName (so when the API settles down we should have a quick fix.
 * <p>
 * Its is advantageous to us to be able to:
 * <ul>
 * <li>Have a API in agreement with QName - considering our target audience
 * <li>Strongly type AttributeName and TypeName separately
 * </ul>
 * The ISO interface move towards combining the AttributeName and Attribute classes,
 * and TypeName and Type classes, while we understand the attractiveness of this on a
 * UML diagram it is very helpful to keep these concepts separate when playing with
 * a strongly typed language like java.
 * </p>
 * <p>
 * It case it is not obvious this is a value object and equality is based on
 * namespace and name.
 * </p>
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 * @author Johann Sorel, Geomatys
 * @module pending
 */
public class DefaultName implements Name,Serializable {
    /**
     * Namespace / scope
     */
    private final String namespace;

    /**
     * Local part
     */
    private final String local;

    private final String separator;

    /**
     * Constructs an instance with the local part set. Namespace / scope is
     * set to null.
     *
     * @param local The local part of the name.
     */
    public DefaultName(final String local) {
        this(null, local);
    }

    public DefaultName(final QName qname) {
        this(qname.getNamespaceURI(), qname.getLocalPart());
    }

    /**
     * Constructs an instance with the local part and namespace set.
     *
     * @param namespace The namespace or scope of the name.
     * @param local The local part of the name.
     *
     */
    public DefaultName(final String namespace, final String local) {
        this(namespace, ":", local);
    }

    /**
     * Constructs an instance with the local part and namespace set.
     *
     * @param namespace The namespace or scope of the name.
     * @param local The local part of the name.
     *
     */
    public DefaultName(final String namespace, final String separator, final String local) {
        this.namespace = namespace;
        this.separator = separator;
        this.local = local;
    }

    @Override
    public boolean isGlobal() {
        return getNamespaceURI() == null;
    }

    @Override
    public String getSeparator() {
        return separator;
    }

    @Override
    public String getNamespaceURI() {
        return namespace;
    }

    @Override
    public String getLocalPart() {
        return local;
    }

    @Override
    public String getURI() {
        if ((namespace == null) && (local == null)) {
            return null;
        }
        if (namespace == null) {
            return local;
        }
        if (local == null) {
            return namespace;
        }
        return new StringBuilder(namespace).append(separator).append(local).toString();
    }

    /**
     * Returns a hash code value for this operand.
     */
    @Override
    public int hashCode() {
        return (namespace == null ? 0 : namespace.hashCode()) +
                37 * (local == null ? 0 : local.hashCode());
    }

    /**
     * value object with equality based on name and namespace.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Name) {
            final DefaultName other = (DefaultName) obj;
            if (!Utilities.equals(this.namespace, other.getNamespaceURI())) {
                return false;
            }
            if (!Utilities.equals(this.local, other.getLocalPart())) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Name or namespace:name
     */
    @Override
    public String toString() {
        return toJCRExtendedForm(this);
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
    public static Name valueOf(final String candidate){

        if(candidate.startsWith("{")){
            //name is in extended form
            return toSessionNamespaceFromExtended(candidate);
        }

        int index = candidate.lastIndexOf(':');

        if(index <= 0){
            return new DefaultName(null, candidate);
        }else{
            final String uri = candidate.substring(0,index);
            final String name = candidate.substring(index+1,candidate.length());
            return new DefaultName(uri, name);
        }

    }

    private static Name toSessionNamespaceFromExtended(final String candidate) {
        final int index = candidate.indexOf('}');

        if(index == -1) throw new IllegalArgumentException("Invalide extended form : "+ candidate);

        final String uri = candidate.substring(1, index);
        final String name = candidate.substring(index+1, candidate.length());

        return new DefaultName(uri, name);
    }

    public static String toJCRExtendedForm(final Name name){
        final String uri = name.getNamespaceURI();
        if(uri == null){
            return name.getLocalPart();
        }else{
            return new StringBuilder("{").append(uri).append('}').append(name.getLocalPart()).toString();
        }
    }

    public static String toExtendedForm(final Name name){
        final String uri = name.getNamespaceURI();
        if(uri == null){
            return name.getLocalPart();
        }else{
            return new StringBuilder(uri).append(':').append(name.getLocalPart()).toString();
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
    public static boolean match(final Name name, final String candidate){
        if(candidate.startsWith("{")){
            //candidate is in extended form
            return candidate.equals(DefaultName.toJCRExtendedForm(name));
        }

        final int index = candidate.lastIndexOf(':');

        if(index <= 0){
            return candidate.equals(name.getLocalPart());
        }else{
            final String uri = candidate.substring(0,index);
            final String local = candidate.substring(index+1,candidate.length());
            return uri.equals(name.getNamespaceURI()) && local.equals(name.getLocalPart());
        }
    }

    public static boolean match(final Name name, final Name candidate){
        if(name.getNamespaceURI() == null || candidate.getNamespaceURI()==null){
            //compare only localpart
            return name.getLocalPart().equals(candidate.getLocalPart());
        }else{
            return name.getNamespaceURI().equals(candidate.getNamespaceURI())
                && name.getLocalPart().equals(candidate.getLocalPart());
        }
    }

}
