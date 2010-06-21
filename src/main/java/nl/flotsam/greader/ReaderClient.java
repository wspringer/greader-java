package nl.flotsam.greader;

/**
 * A convenience class, hiding too much complexity, but sometimes that's just all you can do. (Especially if you want to
 * call methods from XSLT.)
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
