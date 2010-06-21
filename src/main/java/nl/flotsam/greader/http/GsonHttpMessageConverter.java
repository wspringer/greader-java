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
