package nl.flotsam.greader;

import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public interface ReaderCallback<T> {

    /**
     * Allows the implementer to make a request without having to worry about authentication.
     */
    T execute(RestOperations operations);


}
