package nl.flotsam.greader.http;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertiesHttpMessageConverter extends AbstractHttpMessageConverter<Properties> {

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz == Properties.class;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(MediaType.TEXT_PLAIN);
    }

    @Override
    protected Properties readInternal(Class<? extends Properties> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream in = null;
        Properties result = new Properties();
        try {
            in = inputMessage.getBody();
            result.load(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return result;
    }

    @Override
    protected void writeInternal(Properties properties, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStream out = null;
        try {
            out = outputMessage.getBody();
            properties.store(out, "");
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}
