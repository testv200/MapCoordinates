package in.curium.mapcoordinates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void sendCoordinates(View v) {
		EditText xEditText = (EditText) findViewById(R.id.x_id);
		EditText yEditText = (EditText) findViewById(R.id.y_id);
		String stringX = (xEditText).getText().toString();
		String stringY = (yEditText).getText().toString();
		double x;
		double y;
		try {
			x = Double.parseDouble(stringX);
			y = Double.parseDouble(stringY);
		} catch (NumberFormatException e) {
			Toast.makeText(this, "x and y should be numbers",
					Toast.LENGTH_SHORT).show();
			return;
		}
		final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
		mDialog.setMessage("Sending coordinates...");
		mDialog.setCancelable(false);
		mDialog.show();
		xEditText.setText("");
		yEditText.setText("");
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(yEditText.getWindowToken(), 0);
		// send coords to server
		(new Client(x, y) {

			@Override
			public void postResult(final Object reponseObject) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String msg = "Could not send coordinates to the server";
						if (reponseObject != null) {
							if (reponseObject instanceof JSONObject) {
								if (((JSONObject) reponseObject).has("error")) {
									try {
										msg = (String) ((JSONObject) reponseObject)
												.get("error");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									msg = ((JSONObject) reponseObject)
											.toString();
								}
							} else {
								msg = "Response received from server was in invalid format";
							}
						}
						Toast.makeText(MainActivity.this, msg,
								Toast.LENGTH_SHORT).show();
						mDialog.dismiss();
					}
				});
			}
		}).start();

	}

	public void getCoordinates(View v) {
		final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
		mDialog.setMessage("Fetching coordinates...");
		mDialog.setCancelable(false);
		mDialog.show();
		final TextView resultTextView = (TextView) findViewById(R.id.result_id);
		(new Client() {

			@Override
			public void postResult(final Object reponseObject) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (reponseObject != null) {
							if (reponseObject instanceof JSONArray) {
								JSONArray jArray = (JSONArray) reponseObject;
								StringBuilder sb = new StringBuilder();
								for (int i = 0; i < jArray.length(); i++) {
									if (!(jArray.isNull(i))) {
										try {
											String coord = (String) jArray
													.get(i);
											String[] xAndy = coord.split(",");
											sb.append("x = " + xAndy[0]
													+ ", y = " + xAndy[1]
													+ "\n");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
								resultTextView.setText(sb.toString());
							} else if (reponseObject instanceof JSONObject) {
								if (((JSONObject) reponseObject).has("error")) {
									try {
										String msg = (String) ((JSONObject) reponseObject)
												.get("error");
										resultTextView.setText(msg);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									resultTextView.setText("Could not fetch");
								}
							}
						} else {
							resultTextView.setText("Could not fetch");
						}
						mDialog.dismiss();
					}
				});
			}
		}).start();

	}
}
