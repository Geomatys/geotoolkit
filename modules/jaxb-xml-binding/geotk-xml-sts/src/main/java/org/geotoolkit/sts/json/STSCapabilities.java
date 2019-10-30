/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sts.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class STSCapabilities {

    protected List<Link> value = new ArrayList<>();

    /**
     * @return the value
     */
    public List<Link> getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(List<Link> value) {
        this.value = value;
    }

    public void addLink(String name, String url) {
        this.value.add(new Link(name, url));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof STSCapabilities) {
            STSCapabilities that = (STSCapabilities) o;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[STSCapabilities]\nvalue:");
        if (value != null) {
            for (Link v : value) {
                sb.append(v).append('\n');
            }
        }
        return sb.toString();
    }

    public static class Link {

        protected String name;
        protected String url;

        public Link() {

        }

        public Link(String name, String url) {
            this.name = name;
            this.url = url;
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
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url the url to set
         */
        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.name);
            hash = 97 * hash + Objects.hashCode(this.url);
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Link) {
                Link that = (Link) o;
                return Objects.equals(this.name, that.name) &&
                       Objects.equals(this.url, that.url);
            }
            return false;
        }

        @Override
        public String toString() {
            return "[Link]\nname=" + this.name + "\nurl=" + this.url;
        }
     }
}
