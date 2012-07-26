package com.coolsounds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class CoolSounds extends FragmentActivity {

	final static int VSPACING = 10;
	private int HSPACING = 10;
	private int BUTTON_COUNT = 12; // define how many sounds per page
	final static int OFFSETST = 32 + 32; // status bar height
	private int maxColumns;
	private int columnW;
	private int columnH;
	private SoundController sp;
	static Context c;

	// contextMenu options
	final static int SAVE_RINGTONE = 0;
	final static int SAVE_NOTIFICATION = 1;
	final static int SAVE_BOTH = 2;
	final static int REMOVE_RINGTONE = 3;

	// adapters
	private ButtonAdapter bAdap;
	private ButtonAdapter bAdap2;

	// statistic array
	private HashMap<String, String> parameters = new HashMap<String, String>();

	// save itens from menu
	private int menuPosition;
	private int menuText;
	private int menuSound;

	static final int DIALOG_ABOUT = 0;

	private ViewFlow viewFlow;

	private AdView adView;
	private int adH = 0;
	private int acbH = 0;
	private int[] titles = { R.string.title_1, R.string.title_2, R.string.title_3,
			R.string.title_4, R.string.title_5, R.string.title_6, R.string.title_7,
			R.string.title_8, R.string.title_9, R.string.title_10, R.string.title_11,
			R.string.title_12, R.string.title_13, R.string.title_14, R.string.title_15,
			R.string.title_16, R.string.title_17, R.string.title_18, R.string.title_19,
			R.string.title_20, R.string.title_21, R.string.title_22, R.string.title_23,
			R.string.title_24 };
	private int[] sounds = { R.raw.drama, R.raw.jokesound, R.raw.laugh5, R.raw.god,
			R.raw.jupeee, R.raw.failsound, R.raw.fanfaresound, R.raw.uepa,
			R.raw.koperfect, R.raw.shot, R.raw.claps, R.raw.siren, R.raw.blah_blah_blah,
			R.raw.coins, R.raw.crowd, R.raw.friday_rocks, R.raw.snoring, R.raw.got_it,
			R.raw.like_it, R.raw.locomotive, R.raw.old_car, R.raw.toy, R.raw.yes,
			R.raw.zipper };

	// menu variables
	private static final int SHARE = Menu.FIRST;
	private static final int ABOUT = SHARE + 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create a variable to be used when the context is needed
		c = this;

		// set the volume control way
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// get the screen size
		// int height = this.getResources().getDisplayMetrics().heightPixels;
		int height = this.getResources().getDisplayMetrics().heightPixels;
		int width = this.getResources().getDisplayMetrics().widthPixels;
		float dpi = this.getResources().getDisplayMetrics().density;

		// get the layout

		// tela grande, entaum uma soh page com mais botoes
		if (height > 1000 || width > 1000) {
			setContentView(R.layout.main_tablet);
		} else {
			setContentView(R.layout.main);
		}
		if (Constantes.ADD_AD) {
			// get the ad dimensions
			LinearLayout adContainer = (LinearLayout) findViewById(R.id.adViewLayout);

			// Create the adView
			adView = new AdView(this, AdSize.BANNER, Constantes.AD_ID);

			// include the ad inside a linear layout in the main.xml
			adContainer.addView(adView);

			// create the ad request conf
			AdRequest request = new AdRequest();
			if (Constantes.AD_TEST) {
				for (String td : Constantes.AD_TEST_DEVICE) {
					request.addTestDevice(td);
				}

				request.setTesting(true);
			}

			// load the ad
			adView.loadAd(request);

			// get the final height after the dpi adjust
			adH = adContainer.getLayoutParams().height;
		}

		int orientation = getResources().getConfiguration().orientation;
		// set the column width and height
		switch (orientation) {
		case 1: // portrait
		case 3: // square
			this.maxColumns = 3;
			break;
		case 2: // landscape
			this.maxColumns = 6;
			break;
		default: // i dont know what include here...
			break;
		}

		// problemas com o tamnho, preciso de mais espaco no android 1.6
		switch (android.os.Build.VERSION.SDK_INT) {
		case 4:
			HSPACING = 15;
			break;
		case 11:
		case 12:
		case 13:
			HSPACING = 20;
			break;
		}
		// ajustes para os tablets
		if (height > 1000 || width > 1000) {
			this.maxColumns = 4;
			// create the grid 1
			GridView gridView = (GridView) findViewById(R.id.buttonGrid);
			gridView.setVerticalSpacing(VSPACING);
			gridView.setHorizontalSpacing(HSPACING);
			gridView.setNumColumns(this.maxColumns);
			BUTTON_COUNT = 24;
			// set the widht and column
			columnW = (width / this.maxColumns) - VSPACING;
			columnH = ((height - ((int) ((OFFSETST + acbH + (int) (adH)) * dpi))) / (BUTTON_COUNT / this.maxColumns))
					- HSPACING;

			bAdap = new ButtonAdapter(this, columnW, columnH, dpi, titles, sounds);

			gridView.setAdapter(bAdap);

		} else {

			// set the viewflow layout
			viewFlow = (ViewFlow) findViewById(R.id.viewflow);
			DiffAdapter adapter = new DiffAdapter(this);
			viewFlow.setAdapter(adapter);
			CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
			int indH = indic.getHeight();
			viewFlow.setFlowIndicator(indic);

			acbH = this.getSupportActionBar().getHeight();

			// set the widht and column
			columnW = (width / this.maxColumns) - VSPACING;
			columnH = ((height - ((int) ((OFFSETST + acbH + (int) (adH) + indH) * dpi))) / (BUTTON_COUNT / this.maxColumns))
					- HSPACING;

			// create the grid 1
			GridView gridView = (GridView) findViewById(R.id.buttonGrid);
			gridView.setVerticalSpacing(VSPACING);
			gridView.setHorizontalSpacing(HSPACING);
			gridView.setNumColumns(this.maxColumns);

			int[] titles2 = new int[12];
			int[] sounds2 = new int[12];
			int[] titles3 = new int[12];
			int[] sounds3 = new int[12];

			System.arraycopy(titles, 0, titles2, 0, 12);
			System.arraycopy(sounds, 0, sounds2, 0, 12);
			System.arraycopy(titles, 12, titles3, 0, 12);
			System.arraycopy(sounds, 12, sounds3, 0, 12);

			bAdap = new ButtonAdapter(this, columnW, columnH, dpi, titles3, sounds3);

			gridView.setAdapter(bAdap);

			GridView gridView2 = (GridView) findViewById(1000);
			gridView2.setVerticalSpacing(VSPACING);
			gridView2.setHorizontalSpacing(HSPACING);
			gridView2.setNumColumns(this.maxColumns);

			bAdap2 = new ButtonAdapter(this, columnW, columnH, dpi, titles2, sounds2);

			gridView2.setAdapter(bAdap2);

		}
		// get the sound controller to stop music when tha app is paused or
		// over..
		sp = SoundController.getSoundController(this);

		// generate some statistics
		parameters.clear();
		parameters.put("Orientation", String.valueOf(orientation));
		FlurryAgent.onEvent("App Info", parameters);

	}

	protected void onPause() {
		super.onPause();
		if (sp.status == 1) {
			sp.stopSounds();
		}
	}

	// -------Create the menu-------
	// create the option menu based in menu.xml structure
	public boolean onCreateOptionsMenu(android.support.v4.view.Menu menu) {
		menu.add(0, SHARE, 0, R.string.txt_share)
				.setIcon(android.R.drawable.ic_menu_share)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, ABOUT, 0, R.string.txt_about)
				.setIcon(android.R.drawable.ic_menu_info_details)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	// handle the menu buttons
	public boolean onOptionsItemSelected(android.support.v4.view.MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		/*
		 * case R.id.menu_exit: FlurryAgent.onEvent("Menu Exit active");
		 * System.exit(0); return true;
		 */
		case ABOUT:
			FlurryAgent.onEvent("Menu About active");
			showDialog(DIALOG_ABOUT);
			return true;
		case SHARE:
			FlurryAgent.onEvent("Menu Share active");
			Intent i = null;
			i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_msg));

			try {
				startActivity(Intent.createChooser(i, getString(R.string.share_title)));
			} catch (android.content.ActivityNotFoundException ex) {
				// (handle error)
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// ------- end of menu creation --------

	// ------- context menu creation -------
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// get the button option
		ButtonPlus btn = (ButtonPlus) v;

		// save id because i dont know how to get it in the other side...
		menuPosition = btn.getId();
		menuText = btn.getMenuText();
		menuSound = btn.getMenuSound();

		// set the title
		menu.setHeaderTitle(R.string.menu_title);
		menu.add(0, SAVE_RINGTONE, 0, R.string.menu_save_ringtone);
		menu.add(0, SAVE_NOTIFICATION, 0, R.string.menu_save_notification);
		menu.add(0, SAVE_BOTH, 0, R.string.menu_save_both);
		menu.add(0, REMOVE_RINGTONE, 0, R.string.txt_remove_ringtone);

		// flury
		parameters.clear();
		// parameters.put("Sound", getString(menuText));
		//parameters.put("Title", getString(menuText));
		//FlurryAgent.onEvent("Context Menu Active", parameters);
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	public boolean onContextItemSelected(android.view.MenuItem item) {
		// ----- get the button text
		String btnTitle = getString(menuText);
		// ----- get the button id
		int btnId = menuSound;

		// ---- file name
		String fileName = getString(R.string.app_name) + "-" + btnTitle;
		// flury
		parameters.clear();
		switch (item.getItemId()) {
		case SAVE_RINGTONE:
			parameters.put("Type", "Ringtone");
			saveas(btnId, btnTitle, fileName, true, false);
			return true;
		case SAVE_NOTIFICATION:
			parameters.put("Type", "Notification");
			saveas(btnId, btnTitle, fileName, false, true);
			return true;
		case SAVE_BOTH:
			parameters.put("Type", "Both");
			saveas(btnId, btnTitle, fileName, true, true);
			return true;
		case REMOVE_RINGTONE:
			parameters.put("Type", "RemoveIt");
			clearRingtones();
			break;
		}

		parameters.put("Sound", getString(menuSound));
		FlurryAgent.onEvent("ContextMenuSelec", parameters);
		return super.onContextItemSelected(item);
	}

	// ---------- end of context menu creation -------

	// ---------- methods used by flury options
	protected void onStart() {
		// initialize the flurry system...
		super.onStart();
		// coolsounds_12 in flurry
		FlurryAgent.onStartSession(this, "YOUR_FLURY_CODE_HERE");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	public void onDestroy() {
		// Destroy the AdView.
		if (Constantes.ADD_AD) {
			adView.destroy();
		}

		super.onDestroy();
	}

	// ---------- end of methods used by flury options

	// ----- Dialog control -----
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_ABOUT:
			dialog = new Dialog(c);

			// do some replaces
			String desc = getString(R.string.txt_desc);
			desc = desc.replace("@app_name@", getString(R.string.app_name));
			desc = desc.replace("@apps@",
					"<a href=\"market://search?q=pub:h3nr1ke\">h3nr1ke</a>");
			desc = desc.replace("\n", "<br />");

			String title = getString(R.string.txt_about_title);
			title = title.replace("@app_name@", getString(R.string.app_name));

			dialog.setContentView(R.layout.about_dialog);
			dialog.setTitle(title);

			TextView text = (TextView) dialog.findViewById(R.id.text);

			// there is a link in the text view
			text.setText(Html.fromHtml(desc));
			// include the clickable way
			text.setMovementMethod(LinkMovementMethod.getInstance());

			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	// ----- End of Dialog Control -----

	// ----- clean ringtones/notifications added

	public void clearRingtones() { // foreach sound, check and send the
		// intent to remove
		String path = "/sdcard/media/audio/";
		String rTitle = getString(R.string.app_name) + "-" + getString(menuText);
		String filename = rTitle + ".ogg";

		boolean exists = (new File(path + "ringtones/" + filename)).exists();
		if (exists) {
			new File(path + "ringtones/" + filename).delete();
			Log.d("path", path + filename);
		}

		exists = (new File(path + "notifications/" + filename)).exists();
		if (exists) {
			new File(path + "notifications/" + filename).delete();
			Log.d("path", path + filename);
		}
		exists = (new File(path + "coolsounds/" + filename)).exists();
		if (exists) {
			new File(path + "coolsounds/" + filename).delete();
			Log.d("path", path + filename);
		}

		File k = new File(path + "ringtones/", filename);
		this.getContentResolver().delete(
				MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath()),
				"TITLE=\"" + rTitle + "\"", null);
		k = new File(path + "notifications/", filename);
		this.getContentResolver().delete(
				MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath()),
				"TITLE=\"" + rTitle + "\"", null);
		k = new File(path + "coolsounds/", filename);
		this.getContentResolver().delete(
				MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath()),
				"TITLE=\"" + rTitle + "\"", null);

		Intent removeIntent = new Intent(Intent.ACTION_MEDIA_REMOVED);
		removeIntent.setData(Uri.parse("file://" + path + "ringtones/" + filename));
		sendBroadcast(removeIntent);
		removeIntent.setData(Uri.parse("file://" + path + "notifications/" + filename));
		sendBroadcast(removeIntent);
		removeIntent.setData(Uri.parse("file://" + path + "coolsounds/" + filename));
		sendBroadcast(removeIntent);

		Log.d("del", "file://" + path + "ringtones/" + filename);
		Log.d("del", "file://" + path + "notifications/" + filename);

		Toast.makeText(c, R.string.menu_removed, Toast.LENGTH_SHORT).show();
	}

	// ------ save file to sd
	public boolean saveas(int ressound, String title, String fileName, boolean ring,
			boolean not) {
		ProgressDialog dialog = ProgressDialog.show(CoolSounds.this, "",
				getString(R.string.menu_saving), true);
		dialog.show();
		byte[] buffer = null;
		InputStream fIn = getBaseContext().getResources().openRawResource(ressound);
		int size = 0;

		try {
			size = fIn.available();
			buffer = new byte[size];
			fIn.read(buffer);
			fIn.close();
		} catch (IOException e) {
			return false;
		}

		String path = "/sdcard/media/audio/coolsounds/";
		String filename = fileName + ".ogg";

		boolean exists = (new File(path)).exists();
		if (!exists) {
			new File(path).mkdirs();
		}

		FileOutputStream save;
		try {
			save = new FileOutputStream(path + filename);
			save.write(buffer);
			save.flush();
			save.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + path + filename)));

		File k = new File(path, filename);

		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, getString(R.string.app_name) + "-"
				+ title);
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
		values.put(MediaStore.Audio.Media.ARTIST, "h3nr1ke - "
				+ getString(R.string.app_name));
		values.put(MediaStore.Audio.Media.IS_RINGTONE, ring);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, not);
		values.put(MediaStore.Audio.Media.IS_ALARM, true);
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);

		// Insert it into the database
		this.getContentResolver().insert(
				MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath()), values);

		dialog.dismiss();

		Toast.makeText(c, R.string.menu_saved, Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		viewFlow.onConfigurationChanged(newConfig);
	}
}