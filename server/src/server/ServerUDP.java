package server;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;

public class ServerUDP extends Thread {
	byte[] msgAr;
	int MAX_UDP_DATAGRAM_LEN = 7;
	int port;
	DatagramPacket dp;
	USB usb;
	DatagramSocket ds;
	Activity activity;
	Peace peace;

	TextView read;
	TextView AileronValue, ElevationValue, ThrustValue, RudderValue,
			ConnectionValue, ModeValue, ip_value, global_ip_value, terminal;
	private boolean quad = false;

	public ServerUDP(int port) {
		this.port = port;
	}

	public void ui(TextView read, Activity activity) {
		this.activity = activity;
		this.read = read;
		peace = new Peace(this.activity);
		quad = true;
	}

	public void uiQuad(Activity activity, TextView AileronValue,
			TextView ElevationValue, TextView ThrustValue,
			TextView RudderValue, TextView ConnectionValue, TextView ModeValue,
			TextView ip_value, TextView global_ip_value, TextView terminal) {
		this.activity = activity;
		peace = new Peace(this.activity);
		this.AileronValue = AileronValue;
		this.ElevationValue = ElevationValue;
		this.ThrustValue = ThrustValue;
		this.RudderValue = RudderValue;
		this.ConnectionValue = ConnectionValue;
		this.ModeValue = ModeValue;
		this.terminal = terminal;
		this.ip_value = ip_value;
		this.global_ip_value = global_ip_value;

	}

	public void run() {
		try {
			usb = new USB(activity, terminal);
			usb.start();

			msgAr = new byte[MAX_UDP_DATAGRAM_LEN];
			dp = new DatagramPacket(msgAr, msgAr.length);

			WifiManager wifiManager = (WifiManager) activity
					.getSystemService(activity.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipInt = wifiInfo.getIpAddress();

			ds = new DatagramSocket(port);
			String ipStrng = String.format("%d.%d.%d.%d", (ipInt & 0xff),
					(ipInt >> 8 & 0xff), (ipInt >> 16 & 0xff),
					(ipInt >> 24 & 0xff));
			peace.setText(ip_value, ipStrng);
			while (true) {
				ds.receive(dp);
				if (dp != null) {
					usb.send(msgAr);
					updateUi();
				}
			}
		} catch (Exception e) {
			peace.toastThLng(e.toString());
		}
	}

	private void updateUi() {

		byte[] data = dp.getData();
		if (data[1] == Character.valueOf('a')) {
			peace.setText(ConnectionValue, "Connected");
			peace.setText(ModeValue, "Emergency");
		} else if (data[1] == Character.valueOf('b')) {
			peace.setText(ConnectionValue, "Connected");
			peace.setText(ModeValue, "Slow Speed");
		} else if (data[1] == Character.valueOf('c')) {
			peace.setText(ConnectionValue, "Connected");
			peace.setText(ModeValue, "Hand Gesture");
		} else if (data[1] == Character.valueOf('d')) {
			peace.setText(ConnectionValue, "Connected");
			peace.setText(ModeValue, "Altitude Hold");
		} else {
			peace.setText(ConnectionValue, "Disconnected");
		}
		peace.setText(AileronValue, String.valueOf(data[2]));
		peace.setText(ElevationValue, String.valueOf(data[3]));
		peace.setText(ThrustValue, String.valueOf(data[4]));
		peace.setText(RudderValue, String.valueOf(data[5]));

	}

	public String readStringFromPacket() {
		StringBuilder str = new StringBuilder();
		try {
			byte[] data = dp.getData();
			InputStreamReader input = new InputStreamReader(
					new ByteArrayInputStream(data), Charset.forName("UTF-8"));

			for (int value; (value = input.read()) != -1;)
				str.append((char) value);

		} catch (Exception e) {
			peace.toastThShrt(e.toString());
		}
		return str.toString();
	}

}
