package io.antoon.mc.ccc;

/*import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.*;

import java.security.Security;

import static org.asynchttpclient.Dsl.*;*/

public class ExternalRequestManager {
	//static AsyncHttpClient asyncHttpClient = asyncHttpClient();

	private static void request(String url) {
		System.out.println(("\"Requesting\" " + url));
		/*asyncHttpClient.prepareGet(url).execute(new AsyncCompletionHandler<Response>() {
			@Override
			public Response onCompleted(Response response) throws Exception {
				System.out.println("onComplete");
				System.out.println(response.getResponseBody());
				return response;
			}

			@Override
			public void onThrowable(Throwable t) {
				super.onThrowable(t);
				System.out.println("onThrowable");
				System.out.println(t.toString());
			}
		});*/
	}

	public static void seenPlayer(String username, String uuid) {
		System.out.println("Seen player '" + username + "' (" + uuid + ")");
		//request("[url]/api/seenPlayer?username=" + username + "&uuid=" + uuid);
		request("https://mc.antoon.io/ccc/api/seenPlayer?username=" + username + "&uuid=" + uuid);
	}
}
