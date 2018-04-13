package fr.freekit.protocols.rscom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

/**
 * Created by TheDarkBook on 13/04/18.
 */

public class UsbRunnable implements Runnable {

    // Fonction principale
    //la fonction usbrunable implemente un thread qui va se connecter a l'arduino
    private static final Object[] sSendLock = new Object[]{};
    //basically an empty array is lighter than an  actual new Object()...
    private boolean mStop = false;
    private byte mData = 0x00;
    private final UsbDevice mDevice;
    private IUsbConnectionHandler mConnectionHandler;
    private UsbManager mUsbManager;
    private Context mApplicationContext;
    private BroadcastReceiver mPermissionReceiver;
    public Thread mUsbThread;

    public UsbRunnable(UsbDevice dev, UsbManager usbManager,
                       IUsbConnectionHandler connectionHandler,
                       Context context, BroadcastReceiver receiver) {
        mDevice = dev;
        this.mConnectionHandler = connectionHandler;
        this.mUsbManager = usbManager;
        mApplicationContext = context;
        mPermissionReceiver = receiver;
    }

    //les donn�es qu'on envoie sont de type data
    //ici c'est la fonction d'envoie de donn�e
    public void send(byte data) {
        mData = data;
        synchronized (sSendLock) {
            sSendLock.notify();
        }
    }

    //la fonction stop pour arreter l'envoie de donn�e
    public void stop() {
        mStop = true;
        synchronized (sSendLock) {
            sSendLock.notify();
        }
        mStop = false;
        mUsbThread = null;
        try {
            if(mUsbThread != null)
                mUsbThread.join();
        }
        catch (InterruptedException e) {
            Logger.e(e);
        }
        try {
            mApplicationContext.unregisterReceiver(mPermissionReceiver);
        } catch(IllegalArgumentException e){

        };//bravo
    }

    @Override
    public void run() {
        //here the main USB functionality is implemented
        UsbDeviceConnection conn = mUsbManager.openDevice(mDevice);
        if (!conn.claimInterface(mDevice.getInterface(1), true)) {
            return;
        }
        // Arduino Serial usb Conv
        conn.controlTransfer(0x21, 34, 0, 0, null, 0, 0);
        conn.controlTransfer(0x21, 32, 0, 0, new byte[] { (byte) 0x80,
                0x25, 0x00, 0x00, 0x00, 0x00, 0x08 }, 7, 0);

        UsbEndpoint epIN = null;
        UsbEndpoint epOUT = null;

        UsbInterface usbIf = mDevice.getInterface(1);
        for (int i = 0; i < usbIf.getEndpointCount(); i++) {
            if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
                    epIN = usbIf.getEndpoint(i);
                else
                    epOUT = usbIf.getEndpoint(i);
            }
        }

        for (;;) {// ici on tranferre les donn�e
            synchronized (sSendLock) {
                try {
                    sSendLock.wait();
                }
                catch (InterruptedException e) {
                    if (mStop) {
                        mConnectionHandler.onUsbStopped();
                        return;
                    }
                    e.printStackTrace();
                }
            }
            //ici on transfere les donn�es de type mdata a la carte arduino
            conn.bulkTransfer(epOUT, new byte[] { mData }, 1, 0);

            if (mStop) {
                mConnectionHandler.onUsbStopped();
                return;
            }
        }
    }

    public Thread getUsbThread() {
        return mUsbThread;
    }

    public void setUsbThread(Thread usbThread) {
        mUsbThread = usbThread;
    }
}