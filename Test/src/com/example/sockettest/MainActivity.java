package com.example.sockettest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.test.R;

public class MainActivity extends Activity {

	Activity activity = this;
	EditText write, ip;
	Client client;
	TextView ipadrs, read;
	public static String firstMessage;
	public static int HIDE = 1;
	public static int SHOW = 2;

	private Messenger activityMessenger = new Messenger(new MessageHandler());
	private static TextView serviceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// title(textview),write(edittext), connect(button), submit(button)

		TextView title = (TextView) findViewById(R.id.title);
		ip = (EditText) findViewById(R.id.ip);
		write = (EditText) findViewById(R.id.write);
		ipadrs = (TextView) findViewById(R.id.ipadrs);
		read = (TextView) findViewById(R.id.read);
		serviceView = (TextView) findViewById(R.id.serviceView);

		Button connect = (Button) findViewById(R.id.connect);
		Button submitbtn = (Button) findViewById(R.id.submit);
		Button serviceStart = (Button) findViewById(R.id.serviceStart);
		Button serviceStop = (Button) findViewById(R.id.serviceStop);

		ip.setHint("Write IP address here!");
		write.setHint("Write message here!");
		submitbtn.setText("Submit");
		connect.setText("Connect");

		Server server = new Server();
		server.ui(activity, ipadrs, read);
		server.start();

		serviceStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(activity, MyService.class);
				intent.putExtra("ActMessenger", activityMessenger);
				startService(intent);
			}
		});

		serviceStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				stopService(new Intent(MainActivity.this, MyService.class));
			}
		});

		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				client = new Client(3030, ip.getText().toString(), activity);
			}
		});

		submitbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				client.start();
				client.write(write.getText().toString());

			}
		});

	}

	public static class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message message) {
			String serviceMessage = message.getData().getString("message");
			serviceView.setText(serviceMessage);

		}
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// serviceI

		}

		public void onServiceDisconnected(ComponentName className) {
			// serviceView.setText("Disconnected.");
		}
	};

}
