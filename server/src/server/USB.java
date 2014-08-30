package server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.SerialInputOutputManager;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.widget.TextView;
import driver.UsbSerialDriver;
import driver.UsbSerialProber;

public class USB extends Thread {

	private Peace peace;
	private TextView terminal;

	private UsbManager usbManager;
	private UsbSerialDriver usbSerialDriver;
	private final ExecutorService mExecutor = Executors
			.newSingleThreadExecutor();
	private SerialInputOutputManager mSerialIoManager;

	public USB(Activity activity, TextView textView) {
		peace = new Peace(activity);
		terminal = textView;
		usbManager = (UsbManager) activity.getApplicationContext()
				.getSystemService(Context.USB_SERVICE);
	}

	public void run() {

		// Wait for USB to connect
		while (usbSerialDriver == null) {
			try {
				usbSerialDriver = UsbSerialProber.findFirstDevice(usbManager);

				// Install the driver once USB is connected
				if (usbSerialDriver != null) {
					peace.toastThShrt("USB Serial Driver found");
					try {
						usbSerialDriver.open();
						usbSerialDriver.setParameters(1200, 8,
								UsbSerialDriver.STOPBITS_1,
								UsbSerialDriver.PARITY_NONE);
					} catch (IOException e) {
						e.printStackTrace();
					}

					// Initialize USB Serial Input listener
					mSerialIoManager = new SerialInputOutputManager(
							usbSerialDriver, mListener);
					mExecutor.submit(mSerialIoManager);

					// Release the loop
					break;
				}
			} catch (Exception e) {
				peace.toastThLng("USB \n" + e.toString());
			}
		}
	}

	private SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

		@Override
		public void onRunError(Exception e) {
			peace.append(terminal, e.toString());
		}

		@Override
		public void onNewData(final byte[] data) {
			updateReceivedData(data);

		}
	};

	public void send(byte[] lMsg) {
		// Send byte once USB is installed
		if (usbSerialDriver != null) {
			try {
				usbSerialDriver.write(new byte[] { lMsg[0], lMsg[1], lMsg[2],
						lMsg[3], lMsg[4], lMsg[5], lMsg[6] }, 2000);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void updateReceivedData(byte[] data) {
		String messageByteArrayToString = new String(data);
		peace.append(terminal, messageByteArrayToString);
	}

}
