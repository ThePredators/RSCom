package fr.freekit.protocols.rscom;
import android.util.Log;


public class Logger {

	private final static String TAG = Logger.class.getSimpleName();

	public static void d(Object o){
		Log.d(">== USB Controller ==<", String.valueOf(o));
	}
	
	public static void e(Object o){
		Log.e(">== USB Controller ==<", String.valueOf(o.toString()));
	}

	public static void l(Object msg) {
		Log.d(TAG, ">==< " + msg.toString() + " >==<");
	}
}
