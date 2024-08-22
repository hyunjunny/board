package kr.co.ultari.board;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@SpringBootTest
class BoardApplicationTests {

	@Test
	public void test() throws IOException {
		JSONObject json = new JSONObject();
		json.put("notiCode","123123");
		json.put("senderName","관리자");
		json.put("systemName","SYSTEM");
		json.put("notiTitle","제목");
		json.put("notiContents","내용");
		json.put("linkUrl","https://test.co.kr");
		json.put("sendDate","202408061046");
		json.put("sendDate","0");
		json.put("receivers","ultari2-1");

		System.out.println(json.toString());
		URL url = new URL("https://125.131.105.199:18010/notice/");
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

		//HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setRequestProperty("Content-Type", "application/json");

		try(OutputStream os = connection.getOutputStream()) {
			byte[] input = json.toString().getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		int responseCode = connection.getResponseCode();
		System.out.println("Response Code : " + responseCode);
	}

	static {
		// this part is needed cause Lebocoin has invalid SSL certificate, that cannot be normally processed by Java
		TrustManager[] trustAllCertificates = new TrustManager[]{
				new X509TrustManager() {
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null; // Not relevant.
					}

					@Override
					public void checkClientTrusted(X509Certificate[] certs, String authType) {
						// Do nothing. Just allow them all.
					}

					@Override
					public void checkServerTrusted(X509Certificate[] certs, String authType) {
						// Do nothing. Just allow them all.
					}
				}
		};

		HostnameVerifier trustAllHostnames = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true; // Just allow them all.
			}
		};

		try {
			System.setProperty("jsse.enableSNIExtension", "false");
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCertificates, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
		} catch (GeneralSecurityException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}
