/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.flotsam.greader.http;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class GsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Reader reader = null;
        try {
            reader = new InputStreamReader(inputMessage.getBody(), "UTF-8");
            Gson gson = new Gson();
            return gson.fromJson(reader, clazz);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(outputMessage.getBody(), "UTF-8");
            Gson gson = new Gson();
            gson.toJson(writer);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
    
}
