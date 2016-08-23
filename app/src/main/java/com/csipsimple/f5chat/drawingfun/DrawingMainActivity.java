package com.csipsimple.f5chat.drawingfun;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.R;
import com.csipsimple.f5chat.utility.Chatutility;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

//import android.support.v7.widget.PopupMenu;


/**
 * This is demo code to accompany the Mobiletuts+ tutorial series:
 * - Android SDK: Create a Drawing App
 * - extended for follow-up tutorials on using patterns and opacity
 * 
 * Sue Smith
 * August 2013 / September 2013
 *
 */
public class
DrawingMainActivity extends Activity implements OnClickListener {

	//custom drawing view
	private DrawingView drawView;
	private LinearLayout back;
	private ImageButton select_brush_image;

	private ImageView menuSelector;
	private static final int CAMERA_REQUEST = 1888;
	private static final int SELECT_PHOTO = 100;
	//buttons
	private ImageButton currPaint;
	//sizes
	private float smallBrush, mediumBrush, largeBrush,verySmallBrush;

	LinearLayout selectBrush;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int currentapiVersion = Build.VERSION.SDK_INT;
		if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
			Chatutility.changeStatusBarCustomColor(this,"#2c3342");

		}

		//get drawing view
		drawView = (DrawingView) findViewById(R.id.drawing);

		//get the palette and first color button
		LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
		currPaint = (ImageButton) paintLayout.getChildAt(0);
		//currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));


		select_brush_image = (ImageButton)findViewById(R.id.select_brush_image);
		select_brush_image.setOnClickListener(this);

		selectBrush = (LinearLayout) findViewById(R.id.selectbrush);
		selectBrush.setOnClickListener(this);

		menuSelector = (ImageView) findViewById(R.id.menu_selector);
		menuSelector.setOnClickListener(this);

		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(this);

		//sizes from dimensions
		verySmallBrush = 5;
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);

	}

	//user clicked paint
	public void paintClicked(View view) {
		//use chosen color

		//set erase false
		drawView.setErase(false);
		drawView.setPaintAlpha(100);
		drawView.setBrushSize(drawView.getLastBrushSize());

		if (view != currPaint) {
			ImageButton imgView = (ImageButton) view;
			String color = view.getTag().toString();
			imgView.setSelected(true);
			drawView.setColor(color);
			//update ui
			currPaint.setSelected(false);
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
//			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint = (ImageButton) view;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {

			case SELECT_PHOTO:
				if (resultCode == RESULT_OK) {
					Uri selectedImage = imageReturnedIntent.getData();
					InputStream imageStream = null;
					try {
						imageStream = getContentResolver().openInputStream(selectedImage);
						Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

						BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);

						if (Build.VERSION.SDK_INT >= 16) {
							drawView.setBackground(ob);
						} else {
							drawView.setBackgroundDrawable(ob);
						}

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				break;
			case CAMERA_REQUEST:
				if (resultCode == RESULT_OK) {

					Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");

					BitmapDrawable ob = new BitmapDrawable(getResources(), photo);

					if (Build.VERSION.SDK_INT >= 16) {
						drawView.setBackground(ob);
					} else {
						drawView.setBackgroundDrawable(ob);
					}
				}
		}
	}

	public void selectBrushview() {
		final Dialog brushDialog = new Dialog(this);
		brushDialog.setTitle("Brush size:");
		brushDialog.setContentView(R.layout.brush_chooser);
		//listen for clicks on size buttons

		ImageButton verySmallBtn = (ImageButton) brushDialog.findViewById(R.id.very_small_brush);
		verySmallBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.setErase(false);
				drawView.setBrushSize(verySmallBrush);
				drawView.setLastBrushSize(verySmallBrush);
				brushDialog.dismiss();
			}
		});

		ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
		smallBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.setErase(false);
				drawView.setBrushSize(smallBrush);
				drawView.setLastBrushSize(smallBrush);
				brushDialog.dismiss();
			}
		});
		ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
		mediumBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.setErase(false);
				drawView.setBrushSize(mediumBrush);
				drawView.setLastBrushSize(mediumBrush);
				brushDialog.dismiss();
			}
		});
		ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
		largeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.setErase(false);
				drawView.setBrushSize(largeBrush);
				drawView.setLastBrushSize(largeBrush);
				brushDialog.dismiss();
			}
		});
		//show and wait for user interaction
		brushDialog.show();
	}

	private void showFilterPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		// Inflate the menu from xml
		popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
		// Setup menu item selection
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.new_page:
						newView();
						return true;
					case R.id.camera:
						cameraView();
						return true;
					case R.id.choose_from_gallery:
						galleryView();
						return true;
					case R.id.erase:
						erraseView();
						return true;
					case R.id.opacity:
						opacityView();
						return true;
					case R.id.save:
						saveView();
						return true;
					default:
						return false;
				}
			}
		});
		// Handle dismissal with: popup.setOnDismissListener(...);
		// Show the menu
		popup.show();
	}

	public void erraseView() {
		//switch to erase - choose size
		final Dialog brushDialog = new Dialog(this);
		brushDialog.setTitle("Eraser size:");
		brushDialog.setContentView(R.layout.brush_chooser);
		//size buttons

		ImageButton verySmallBtn = (ImageButton) brushDialog.findViewById(R.id.very_small_brush);
		verySmallBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.setErase(true);
				drawView.setBrushSize(verySmallBrush);
				brushDialog.dismiss();
			}
		});

		ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
		smallBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.setErase(true);
				drawView.setBrushSize(smallBrush);
				brushDialog.dismiss();
			}
		});
		ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
		mediumBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.setErase(true);
				drawView.setBrushSize(mediumBrush);
				brushDialog.dismiss();
			}
		});
		ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
		largeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.setErase(true);
				drawView.setBrushSize(largeBrush);
				brushDialog.dismiss();
			}
		});
		brushDialog.show();
	}

	public void newView() {
		//new button
		AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
		newDialog.setTitle("New drawing");
		newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
		newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				drawView.startNew();
				dialog.dismiss();
			}
		});
		newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		newDialog.show();
	}

	public void saveView() {
		//save drawing
		AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
		saveDialog.setTitle("Send drawing");
		saveDialog.setMessage("Do you want to send this drawing?");
		saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//save drawing
				drawView.setDrawingCacheEnabled(true);
				//attempt to save
				String imgSaved = MediaStore.Images.Media.insertImage(
						getContentResolver(), drawView.getDrawingCache(),
						UUID.randomUUID().toString() + ".png", "drawing");
				//feedback
				if (imgSaved != null) {

					Toast savedToast = Toast.makeText(getApplicationContext(),
							"Drawing sending...!", Toast.LENGTH_SHORT);
					savedToast.show();

					Intent intent = new Intent();
					intent.putExtra("FilePath", imgSaved);
					setResult(RESULT_OK, intent);

					finish();

				} else {
					Toast unsavedToast = Toast.makeText(getApplicationContext(),
							"Oops! something wrong happening\nPlease try again....", Toast.LENGTH_SHORT);
					unsavedToast.show();
				}
				drawView.destroyDrawingCache();
			}
		});
		saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		saveDialog.show();
	}

	public void opacityView() {
		//launch opacity chooser
		final Dialog seekDialog = new Dialog(this);
		seekDialog.setTitle("Opacity level:");
		seekDialog.setContentView(R.layout.opacity_chooser);
		//get ui elements
		final TextView seekTxt = (TextView) seekDialog.findViewById(R.id.opq_txt);
		final SeekBar seekOpq = (SeekBar) seekDialog.findViewById(R.id.opacity_seek);
		//set max
		seekOpq.setMax(100);
		//show current level
		int currLevel = drawView.getPaintAlpha();
		seekTxt.setText(currLevel + "%");
		seekOpq.setProgress(currLevel);
		//update as user interacts
		seekOpq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				seekTxt.setText(Integer.toString(progress) + "%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

		});
		//listen for clicks on ok
		Button opqBtn = (Button) seekDialog.findViewById(R.id.opq_ok);
		opqBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.setPaintAlpha(seekOpq.getProgress());
				seekDialog.dismiss();
			}
		});
		//show dialog
		seekDialog.show();

	}

	public void cameraView() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}

	public void galleryView() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.selectbrush: {
				selectBrushview();
				break;
			}
			case R.id.menu_selector: {
				showFilterPopup(v);
				break;
			}
			case R.id.back: {
				finish();
				break;
			}
			case R.id.select_brush_image:
			{
				selectBrushview();
				break;
			}
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
