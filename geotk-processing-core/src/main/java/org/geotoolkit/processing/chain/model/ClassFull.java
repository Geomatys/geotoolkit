/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.processing.chain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassFull {

    private String name;

    private List<String> classes;

    public ClassFull() {

    }
    public ClassFull(final Class c) {
        if (c != null) {
            this.name = c.getName();
            this.classes = new ArrayList<String>();
            for (Class cc : Classes.getAllInterfaces(c)) {
                this.classes.add(cc.getName());
            }
            Class superClass = c.getSuperclass();
            while (superClass != null) {
                this.classes.add(superClass.getName());
                superClass = superClass.getSuperclass();
            }
        }
    }

    public ClassFull(final String name, final List<String> classes) {
        this.name = name;
        this.classes = classes;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the classes
     */
    public List<String> getClasses() {
        if(classes == null){
            classes = new ArrayList<String>();
        }
        return classes;
    }

    /**
     * @param classes the classes to set
     */
    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public Class getRealClass() {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            Logging.getLogger("org.geotoolkit.processing.chain.model").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ClassFull) {
            final ClassFull that = (ClassFull) obj;
            return Objects.equals(this.getClasses(), that.getClasses()) &&
                   Objects.equals(this.name, that.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ClassFullDto]");
        sb.append("name:").append(name).append('\n');
        if (classes != null) {
            sb.append("classes:\n");
            for (String classe : classes) {
                sb.append(classe).append("\n");
            }
        }
        return sb.toString();
    }
}
