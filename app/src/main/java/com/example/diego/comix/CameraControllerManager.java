package com.example.diego.comix;

public abstract class CameraControllerManager {
	public abstract int getNumberOfCameras();
	abstract boolean isFrontFacing(int cameraId);
}
