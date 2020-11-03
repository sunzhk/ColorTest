package com.sunzk.colortest.entity;

import android.graphics.Color;

public class HSB {

	private float h;
	private float s;
	private float b;

	public HSB() {
	}

	public HSB(float[] colors) {
		h = colors[0];
		s = colors[1];
		b = colors[2];
	}

	public HSB(float h, float s, float b) {
		this.h = h;
		this.s = s;
		this.b = b;
	}
	
	public int getRGBColor() {
		return Color.HSVToColor(new float[]{h, s, b});
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}

	public float getS() {
		return s;
	}

	public void setS(float s) {
		this.s = s;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HSB{");
		sb.append("h=").append(h);
		sb.append(", s=").append(s);
		sb.append(", b=").append(b);
		sb.append('}');
		return sb.toString();
	}
}
