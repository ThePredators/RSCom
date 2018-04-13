package fr.freekit.protocols.rscom;

public interface IUsbConnectionHandler {

	void onUsbStopped();

	void onErrorLooperRunningAlready();

	void onDeviceNotFound();
}
