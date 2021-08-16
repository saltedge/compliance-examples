/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2020 Salt Edge.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.saltedge.connector.sdk.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceTools {
    private static final Logger log = LoggerFactory.getLogger(ResourceTools.class);

    /**
     * Read file from `resources`
     *
     * @param path of file in application resources
     * @return file content or null
     */
    public static String readResourceFile(String path) {
        try {
            Resource fileResource = new ClassPathResource(path);
            return readFromInputStream(fileResource.getInputStream());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * Read file from file system
     *
     * @param path of file in file system
     * @return file content or null
     */
    public static String readFile(String path) {
        try {
            File file = new File(path);
            return readFromPath(file.toPath());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) resultStringBuilder.append(line).append("\n");
        }
        return resultStringBuilder.toString();
    }

    private static String readFromPath(Path inputPath) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) resultStringBuilder.append(line).append("\n");
        }
        return resultStringBuilder.toString();
    }
}
