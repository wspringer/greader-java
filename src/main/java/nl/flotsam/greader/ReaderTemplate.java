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
package nl.flotsam.greader;

import nl.flotsam.greader.http.GsonHttpMessageConverter;
import nl.flotsam.greader.http.TracingClientHttpRequestFactory;
import nl.flotsam.greader.http.PropertiesHttpMessageConverter;
import nl.flotsam.greader.rest.RestInvoker;
import nl.flotsam.greader.struct.SubscribeResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static nl.flotsam.greader.rest.RestInvoker.preparePostTo;

public class ReaderTemplate implements ReaderOperations {

    private final String email;
    private final String password;
    private final RestTemplate restTemplate;
    private final static String AUTHENTICATION_URL = "https://www.google.com/accounts/ClientLogin";
    private final static String BASE_URL = "http://www.google.com/reader/api/0/";
    private final static String TOKEN_URL = BASE_URL + "token";
    private final static String QUICK_ADD_URL = BASE_URL + "subscription/quickadd";
    private final static String EDIT_URL = BASE_URL + "subscription/edit";
    private final AtomicReference<String> auth = new AtomicReference<String>();

    public ReaderTemplate(String email, String password, boolean tracing) {
        this.password = password;
        this.email = email;
        restTemplate = new AuthenticatingRestTemplate();
        List<HttpMessageConverter<?>> converters =
                new ArrayList<HttpMessageConverter<?>>(restTemplate.getMessageConverters());
        converters.add(new PropertiesHttpMessageConverter());
        converters.add(new GsonHttpMessageConverter());
        if (tracing) {
            restTemplate.setRequestFactory(new TracingClientHttpRequestFactory(restTemplate.getRequestFactory()));
        }
        restTemplate.setMessageConverters(converters);
    }

    public ReaderTemplate(String email, String password) {
        this(email, password, false);
    }

    @Override
    public <T> T doWithCallback(ReaderCallback<T> callback) {
        if (auth.get() == null) {
            updateAuth();
        }
        return callback.execute(restTemplate);
    }

    @Override
    public String getToken() {
        return doWithCallback(new ReaderCallback<String>() {
            @Override
            public String execute(RestOperations operations) {
                return operations.getForObject(TOKEN_URL, String.class);
            }
        });
    }

    @Override
    public boolean subscribe(final String feed, final String title, final String token) {
        return doWithCallback(new ReaderCallback<Boolean>() {
            @Override
            public Boolean execute(RestOperations operations) {
                SubscribeResponse result = RestInvoker.preparePostTo(QUICK_ADD_URL)
                        .using(operations)
                        .expecting(SubscribeResponse.class)
                        .withParam("quickadd", feed)
                        .withParam("ac", "subscribe")
                        .withParam("T", token)
                        .execute();
                return result != null;
            }
        });
    }

    @Override
    public boolean tag(final String feed, final String tag, final String token) {
        return doWithCallback(new ReaderCallback<Boolean>() {
            @Override
            public Boolean execute(RestOperations operations) {
                String result = RestInvoker.preparePostTo(EDIT_URL)
                        .using(operations)
                        .expecting(String.class)
                        .withParam("s", "feed/" + feed)
                        .withParam("ac", "edit")
                        .withParam("a", "user/-/label/" + tag)
                        .withParam("T", token)
                        .execute();
                return "OK".equals(result);
            }
        });
        
    }

    private void updateAuth() {
        String current = auth.get();
        String replacement = authenticate();
        auth.compareAndSet(current, replacement);
    }

    private String authenticate() {
        Properties result = preparePostTo(AUTHENTICATION_URL)
                .using(restTemplate)
                .expecting(Properties.class)
                .withParam("accountType", "HOSTED_OR_GOOGLE")
                .withParam("Email", email)
                .withParam("Passwd", password)
                .withParam("service", "reader")
                .withParam("source", "flotsam-greader-java-1.0")
                .execute();
        return (String) result.get("Auth");  
    }

    private class AuthenticatingRestTemplate extends RestTemplate {
        @Override
        protected <T> T doExecute(URI url, HttpMethod method, final RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
            return super.doExecute(url, method, new RequestCallback() {
                @Override
                public void doWithRequest(ClientHttpRequest request) throws IOException {
                    String auth = ReaderTemplate.this.auth.get();
                    if (auth != null) {
                        request.getHeaders().set("Authorization", "GoogleLogin auth=" + auth);
                    }
                    requestCallback.doWithRequest(request);
                }
            }, responseExtractor);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

}
