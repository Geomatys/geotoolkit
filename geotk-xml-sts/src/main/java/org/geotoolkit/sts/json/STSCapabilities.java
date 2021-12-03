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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class STSCapabilities implements STSResponse {

    protected List<Link> value = new ArrayList<>();

    private final Map<String, Object> serverSettings = new LinkedHashMap();

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

    /**
     * @return the serverSettings
     */
    public Map getServerSettings() {
        return serverSettings;
    }

    public void addServerSetting(String key, Object value) {
        this.serverSettings.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof STSCapabilities) {
            STSCapabilities that = (STSCapabilities) o;
            return Objects.equals(this.value,          that.value) &&
                   Objects.equals(this.serverSettings, that.serverSettings);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.value);
        hash = 79 * hash + Objects.hashCode(this.serverSettings);
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
        for (Entry<String, Object> entry : serverSettings.entrySet()) {
            sb.append(entry.getKey()).append(":\n");
            Object o = entry.getValue();
            print(sb, o, "\t");
        }
        return sb.toString();
    }

    private static void print(StringBuilder sb, Object o, String tab) {
        if (o instanceof List) {
            List l = (List) o;
            for (Object lo : l) {
                print(sb, lo, tab + "\t");
            }
        } else if (o instanceof Map) {
            Map m = (Map) o;
            for (Object key : m.keySet()) {
                sb.append(tab).append(key).append(":\n");
                Object mo = m.get(key);
                print(sb, mo, tab + "\t");
            }
        } else {
            sb.append(o).append('\n');
        }
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
