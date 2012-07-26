package com.coolsounds;

import android.content.Context;
import android.widget.Button;

public class ButtonPlus extends Button {

	private int mMenuText;
	private int mMenuSound;
	
	public int getMenuText() {
		return mMenuText;
	}

	public void setMenuText(int mMenuText) {
		this.mMenuText = mMenuText;
	}

	public int getMenuSound() {
		return mMenuSound;
	}

	public void setMenuSound(int mMenuSound) {
		this.mMenuSound = mMenuSound;
	}

	public ButtonPlus(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

}
