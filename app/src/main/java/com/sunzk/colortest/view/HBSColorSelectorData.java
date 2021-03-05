package com.sunzk.colortest.view;

import androidx.lifecycle.MutableLiveData;

public class HBSColorSelectorData {

	public static final int COLOR_H_MIN = 0;
	public static final int COLOR_H_MAX = 360;

	public static final float COLOR_S_MIN = 0;
	public static final float COLOR_S_MAX = 1.0f;

	public static final float COLOR_B_MIN = 0;
	public static final float COLOR_B_MAX = 1.0f;

	private MutableLiveData<Integer> colorH;
	private MutableLiveData<Float> colorS;
	private MutableLiveData<Float> colorB;

	public HBSColorSelectorData() {
		colorH = new MutableLiveData<>(0);
		colorS = new MutableLiveData<>(0f);
		colorB = new MutableLiveData<>(0f);
	}

	public void setColorH(int h) {
		if (getColorH().getValue() == h) {
			return;
		}
		if (h < COLOR_H_MIN) {
			colorH.postValue(COLOR_H_MIN);
		} else {
			colorH.postValue(Math.min(h, COLOR_H_MAX));
		}
	}

	public MutableLiveData<Integer> getColorH() {
		return colorH;
	}

	public void setColorS(float s) {
		if (getColorS().getValue() == s) {
			return;
		}
		if (s < COLOR_S_MIN) {
			colorS.postValue(COLOR_S_MIN);
		} else {
			colorS.postValue(Math.min(s, COLOR_S_MAX));
		}
	}
	
	public MutableLiveData<Float> getColorS() {
		return colorS;
	}

	public void setColorB(float b) {
		if (getColorB().getValue() == b) {
			return;
		}
		if (b < COLOR_B_MIN) {
			colorB.postValue(COLOR_B_MIN);
		} else {
			colorB.postValue(Math.min(b, COLOR_B_MAX));
		}
	}
	
	public MutableLiveData<Float> getColorB() {
		return colorB;
	}

}
