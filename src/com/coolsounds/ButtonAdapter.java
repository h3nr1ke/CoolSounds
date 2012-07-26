package com.coolsounds;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

public class ButtonAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	private int width;
	private int height;
	private int btnText;
	private SoundController sp;
	private float dpi = 1;
	private int mPos = 0;

	// the followings arrays are responsibles for create a new button
	private int[] titles;

	private int[] sounds;

	public void setTitles(int[] pTitles){
		this.titles = pTitles;
	}
	
	public void setSounds(int[] pSounds){
		this.sounds = pSounds;
	}
	
	public int getTitle(int pPos){
		return titles[pPos];
	}
	public int getSound(int pPos){
		return sounds[pPos];
	}
	
	public ButtonAdapter(Context c) {
		context = c;
	}

	public ButtonAdapter(Context c, int w, int h) {
		context = c;
		width = w;
		height = h;
		sp = SoundController.getSoundController(context);
	}

	public ButtonAdapter(Context c, int w, int h, float d) {
		context = c;
		width = w;
		height = h;
		sp = SoundController.getSoundController(context);
		dpi = d;
	}
	public ButtonAdapter(Context c, int w, int h, float d, int[] pTitles, int[] pSounds) {
		context = c;
		width = w;
		height = h;
		sp = SoundController.getSoundController(context);
		dpi = d;
		this.titles = pTitles;
		this.sounds = pSounds;
	}

	//@Override
	public int getCount() {
		// load the data base and see how many are added
		return sounds.length;
	}

	//@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	//@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	//@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ButtonPlus mButton;
		btnText = titles[position];

		if (convertView == null) {
			mButton = new ButtonPlus(context);
			mButton.setLayoutParams(new GridView.LayoutParams(width
					- ((int) (20 * dpi)), height));
			mButton.setBackgroundResource(R.drawable.icon_button);
			mButton.setOnClickListener(this);
			mButton.setText(btnText);
			mButton.setMenuText(btnText);
			mButton.setMenuSound(sounds[position]);
			// mButton.getTextSize();
			int paddingTitle = (int) (height - (mButton.getTextSize() * 1.3));
			mButton.setPadding(0, paddingTitle, 0, 0);
			mButton.setTextColor(Color.WHITE);
			mButton.setId(mPos);
			mPos++;
		} else {
			mButton = (ButtonPlus) convertView;
		}

		// create a temp activity to include the actual button in the context
		// menu list
		Activity lc = (Activity) context;
		lc.registerForContextMenu(mButton);
		return mButton;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		// play the song
		// choose the pressed button
		int play_sound = 0;
		int text = 0;

		try {
			play_sound = this.sounds[v.getId()];
		} catch (Exception e) {
			play_sound = this.sounds[0];
		}

		text = this.titles[v.getId()];

		sp.initSound(play_sound);

		// flury
		//parameters.clear();
		//parameters.put("Sound", getString(play_sound));
		//parameters.put("Title", getString(text));

		//FlurryAgent.onEvent("Button active", parameters);

		Toast.makeText(v.getContext(), text, Toast.LENGTH_SHORT).show();
	}

}
