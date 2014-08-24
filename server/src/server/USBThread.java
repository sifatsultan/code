package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.SerialInputOutputManager;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.widget.TextView;

import com.example.server.R;

import driver.UsbSerialDriver;
import driver.UsbSerialProber;

public class USBThread extends Thread {

	private Peace peace;

	private Activity mainActivity;
	private Context context;

	private UsbManager usbManager;
	private UsbSerialDriver usbSerialDriver;

	private TextView terminal;
	private String clientMessage = "";
	private DatagramPacket dp;

	private byte[] lMsg;
	private final ExecutorService mExecutor = Executors
			.newSingleThreadExecutor();

	private SerialInputOutputManager mSerialIoManager;

	public USBThread(Activity activity, TextView textView) {
		mainActivity = activity;
		peace = new Peace(mainActivity);
		context = mainActivity.getApplicationContext();
		terminal = textView;
		usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
	}

	public USBThread(Activity activity) {
		mainActivity = activity;
	}

	public void run() {

		// Keep on trying to find USB...
		while (usbSerialDriver == null) {
			try {
				usbSerialDriver = UsbSerialProber.findFirstDevice(usbManager);
			} catch (Exception e) {

			}
			// USB found! Install Driver...
			if (usbSerialDriver != null) {
				try {
					usbSerialDriver.open();
					usbSerialDriver.setParameters(1200, 8,
							UsbSerialDriver.STOPBITS_1,
							UsbSerialDriver.PARITY_NONE);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Starting USB receiver listener
				mSerialIoManager = new SerialInputOutputManager(
						usbSerialDriver, mListener);
				mExecutor.submit(mSerialIoManager);
				break;
			}
		}

		//
		// peace = new Peace(mainActivity);
		// usbManager = (UsbManager) mainActivity
		// .getSystemService(Context.USB_SERVICE);
		// while (usbSerialDriver == null) {
		// try {
		// usbSerialDriver = UsbSerialProber.findFirstDevice(usbManager);
		// } catch (Exception e) {
		//
		// }

		// if (usbSerialDriver != null) {
		// peace.toastThShrt("USB Connected");
		// indicateUI();
		// try {
		// usbSerialDriver.open();
		// usbSerialDriver.setParameters(1200, 8,
		// UsbSerialDriver.STOPBITS_1,
		// UsbSerialDriver.PARITY_NONE);
		// //
		// // usbSerialDriver.open();
		// // usbSerialDriver.setParameters(115200, 8,
		// // UsbSerialDriver.STOPBITS_1,
		// // UsbSerialDriver.PARITY_NONE);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// // Starting USB receiver listener...
		// mSerialIoManager = new SerialInputOutputManager(usbSerialDriver,
		// mListener);
		// mExecutor.submit(mSerialIoManager);
		// break;

		// }
	}

	private SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

		@Override
		public void onRunError(Exception e) {
			peace.toastThShrt("Runner Stopped");
		}

		@Override
		public void onNewData(final byte[] data) {
			updateReceivedData(data);

		}
	};

	private void updateReceivedData(byte[] data) {
		String messageByteArrayToString = new String(data);
		// peace.setText(terminal, messageByteArrayToString);
		peace.append(terminal, messageByteArrayToString);
		// terminal.setText(messageByteArrayToString);
	}

	//
	// public void send(char[] buffer) {
	// try {
	// String bufferString = buffer.toString();
	// byte[] bufferByteArray = bufferString.getBytes();
	// if (usbSerialDriver != null) {
	// usbSerialDriver.write(bufferByteArray, bufferString.length());
	// }
	// // usbSerialDriver.write(buffer.getBytes(), clientMessage.length());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	public void send(byte[] lMsg) {
		this.lMsg = lMsg;
		try {
			usbSerialDriver.write(new byte[] { lMsg[0], lMsg[1], lMsg[2],
					lMsg[3], lMsg[4], lMsg[5], lMsg[6] }, 2000);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// public void send(String message) {
	// clientMessage = message;
	// try {
	// if (usbSerialDriver != null) {
	// usbSerialDriver.write(clientMessage.getBytes(),
	// clientMessage.length());
	// peace.setText(tv_usb, clientMessage);
	// }
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// private void indicateUI() {
	// mainActivity.runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	//
	// Resources resources = mainActivity.getResources();
	// int color_hover = resources.getColor(R.color.footer);
	// tv_usb.setBackgroundColor(color_hover);
	// tv_usb.setTextColor(resources.getColor(R.color.white));
	// }
	// });

	// }

}