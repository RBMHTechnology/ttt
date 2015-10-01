/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY SKYNAV, INC. AND ITS CONTRIBUTORS “AS IS” AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SKYNAV, INC. OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.skynav.ttx.transformer;

import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.app.UnknownOptionException;
import com.skynav.ttv.util.Reporter;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

public abstract class AbstractTransformer implements Transformer {

    public static final String DEFAULT_OUTPUT_ENCODING = "UTF-8";
    protected static final String defaultOutputFileNamePattern = "out{0,number,000000}.xml";
    protected static final Charset defaultOutputEncoding;

    static {
        Charset de;
        try {
            de = Charset.forName(DEFAULT_OUTPUT_ENCODING);
        } catch (RuntimeException e) {
            de = Charset.defaultCharset();
        }
        defaultOutputEncoding = de;
    }
    // option and usage info
    protected static final String[][] longOptionSpecifications = new String[][]{
        {"output-clean", "", "clean (remove) all files in output directory prior to writing output"},
        {"output-directory", "DIRECTORY", "specify path to directory where output is to be written"},
        {"output-encoding", "ENCODING", "specify character encoding of output (default: " + defaultOutputEncoding.name() + ")"},
        {"output-indent", "YES|NO", "indent output (default: NO)"},
        {"output-pattern", "PATTERN", "specify output file name pattern (default: 'out00000')"},};
    protected static final Collection<OptionSpecification> longOptions;

    static {
        longOptions = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.add(new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }
    protected static boolean outputDirectoryClean;
    protected static String outputDirectoryPath;
    protected static String outputEncodingName;
    protected static boolean outputIndent;
    protected static String outputPattern;
    protected static File outputDirectory;
    protected static Charset outputEncoding;

    @Override
    public Collection<OptionSpecification> getShortOptionSpecs() {
        return null;
    }

    @Override
    public Collection<OptionSpecification> getLongOptionSpecs() {
        return longOptions;
    }

    @Override
    public int parseLongOption(List<String> args, int index) {
        String arg = args.get(index);
        int numArgs = args.size();
        String option = arg;
        assert option.length() > 2;
        option = option.substring(2);
        switch (option) {
            case "output-clean":
                outputDirectoryClean = true;
                break;
            case "output-directory":
                if (index + 1 > numArgs) {
                    throw new MissingOptionArgumentException("--" + option);
                }
                outputDirectoryPath = args.get(++index);
                break;
            case "output-encoding":
                if (index + 1 > numArgs) {
                    throw new MissingOptionArgumentException("--" + option);
                }
                outputEncodingName = args.get(++index);
                break;
            case "output-indent":
                if (index + 1 > numArgs) {
                    throw new MissingOptionArgumentException("--" + option);
                }
                String indent = args.get(++index);
                outputIndent = indent.equalsIgnoreCase("true") ||
                        indent.equals("1") || 
                        indent.startsWith("y") || 
                        indent.startsWith("Y");
                break;
            case "output-pattern":
                if (index + 1 > numArgs) {
                    throw new MissingOptionArgumentException("--" + option);
                }
                outputPattern = args.get(++index);
                break;
            default:
                index = index - 1;
                break;
        }
        return index + 1;
    }

    @Override
    public int parseShortOption(List<String> args, int index) {
        String arg = args.get(index);
        String option = arg;
        assert option.length() == 2;
        option = option.substring(1);
        throw new UnknownOptionException("-" + option);
    }

    @Override
    public void processDerivedOptions() {
        // output directory
        File outputDirectory;
        if (outputDirectoryPath != null) {
            outputDirectory = new File(outputDirectoryPath);
            if (!outputDirectory.exists()) {
                throw new InvalidOptionUsageException("output-directory", "directory does not exist: " + outputDirectoryPath);
            } else if (!outputDirectory.isDirectory()) {
                throw new InvalidOptionUsageException("output-directory", "not a directory: " + outputDirectoryPath);
            }
        } else {
            outputDirectory = new File(".");
        }
        this.outputDirectory = outputDirectory;
        // output encoding
        Charset outputEncoding;
        if (outputEncodingName != null) {
            try {
                outputEncoding = Charset.forName(outputEncodingName);
            } catch (Exception e) {
                outputEncoding = null;
            }
            if (outputEncoding == null) {
                throw new InvalidOptionUsageException("output-encoding", "unknown encoding: " + outputEncodingName);
            }
        } else {
            outputEncoding = null;
        }
        if (outputEncoding == null) {
            outputEncoding = defaultOutputEncoding;
        }
        this.outputEncoding = outputEncoding;
        this.outputEncodingName = outputEncoding.displayName();
        // output pattern
        String outputPattern = this.outputPattern;
        if (outputPattern == null) {
            outputPattern = defaultOutputFileNamePattern;
        }
        this.outputPattern = outputPattern;
    }

    protected static void cleanOutputDirectory(File directory, TransformerContext context) {
        Reporter reporter = context.getReporter();
        reporter.logInfo(reporter.message("*KEY*", "Cleaning ISD artifacts from output directory ''{0}''...", directory.getPath()));
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                if (name.indexOf("out") != 0) {
                    continue;
                } else if (name.indexOf(".xml") != (name.length() - 4)) {
                    continue;
                } else if (!f.delete()) {
                    throw new TransformerException("unable to clean output directory: can't delete: '" + name + "'");
                }
            }
        }
    }
}
