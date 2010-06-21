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

import org.apache.commons.io.HexDump;
import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: wilfred Date: Jun 21, 2010 Time: 1:24:18 PM To change this template use File |
 * Settings | File Templates.
 */
public class TracingClientHttpRequestFactory implements ClientHttpRequestFactory {

    private final ClientHttpRequestFactory factory;

    public TracingClientHttpRequestFactory(ClientHttpRequestFactory factory) {
        this.factory = factory;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return new LoggingClientHttpRequest(factory.createRequest(uri, httpMethod));
    }

    private static class LoggingClientHttpRequest implements ClientHttpRequest {

        private final ClientHttpRequest decorated;
        private final ByteArrayOutputStream body = new ByteArrayOutputStream();

        public LoggingClientHttpRequest(ClientHttpRequest request) {
            decorated = request;
        }

        @Override
        public HttpMethod getMethod() {
            return decorated.getMethod();
        }

        @Override
        public URI getURI() {
            return decorated.getURI();
        }

        @Override
        public ClientHttpResponse execute() throws IOException {
            for (Map.Entry<String,List<String>> entry : decorated.getHeaders().entrySet()) {
                System.err.println(entry.getKey() + ": " + entry.getValue());
            }
            if (body.size() != 0) {
                HexDump.dump(body.toByteArray(), 0, System.err, 0);
            }
            ClientHttpResponse response = decorated.execute();
            System.err.println(response.getHeaders().getContentType());
            return response;
        }

        @Override
        public OutputStream getBody() throws IOException {
            return new TeeOutputStream(decorated.getBody(), body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return decorated.getHeaders();
        }
    }

}
