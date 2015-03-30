package com.example.dylosusbdatatransferapp;

import android.hardware.usb.UsbDevice;

public class DeviceHolder {
	
	static DeviceHolder instance = null;
	UsbDevice device = null;
	public UsbDevice getDevice() {
		return device;
	}
	public static DeviceHolder getInstance(){
		if (instance == null)
			instance = new DeviceHolder();
		return instance;
	}
	public void setDevice(UsbDevice device) {
		this.device = device;
	}
	private DeviceHolder(){
		
	}

}
