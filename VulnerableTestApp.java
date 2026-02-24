import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Random;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.naming.directory.*;
import javax.naming.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import com.thoughtworks.xstream.XStream;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.net.ssl.*;

public class VulnerableTestApp {

    // Hardcoded password
    private static final String DB_PASSWORD = "admin123";

    // Magic hardcoded file path
    private static final String FILE_PATH = "/tmp/test.txt";

    public void unsafeSQL(String userInput) throws Exception {
        // Unsafe DB Connection String Building
        String connStr = "jdbc:mysql://localhost:3306/testdb?user=root&password=" + DB_PASSWORD;

        Connection conn = DriverManager.getConnection(connStr); // No timeout
        Statement stmt = conn.createStatement();

        // Unsafe SQL Query Construction
        String query = "SELECT * FROM users WHERE username = '" + userInput + "'";
        stmt.executeQuery(query); // Resource leak (not closed)
    }

    public void fileCreation() throws Exception {
        // File Creation without setting permissions + improper permissions
        File file = new File(FILE_PATH);
        FileWriter writer = new FileWriter(file); // No encoding set
        writer.write("Sensitive Data");
        // Resource leak - not closed
    }

    public void insecureExceptionHandling() {
        try {
            int x = 10 / 0;
        } catch (Exception e) {
            // Sensitive data exposure
            e.printStackTrace();
        }
    }

    public void potentialXSS(String input) {
        // Potential XSS
        System.out.println("<html>" + input + "</html>");
    }

    public void unsafeDeserialization() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data.ser"));
        Object obj = ois.readObject(); // Unsafe Deserialization
    }

    public void insecureCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("sessionId", "12345");
        // Cookie without HttpOnly and without setDomain
        response.addCookie(cookie);
    }

    public void unsafePathHandling(String filename) throws Exception {
        File file = new File("/uploads/" + filename);
        new FileInputStream(file);
    }

    public void missingHttpValidation() throws Exception {
        URL url = new URL("http://example.com");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream is = conn.getInputStream(); // No HTTP code validation
    }

    public void unsafeRedirect(HttpServletResponse response, String url) throws Exception {
        response.sendRedirect(url); // Unsafe Redirect
    }

    public void unsafeCodeExecution(String cmd) throws Exception {
        Runtime.getRuntime().exec(cmd); // Unsafe OS Command generation
    }

    public void unsafeXPath(String userInput) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("users.xml"));

        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.evaluate("//user[name='" + userInput + "']", doc); // Unsafe XPath
    }

    public void unsafeReflection(String className) throws Exception {
        Class clazz = Class.forName(className);
        clazz.newInstance(); // Unsafe Reflection
    }

    public void unsafeLDAP(String userInput) throws Exception {
        DirContext ctx = new InitialDirContext();
        String filter = "(cn=" + userInput + ")"; // Unsafe LDAP Search
        SearchControls controls = new SearchControls();
        ctx.search("dc=example,dc=com", filter, controls);
    }

    public void jwtWithoutExpiration() {
        String jwt = Jwts.builder()
                .setSubject("user")
                .signWith(SignatureAlgorithm.HS256, "secretKey")
                .compact(); // No expiration time
    }

    public void weakRandom() {
        Random random = new Random(); // Weak Random
        int token = random.nextInt(999999);
    }

    public void weakHashing() throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5"); // Deprecated algorithm
        md.digest("password".getBytes());
    }

    public void weakEncryptionMode() throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Weak encryption mode
        SecretKeySpec key = new SecretKeySpec("1234567890123456".getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
    }

    public void potentialXXE() throws Exception {
        String xml = "<?xml version=\"1.0\"?><!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]><foo>&xxe;</foo>";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder(); // XXE not disabled
        db.parse(new ByteArrayInputStream(xml.getBytes()));
    }

    public void xmlDecoderXXE() throws Exception {
        XMLDecoder decoder = new XMLDecoder(new FileInputStream("data.xml"));
        Object obj = decoder.readObject(); // Potential XXE with XMLDecoder
    }

    public void xstreamDeserialization() {
        XStream xstream = new XStream();
        Object obj = xstream.fromXML("<user>test</user>"); // Unsafe XML Deserialization
    }

    public void restCallWithoutTimeout() throws Exception {
        URL url = new URL("http://example.com");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect(); // No timeout
    }

    public void improperFilePermissions() throws Exception {
        File file = new File("/tmp/public.txt");
        file.createNewFile(); // No permission restriction
    }

    public void incorrectStringComparison(String input) {
        if (input == "admin") { // Incorrect string comparison
            System.out.println("Admin logged in");
        }
    }

    public void ignoreSSLValidation() throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAll, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Ignoring hostname verification
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    public void ssrf(String targetUrl) throws Exception {
        URL url = new URL(targetUrl); // Potential SSRF
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.getInputStream();
    }

    public void insecureLogging(String password) {
        System.out.println("User password: " + password); // Sensitive data exposure
    }

    public void unsafeThreadTermination(Thread t) {
        t.stop(); // Unsafe thread termination
    }
}
