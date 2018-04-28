
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WebHelper {

    public static String getPage(String page) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        String output = "";
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            System.out.println("Error" + e);
        }
        try {
            url = new URL(page);
            URLConnection conn = url.openConnection();
            HostnameVerifier hv = (String urlHostName, SSLSession session) -> true /// System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                    //   + session.getPeerHost());
                    ;
            conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
            HttpsURLConnection urlConn = (HttpsURLConnection) conn;
            urlConn.setHostnameVerifier(hv);
            is = urlConn.getInputStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                output += line;
            }
        } catch (Exception e) {

            return "not found";
        }

        return output;
    }

    public static void sendGetRequest(String page) {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println("Error" + e);
        }

        try {
            HostnameVerifier hv = (String urlHostName, SSLSession session) -> true /// System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                    //   + session.getPeerHost());
                    ;
            URL url = new URL(page);
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
            HttpsURLConnection urlConn = (HttpsURLConnection) conn;
            urlConn.setHostnameVerifier(hv);
            try (InputStream is = urlConn.getInputStream()) {
                is.read();
            }
        } catch (IOException e) {

        }
    }

    public static void main(String[] args) {
        //System.out.println(getPage("https://www.instagram.com/danbilzerian/"));
        sendGetRequest("https://www.mailcollector.nl/collecting/collect.php?apikey=zXPwYUIpyt1E6TL7IU8NCdO5BguVUWgQ&&username=test&email=test55@gmail.com");
        //sendGetRequest("http://www.animenorth.ca/admin/jason/test.php?apikey=458621rgg45g51621DFG45134g&&subject=dezinated&&username=test&mail_form=test@gmail.com");
    }
}
