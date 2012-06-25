/*
 *    Geotoolkit - An Open Source Java GIS Tookit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.maven.site;

import java.io.*;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;


/**
 * Modifies the home page in order to insert the Geotoolkit.org mascot.
 * Maven invocation syntax is:
 *
 * <blockquote><code>mvn org.geotoolkit.project:geotk-site:home --non-recursive</code></blockquote>
 *
 * This Mojo is very specific to the way the Geotk home page is formatted and is absolutely not
 * for a general usage.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 *
 * @goal home
 * @phase post-site
 */
public class HomePageMojo extends AbstractMojo {
    /**
     * The encoding of HTML pages.
     */
    static final String ENCODING = "UTF-8";

    /**
     * The Maven project running this plugin.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * Modifies the home page.
     *
     * @throws MojoExecutionException if the plugin execution failed.
     */
    @Override
    public void execute() throws MojoExecutionException {
        final File file = new File(project.getBuild().getDirectory(), "site/index.html");
        try {
            process(file);
        } catch (IOException e) {
            throw new MojoExecutionException(e.toString(), e);
        }
    }

    /**
     * Modifies the given home page. This method searches for the two first
     * {@code <div class="section">} strings. A {@code <table>} with a single
     * cell is opened after the first section and closed before the second section.
     * The purpose of the table is to prevent the second section to be layout at
     * the right side of the image.
     * <p>
     * Note there is no expected {@code </div>} strings between the two first
     * {@code <div class="section">}, because the first {@code <div>} is for
     * a section of level 1 while the second {@code <div>} is for a section of
     * level 2. The section of level 1 will be closed only later. This is very
     * specific to the way the Geotk home page is formatted.
     *
     * @param file The file to the home page to modify.
     * @throws IOException If an error occurred while reading or writing the page.
     */
    private static void process(final File file) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING));
        final StringBuilder buffer = new StringBuilder();
        int section = 0; // The count of sections.
        String line;
        while ((line = in.readLine()) != null) {
            if ((line = line.trim()).length() == 0) {
                // Skip empty lines (Maven put a lot of them...).
                continue;
            }
            int s = 0;
            while ((s = line.indexOf("<div class=\"section\">", s)) >= 0) {
                buffer.append(line.substring(0, s).trim()).append('\n');
                line = line.substring(s);
                switch (++section) {
                    case 1: {
                        s = line.indexOf("<p>");
                        if (s < 0) {
                            // We didn't found the paragraph at the begining of the section.
                            // Maybe the format changed? Cancel everything; we will not put
                            // any mascot.
                            in.close();
                            return;
                        }
                        buffer.append(line.substring(0, s).trim())
                              .append("\n<table><tr><td><img src=\"images/logos/FrontPage.jpg\" align=\"left\"/>\n");
                        line = line.substring(s);
                        break; // Continue the while loop since the next <div> may be on the same line.
                    }
                    case 2: {
                        buffer.append("</td></tr></table>\n");
                        break;
                    }
                }
                s = 1; // The current line starts with <div...>, but we want to find the next one.
            }
            buffer.append(line).append('\n');
        }
        in.close();
        final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODING);
        out.write(buffer.toString());
        out.close();
    }
}
