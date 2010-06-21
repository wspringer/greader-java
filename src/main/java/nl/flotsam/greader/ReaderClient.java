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
 * A convenience class, hiding too much complexity, but sometimes that's just all you can do. (Especially if you want to
 * call methods from XSLT.) Don't expect this class to survive eventually.
 */
public class ReaderClient {

    private static ReaderOperations operations = new NullReaderOperations();
    private static String token;

    public static boolean configure(String email, String password) {
        operations = new ReaderTemplate(email, password);
        token = operations.getToken();
        return true;
    }

    public static boolean subscribe(String feed) {
        return operations.subscribe(feed, token);
    }

    public static boolean tag(String feed, String tag) {
        return operations.tag(feed, tag, token);
    }

    private static class NullReaderOperations implements ReaderOperations {

        @Override
        public <T> T doWithCallback(ReaderCallback<T> callback) {
            notifyNotConfigured();
            return null;
        }

        @Override
        public String getToken() {
            notifyNotConfigured();
            return null;
        }

        @Override
        public boolean subscribe(String feed, String token) {
            notifyNotConfigured();
            return false;
        }

        @Override
        public boolean tag(String feed, String tag, String token) {
            notifyNotConfigured();
            return false;
        }

        private void notifyNotConfigured() {
            System.err.println("WARNING: Google Reader connection not configured yet.");
        }

    }
}
