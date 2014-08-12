package in.curium.mapcoordinates;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
		String stringX = ((EditText) findViewById(R.id.x_id)).getText()
				.toString();
		String stringY = ((EditText) findViewById(R.id.y_id)).getText()
				.toString();
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

		// send coords to server
		(new Client(x, y) {

			@Override
			public void postResult(JSONObject jsonObject) {
				// you can read if request was success of fail here
			}
		}).start();

	}

	public void getCoordinates(View v) {
		final ProgressDialog mDialog = new ProgressDialog(
				getApplicationContext());
		mDialog.setMessage("Fetching coordinates...");
		mDialog.setCancelable(false);
		mDialog.show();
		final TextView resultTextView = (TextView) findViewById(R.id.result_id);
		new Client() {

			@Override
			public void postResult(JSONObject jsonObject) {
				if (jsonObject != null) {
					resultTextView.setText(jsonObject.toString());
				} else {
					resultTextView.setText("Could not fetch");
				}
				mDialog.dismiss();
			}
		};

	}
}
