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

/**
 * A collection of operations for interacting Google Reader.
 */
public interface ReaderOperations {

    /**
     * Allows you to do an arbitrary request against the Google Reader API, hiding the complexity of keeping an
     * authentication token allive.
     *
     * @param callback The object that is expected to do something useful against the Google Reader API.
     * @param <T> The result type.
     * @return The result of the callback.
     */
    <T> T doWithCallback(ReaderCallback<T> callback);

    /**
     * Retrieves a token to be included in editing type of calls.
     */
    String getToken();

    /**
     * Adds a subscription for the given feed.
     *
     * @param feed The URL of the feed to subscribe to.
     * @param token The token. (See {@link #getToken()}.
     * @return A boolean indicating if the request succeeded.
     */
    boolean subscribe(String feed, String token);

    /**
     * Tags a subscription with the given tag.
     *
     * @param feed The URL of the feed to subscribe to.
     * @param tag The tag to be set.
     * @param token The token. (See {@link #getToken()}.
     * @return A boolean indicating if the request succeeded.
     */
    boolean tag(String feed, String tag, String token);

}
