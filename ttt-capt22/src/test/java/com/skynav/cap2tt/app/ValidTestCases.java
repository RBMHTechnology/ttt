/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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
 
package com.skynav.cap2tt.app;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.util.TextReporter;
import com.skynav.ttv.util.Reporter;

public class ValidTestCases {

    @Test
    public void testConversionSimple1() throws Exception {
        performConversionTest("test-001.cap", 0, 0);
    }

    @Test
    public void testConversionSimple2() throws Exception {
        performConversionTest("test-002.cap", 0, 0);
    }

    /*
    @Test
    public void testConversionSimple3() throws Exception {
        performConversionTest("test-003.cap", 0, 0);
    }
    */

    private void performConversionTest(String resourceName, int expectedErrors, int expectedWarnings) {
        performConversionTest(resourceName, expectedErrors, expectedWarnings, null);
    }

    private void performConversionTest(String resourceName, int expectedErrors, int expectedWarnings, String[] additionalOptions) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String urlString = url.toString();
        List<String> args = new java.util.ArrayList<String>();
        args.add("-v");
        args.add("--warn-on");
        args.add("all");
        if (expectedErrors >= 0) {
            args.add("--expect-errors");
            args.add(Integer.toString(expectedErrors));
        }
        if (expectedWarnings >= 0) {
            args.add("--expect-warnings");
            args.add(Integer.toString(expectedWarnings));
        }
        if (additionalOptions != null) {
            args.addAll(java.util.Arrays.asList(additionalOptions));
        }
        args.add(urlString);
        Converter cvt = new Converter();
        File inputFile = new File(url.getPath());
        Reporter reporter = new TextReporter();
        TimedText tt = cvt.convert(args.toArray(new String[args.size()]), inputFile, reporter);
        assertNotNull(tt);
        int resultCode = cvt.getResultCode(urlString);
        int resultFlags = cvt.getResultFlags(urlString);
        if (resultCode == Converter.RV_SUCCESS) {
            if ((resultFlags & Converter.RV_FLAG_ERROR_EXPECTED_MATCH) != 0) {
                fail("Unexpected success with expected error(s) match.");
            }
            if ((resultFlags & Converter.RV_FLAG_WARNING_UNEXPECTED) != 0) {
                fail("Unexpected success with unexpected warning(s).");
            }
            if ((resultFlags & Converter.RV_FLAG_WARNING_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected success with expected warning(s) mismatch.");
            }
        } else if (resultCode == Converter.RV_FAIL) {
            if ((resultFlags & Converter.RV_FLAG_ERROR_UNEXPECTED) != 0) {
                fail("Unexpected failure with unexpected error(s).");
            }
            if ((resultFlags & Converter.RV_FLAG_ERROR_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected failure with expected error(s) mismatch.");
            }
        } else
            fail("Unexpected result code " + resultCode + ".");
    }

}