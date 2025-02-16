package main;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.server.R;

public class MainActivity extends Activity implements Orentation.Listener {

	private Activity activity = this;
	// private String serviceLocation;
	private TextView orientation;
	private static TextView current;
	private Messenger activityMessenger = new Messenger(new MessageHandler());
	private static boolean clicked = false;
	private Orentation mOrientation;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			TextView ip_value = (TextView) findViewById(R.id.ip_value);
			TextView global_ip_value = (TextView) findViewById(R.id.global_ip_value);
			TextView connection_value = (TextView) findViewById(R.id.connection_value);
			TextView rudder_value = (TextView) findViewById(R.id.rudder_value);
			TextView mode_value = (TextView) findViewById(R.id.mode_value);
			TextView aileron_value = (TextView) findViewById(R.id.aileron_value);
			TextView elevation_value = (TextView) findViewById(R.id.elevation_value);
			TextView thrust_value = (TextView) findViewById(R.id.thrust_value);
			TextView reply_value = (TextView) findViewById(R.id.reply_value);
			TextView json_value = (TextView) findViewById(R.id.json_value);
			current = (TextView) findViewById(R.id.current);
			current.setMovementMethod(new ScrollingMovementMethod());
			orientation = (TextView) findViewById(R.id.orientation);
			TextView next = (TextView) findViewById(R.id.next);
			TextView bearing = (TextView) findViewById(R.id.bearing);
			Button start_cameraStream = (Button) findViewById(R.id.startStream);
			ImageButton serviceStart = (ImageButton) findViewById(R.id.startService);

			mOrientation = new Orentation(
					(SensorManager) getSystemService(Activity.SENSOR_SERVICE),
					getWindow().getWindowManager());

			serviceStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (clicked) {
						stopService(new Intent(MainActivity.this,
								Location.class));
						clicked = false;
					} else {
						Intent intent = new Intent(activity, Location.class);
						intent.putExtra("ActMessenger", activityMessenger);
						startService(intent);
						clicked = true;
					}

				}
			});

			start_cameraStream.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent_stream = new Intent().setClassName(
							"com.pas.webcam", "com.pas.webcam.Rolling");
					startActivity(intent_stream);

				}
			});
			// serviceStop.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View arg0) {
			// stopService(new Intent(MainActivity.this, Location.class));
			// }
			// });

			Parse parse = new Parse(4040);
			parse.ui(activity, json_value);
			parse.start();

			ServerUDP serverUdp = new ServerUDP(3030);
			serverUdp.uiQuad(activity, aileron_value, elevation_value,
					thrust_value, rudder_value, connection_value, mode_value,
					ip_value, global_ip_value, reply_value);
			serverUdp.start();

		} catch (Exception e) {
		}
	}

	public static class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message message) {
			String serviceMessage = message.getData().getString("message");
			current.append(serviceMessage + "\n");

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mOrientation.startListening(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mOrientation.stopListening();
	}

	@Override
	public void onOrientationChanged(float azimuth, float pitch, float roll) {
		orientation.setText("Pitch: " + Float.toString(pitch) + " Roll: "
				+ Float.toString(roll) + " Yaw: " + Float.toString(azimuth));

	}
}
