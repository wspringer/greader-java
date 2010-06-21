package nl.flotsam.greader;

public interface ReaderOperations {

    <T> T doWithCallback(ReaderCallback<T> callback);

    String getToken();

    boolean subscribe(String feed, String title, String token);

    boolean tag(String feed, String tag, String token);

}
