package in.curium.mapcoordinates;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public abstract class Client extends Thread {

	double x;
	double y;

	private static int READ = 1;
	private static int WRITE = 2;

	private int readOrWrite;

	public Client(double x, double y) {
		this.x = x;
		this.y = y;
		this.readOrWrite = WRITE;
	}

	public Client() {
		this.readOrWrite = READ;
	}

	@Override
	public void run() {
		super.run();
		String output = getDataFromUrl();
		Object responseObject = parseJson(output);
		postResult(responseObject);
	}

	private Object parseJson(String stringJson) {
		try {
			Object object = new JSONTokener(stringJson).nextValue();
			if (object instanceof JSONObject) {
				return (JSONObject) object;
			} else if (object instanceof JSONArray) {
				return (JSONArray) object;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}

	private String getDataFromUrl() {
		URL url = null;
		String data = "";
		try {
			if (readOrWrite == READ) {
				url = new URL(
						//"http://example.com/read-coords-from-text-file.php");
						"http://10.16.20.214/accesstestfile.php");
			} else if (readOrWrite == WRITE) {
				url = new URL(
						//"http://example.com/write-coords-to-text-file.php?x="
						"http://10.16.20.214/writecoords.php?x="
								+ this.x + "&y=" + this.y);
						
			}

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			try {
				InputStream is = new BufferedInputStream(
						urlConnection.getInputStream());
				data = convertInputStreamToSting(is);

			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
	}

	private String convertInputStreamToSting(final InputStream is) {
		final char[] buffer = new char[1024 * 4];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
		} catch (UnsupportedEncodingException ex) {
			/* ... */
		} catch (IOException ex) {
			/* ... */
		}
		return out.toString();
	}
	
	public abstract void postResult(Object reponseObject);
}
