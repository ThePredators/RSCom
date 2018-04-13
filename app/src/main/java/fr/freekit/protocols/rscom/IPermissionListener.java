package fr.freekit.protocols.rscom;

import android.hardware.usb.UsbDevice;

/**
 * Created by TheDarkBook on 13/04/18.
 */

public interface IPermissionListener {
    void onPermissionDenied(UsbDevice d);
}
