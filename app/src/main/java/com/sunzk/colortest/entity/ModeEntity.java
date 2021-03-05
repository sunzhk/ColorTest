package com.sunzk.colortest.entity;

import android.content.Intent;

import androidx.annotation.NonNull;

public class ModeEntity {
	private String title;
	private Intent intent;

	public ModeEntity(@NonNull String title, @NonNull Intent intent) {
		this.title = title;
		this.intent = intent;
	}

	public String getTitle() {
		return title;
	}

	public Intent getIntent() {
		return intent;
	}
}