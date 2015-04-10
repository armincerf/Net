import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by alexdavis on 08/04/15.
 */


interface HTTPResultCode {
    public int get ( String fullURL ) throws NetException;
}

class Task1 {
    public static HTTPResultCode make () throws NetException {
        HTTPResultCode result = new HTTPResultCode() {
            @Override
            public int get(String fullURL) throws NetException {
                try {
                    URL url;
                    if(fullURL.toLowerCase().startsWith("http://")){
                       url = new URL(fullURL);
                    }
                    else{
                        url = new URL("http://" + fullURL);
                    }
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    return connection.getResponseCode();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                throw new NetException("error");
            }
        };
        return result;
    }
}