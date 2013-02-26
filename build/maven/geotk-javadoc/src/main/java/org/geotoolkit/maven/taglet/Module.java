/*
 *    Geotoolkit - An Open Source Java GIS Tookit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.maven.taglet;

import java.io.File;
import java.util.Map;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.Taglet;


/**
 * The <code>@module</code> tag. This tag expects no argument.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.20
 *
 * @since 2.2
 */
public final class Module implements Taglet {
    /**
     * Geotoolkit.org version to be referenced by this taglet.
     */
    private static final String VERSION = "4.x-SNAPSHOT";

    /**
     * Register this taglet.
     *
     * @param tagletMap the map to register this tag to.
     */
    public static void register(final Map<String,Taglet> tagletMap) {
       final Module tag = new Module();
       tagletMap.put(tag.getName(), tag);
    }

    /**
     * The base URL for Maven reports.
     */
    private static final String MAVEN_REPORTS_BASE_URL = "http://www.geotoolkit.org/";

    /**
     * The base URL for Maven repository.
     */
    private static final String MAVEN_REPOSITORY_BASE_URL = "http://maven.geotoolkit.org/";

    /**
     * Temporary variable for a tag under writing.
     */
    private String root, group, module;

    /**
     * Constructs a default <code>@module</code> taglet.
     */
    private Module() {
        super();
    }

    /**
     * Returns the name of this custom tag.
     *
     * @return The tag name.
     */
    @Override
    public String getName() {
        return "module";
    }

    /**
     * Returns {@code false} since <code>@module</code> can not be used in overview.
     *
     * @return Always {@code false}.
     */
    @Override
    public boolean inOverview() {
        return false;
    }

    /**
     * Returns {@code true} since <code>@module</code> can be used in package documentation.
     *
     * @return Always {@code true}.
     */
    @Override
    public boolean inPackage() {
        return true;
    }

    /**
     * Returns {@code true} since <code>@module</code> can be used in type documentation
     * (classes or interfaces). This is actually its main target.
     *
     * @return Always {@code true}.
     */
    @Override
    public boolean inType() {
        return true;
    }

    /**
     * Returns {@code false} since <code>@module</code> can not be used in constructor
     *
     * @return Always {@code false}.
     */
    @Override
    public boolean inConstructor() {
        return false;
    }

    /**
     * Returns {@code false} since <code>@module</code> can not be used in method documentation.
     *
     * @return Always {@code false}.
     */
    @Override
    public boolean inMethod() {
        return false;
    }

    /**
     * Returns {@code false} since <code>@module</code> can not be used in field documentation.
     *
     * @return Always {@code false}.
     */
    @Override
    public boolean inField() {
        return false;
    }

    /**
     * Returns {@code false} since <code>@module</code> is not an inline tag.
     *
     * @return Always {@code false}.
     */
    @Override
    public boolean isInlineTag() {
        return false;
    }

    /**
     * Given the <code>Tag</code> representation of this custom tag, return its string representation.
     * The default implementation invokes the array variant of this method.
     *
     * @param tag The tag to format.
     * @return A string representation of the given tag.
     */
    @Override
    public String toString(final Tag tag) {
        return toString(new Tag[] {tag});
    }

    /**
     * Given an array of {@code Tag}s representing this custom tag, return its string
     * representation.
     *
     * @param tags The tags to format.
     * @return A string representation of the given tags.
     */
    @Override
    public String toString(final Tag[] tags) {
        if (tags==null || tags.length==0) {
            return "";
        }
        final StringBuilder buffer = new StringBuilder("\n<DT><B>Module:</B></DT>");
        for (int i=0; i<tags.length; i++) {
            final Tag tag = tags[i];
            File file = tag.position().file();
            module = file.getName();
            group  = "unknown";
            root   = "modules";
            while (file != null) {
                file = file.getParentFile();
                if (file.getName().equals("src")) {
                    file = file.getParentFile();
                    if (file != null) {
                        module = file.getName();
                        file = file.getParentFile();
                        if (file != null) {
                            group = file.getName();
                            file = file.getParentFile();
                            if (file != null) {
                                root = file.getName();
                            }
                        }
                    }
                    break;
                }
            }
            buffer.append('\n').append(i==0 ? "<DD>" : "<BR>")
                  .append("<TABLE WIDTH=\"100%\" BORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"0\">" +
                          "<TR>\n<TD ALIGN=\"LEFT\">");
            /*
             * Appends the module link.
             */
            openMavenReportLink(buffer);
            buffer.append("index.html\">")
                  .append(group).append('/').append(module)
                  .append("</A>");
            /*
             * Appends the "(download binary)" link.
             */
            buffer.append("\n<FONT SIZE=\"-2\">(<A HREF=\"").append(MAVEN_REPOSITORY_BASE_URL)
                  .append("org/geotoolkit/").append(module).append('/').append(VERSION).append('/')
                  .append("\">download</A>)</FONT>");
            /*
             * Appends the "View source code for this class" link.
             */
            buffer.append("\n</TD><TD ALIGN=\"RIGHT\">\n");
            final Doc holder = tag.holder();
            if (holder instanceof ClassDoc) {
                ClassDoc outer, doc = (ClassDoc) holder;
                while ((outer = doc.containingClass()) != null) {
                    doc = outer;
                }
                buffer.append(" &nbsp;&nbsp; ");
                openMavenReportLink(buffer);
                buffer.append("cobertura/").append(doc.qualifiedName())
                      .append(".html\">View source code for this class</A>");
            }
            buffer.append("\n</TD></TR></TABLE>");
        }
        return buffer.append("</DD>\n").toString();
    }

    /**
     * Opens a {@code <A HREF>} element toward the Maven report directory.
     * A trailing slash is included.
     *
     * @param buffer The buffer in which to write.
     */
    private void openMavenReportLink(final StringBuilder buffer) {
        buffer.append("<A HREF=\"").append(MAVEN_REPORTS_BASE_URL).append(root).append('/')
              .append(group).append('/').append(module).append('/');
    }
}
