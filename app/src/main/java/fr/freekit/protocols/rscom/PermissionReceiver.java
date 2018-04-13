package fr.freekit.protocols.rscom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import static fr.freekit.protocols.rscom.UsbController.ACTION_USB_PERMISSION;

/**
 * Created by TheDarkBook on 13/04/18.
 */

//dans cette fonction le smartphone doit afficher une fenetre pour demander
//la permission d'utiliser la carte arduino qui vient de se connecter par USB
//une fois qu'on coche accepter il lance le handler qui va ecouter sur le port USB
public class PermissionReceiver extends BroadcastReceiver {

    private final IPermissionListener mPermissionListener;
    private Context mApplicationContext;
    private UsbController mUsbControllerContext;
    private int VID;
    private int PID;

    public PermissionReceiver(IPermissionListener permissionListener,
                              Context applicationContext, int vid, int pid, UsbController context) {
        mPermissionListener = permissionListener;
        mApplicationContext = applicationContext;
        VID = vid;
        PID = pid;
        mUsbControllerContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mApplicationContext.unregisterReceiver(this);
        if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
            if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                mPermissionListener.onPermissionDenied((UsbDevice) intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE));
            } else {
                Logger.l("Permission granted");
                UsbDevice dev = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (dev != null) {
                    if (dev.getVendorId() == VID && dev.getProductId() == PID) {
                        mUsbControllerContext.startHandler(dev);// has new thread
                    }
                } else {
                    Logger.e("device not present!");
                }
            }
        }
    }
}