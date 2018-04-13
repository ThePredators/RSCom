package fr.freekit.protocols.rscom;


import android.app.Activity;
import android.os.Bundle;

public class UsbControllerActivity extends Activity 
{
	/** on definit l'id du produit arduino et son numero de vendeur */
	private static final int VID = 0x2341;
	private static final int PID = 0x0043;
	private static UsbController sUsbController;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		startRSConnection();
    }

    private void startRSConnection(){
		if(sUsbController == null) {
			sUsbController = new UsbController(this, mConnectionHandler, VID, PID);
		}
	}

	private void sendRsData(){
		if(sUsbController != null && sUsbController.mLoop != null) {
			// This is an exemple data
			int progress = 12;
			sUsbController.mLoop.send((byte)(progress&0xFF));
		}
	}

	private void stopRSConnection(){
		if(sUsbController != null && sUsbController.mLoop != null) {
			sUsbController.mLoop.stop();
			sUsbController = null;
		}
	}

	//fonction d'alerte si jamais le device se deconnecte du port USB ou pas
	private final IUsbConnectionHandler mConnectionHandler = new IUsbConnectionHandler() {
		@Override
		public void onUsbStopped() 
		{
			Logger.e("Usb stopped!");
		}
		
		@Override
		public void onErrorLooperRunningAlready() 
		{
			Logger.e("Looper already running!");
		}
		
		@Override
		public void onDeviceNotFound() {
			if(sUsbController != null && sUsbController.mLoop != null) {
				sUsbController.mLoop.stop();
				sUsbController = null;
			}
		}
	};
}