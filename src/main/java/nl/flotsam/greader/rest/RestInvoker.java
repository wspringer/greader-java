package nl.flotsam.greader.rest;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class RestInvoker {

    private final HttpMethod method;
    private final String uri;
    private final RestOperations operations;

    public RestInvoker(HttpMethod method, String uri) {
        this(method, uri, new RestTemplate());
    }

    public RestInvoker(HttpMethod method, String uri, RestOperations operations) {
        this.method = method;
        this.uri = uri;
        this.operations = operations;
    }

    public static RestInvoker preparePostTo(String uri) {
        return new RestInvoker(HttpMethod.POST, uri);
    }

    public RestInvoker using(RestOperations template) {
        return new RestInvoker(method, uri, template);
    }

    public <T> TypedRestInvoker<T> expecting(Class<T> type) {
        return new TypedRestInvoker(type);
    }

    public class TypedRestInvoker<T> {

        private final Class<T> type;
        private MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String, Object>();

        private TypedRestInvoker(Class<T> type) {
            this.type = type;
        }

        public TypedRestInvoker<T> withParam(String name, Object value) {
            parameters.add(name, value);
            return this;
        }

        public T execute() {
            switch (method) {
                case POST: {
                    return operations.postForObject(uri, parameters, type);
                }
                default: {
                    throw new UnsupportedOperationException();
                }
            }
        }

    }
}
