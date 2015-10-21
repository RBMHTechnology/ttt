/*
 * Copyright (c) 2015, Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.skynav.ttx.app.transformer.ttmlplain;

import com.skynav.ttx.app.TimedTextTransformer;
import com.skynav.ttx.transformer.TransformerContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class ValidTestCases {
    private TimedTextTransformer transformer = new TimedTextTransformer();
    
    private final String filename = "BBC123456_MainTitle_EpisodeTitle_S01E02_EN_UK_sub.ttml.xml";
    
    @Test
    public void verify() throws IOException {
        String[] args = new String[]{
            "--model", "ebuttd",
            "--transformer", "ttml-plain", 
            "--ttml-plain-suppress-output",
            "--quiet",
            "--no-verbose"};
        
        String resourceUriString = getClass().getResource(filename).getPath();
        List<String> realArgs = new ArrayList<>();
        realArgs.addAll(Arrays.asList(args));
        realArgs.add(resourceUriString);
        
        transformer.run(realArgs);
        
        Object output = transformer.getResourceState(TransformerContext.ResourceState.ttxOutput.name());
        byte[] bytes = (byte[]) output;
        System.out.print(new String(bytes));
        
    }
}
