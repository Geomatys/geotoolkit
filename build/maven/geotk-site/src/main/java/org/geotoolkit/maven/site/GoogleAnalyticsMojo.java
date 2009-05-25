/*
 *    Geotoolkit - An Open Source Java GIS Tookit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
 * Appends the Google Analytics scripts at the end of every HTML file generated in the site
 * directory. This plugin should be executed <strong>after</strong> <code>geotk-jar-collector</code>
 * because we don't want the Google script to be included in the javadoc to be bundled in the ZIP
 * file.
 * <p>
 * Maven invocation syntax is:
 *
 * <blockquote><code>mvn org.geotoolkit.project:geotk-site:google</code></blockquote>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 *
 * @goal google
 * @phase post-site
 */
public class GoogleAnalyticsMojo extends AbstractMojo implements FileFilter {
    /**
     * The scripts to be appended at the end of every HTML pages.
     */
    private static final String SCRIPT =
            "<script src=\"http://www.google-analytics.com/ga.js\" type=\"text/javascript\"></script>\n" +
            "<script type=\"text/javascript\">\n" +
            "try {\n" +
            "  var pageTracker = _gat._getTracker(\"UA-8374786-1\");\n" +
            "  pageTracker._trackPageview();\n" +
            "} catch(err) {}\n" +
            "</script>\n";

    /**
     * The Maven project running this plugin.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * A buffer for the trailing HTML elements in the file being updated.
     */
    private StringBuilder buffer;

    /**
     * Appends the scripts at the end of every HTML pages.
     *
     * @throws MojoExecutionException if the plugin execution failed.
     */
    @Override
    public void execute() throws MojoExecutionException {
        final String directory = project.getBuild().getDirectory();
        buffer = new StringBuilder();
        try {
            process(new File(directory));
        } catch (IOException e) {
            throw new MojoExecutionException(e.toString(), e);
        }
    }

    /**
     * Process all files in the given directory and sub-directories.
     *
     * @param directory The directory to process.
     */
    private void process(final File directory) throws IOException {
        final File[] files = directory.listFiles(this);
        if (files == null) {
            return;
        }
skip:   for (final File file : files) {
            if (file.isDirectory()) {
                process(file);
                continue;
            }
            final RandomAccessFile ra = new RandomAccessFile(file, "rw");
            /*
             * Finds the position where to insert the script (just before the </body> statement)
             * and stores in memory all the text from this </body> statement to the end of file.
             */
            long insertAt = Math.max(0, ra.length() - 100);
            ra.seek(insertAt);
            String line;
            do {
                insertAt = ra.getFilePointer();
                line = ra.readLine();
                if (line == null) {
                    /*
                     * No </BODY> statement. This happen for example if the HTML file contains
                     * only <FRAMESET> elements. Skip this file.
                     */
                    ra.close();
                    continue skip;
                }
            } while (!line.trim().equalsIgnoreCase("</body>"));
            do {
                buffer.append(line).append('\n');
            } while ((line = ra.readLine()) != null);
            /*
             * Writes the script.
             */
            ra.seek(insertAt);
            ra.writeBytes(SCRIPT);
            ra.writeBytes(buffer.toString());
            ra.close();
            buffer.setLength(0);
        }
    }

    /**
     * Returns {@code true} if the given file or directory should be processed.
     *
     * @param pathname The file to test.
     * @return {@code true} if the given file or directory should be processed.
     */
    @Override
    public boolean accept(File pathname) {
        return !pathname.isHidden() && (pathname.isDirectory() || pathname.getName().endsWith(".html"));
    }
}
