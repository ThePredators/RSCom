package fr.freekit.protocols.rscom;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class UsbController {

	private Context mApplicationContext;
	private UsbManager mUsbManager;
	private IUsbConnectionHandler mConnectionHandler;
	private int VID;
	private int PID;
	public UsbRunnable mLoop;
	public Thread mUsbThread;

	protected static final String ACTION_USB_PERMISSION = "ch.serverbox.android.USB";

	// END MAIN LOOP
	private BroadcastReceiver mPermissionReceiver = new PermissionReceiver(new IPermissionListener() {
		@Override
		public void onPermissionDenied(UsbDevice d) {
			Logger.l("Permission denied on " + d.getDeviceId());
		}
	}, mApplicationContext, VID, PID, this);

	//fournir les infos sur la  carte arduino en entrant l'id du Produit
	//ET LE NUMERO DE VENDEUR
	public UsbController(Activity parentActivity,IUsbConnectionHandler connectionHandler, int vid, int pid) {
		mApplicationContext = parentActivity.getApplicationContext();
		mConnectionHandler = connectionHandler;
		mUsbManager = (UsbManager) mApplicationContext.getSystemService(Context.USB_SERVICE);
		VID = vid;
		PID = pid;
		init();
	}

	//la fonction init sert a ecouter en continue sur le port USB les infos
	private void init() {
		enumerate(new IPermissionListener() 
		{
			@Override
			public void onPermissionDenied(UsbDevice d) 
			{
				UsbManager usbman = (UsbManager) mApplicationContext
						.getSystemService(Context.USB_SERVICE);
				
				PendingIntent pi = PendingIntent.getBroadcast(mApplicationContext, 
						0, new Intent(ACTION_USB_PERMISSION), 0);
				
				mApplicationContext.registerReceiver(mPermissionReceiver,
						new IntentFilter(ACTION_USB_PERMISSION));
				usbman.requestPermission(d, pi);
			}
		});
	}

	//la fonction hander ici reste en standby pour checker a chaque fois s'il y'a ou pas 
	//un nouveau p�riph�risque (device) connecter a l'arduino via cable OTG
	public void startHandler(UsbDevice d) {
		if (mLoop != null) {
			mConnectionHandler.onErrorLooperRunningAlready();
			return;
		}
		mLoop = new UsbRunnable(d, mUsbManager, mConnectionHandler,
				mApplicationContext, mPermissionReceiver);
		mUsbThread = new Thread(mLoop);
		mLoop.setUsbThread(mUsbThread);
		mUsbThread.start();
	}

	//cette fonction permet de se connecter a l'arduino via usb
	//ici c'est la fontion enumerate qui lors de l'appuie sur le boutton enumerate 
	//va checker si le device existe pour lancer la fonction usbrun qui va communiquer avec l'arduino
	private void enumerate(IPermissionListener listener) {
		Logger.l("enumerating");
		HashMap<String, UsbDevice> devlist = mUsbManager.getDeviceList();
		Iterator<UsbDevice> deviter = devlist.values().iterator();
		while (deviter.hasNext()) {
			UsbDevice d = deviter.next();
			Logger.l("Found device: "+ String.format("%04X:%04X", d.getVendorId(),d.getProductId()));
			if (d.getVendorId() == VID && d.getProductId() == PID) {
				Logger.l("Device under: " + d.getDeviceName());
				if (!mUsbManager.hasPermission(d))
					listener.onPermissionDenied(d);
				else {
					startHandler(d);
					return;
				}
				break;
			}
		}
		Logger.l("no more devices found");
		mConnectionHandler.onDeviceNotFound();
	}

}
