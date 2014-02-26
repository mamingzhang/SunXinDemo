package com.example.sunxindemo;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

public class EncodeCodeActivity extends Activity
{
	private Spinner mCodeStyleSpinner;
	private Spinner mCodeStringSpinner;
	private ImageView mImageView;

	private ArrayAdapter<String> mCodeTwoDimensioneAdapter;
	private ArrayAdapter<String> mCodeOneDimensioneAdapter;

	private Bitmap mOldBitmap;

	private int mOldStylePos;
	private int mOldStringPos;

	public static void actionStart(Context context)
	{
		Intent intent = new Intent(context, EncodeCodeActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encodecode_activity_main);

		mCodeStyleSpinner = (Spinner) findViewById(R.id.spinner1);
		mCodeStringSpinner = (Spinner) findViewById(R.id.spinner2);
		mImageView = (ImageView) findViewById(R.id.imageView1);

		String[] codeStyle = new String[] { "二维码", "条形码" };
		ArrayAdapter<String> codeStyleAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, codeStyle);
		mCodeStyleSpinner.setAdapter(codeStyleAdapter);
		mCodeStyleSpinner.setSelection(0, false);

		String[] codeTwoString = new String[] { "二维码1", "二维码2", "二维码3" };
		mCodeTwoDimensioneAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, codeTwoString);

		String[] codeOneString = new String[] { "0000000000001",
				"0000000000002", "0000000000003" };
		mCodeOneDimensioneAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, codeOneString);

		mCodeStringSpinner.setAdapter(mCodeTwoDimensioneAdapter);
		mCodeStringSpinner.setSelection(0, false);

		mCodeStyleSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener()
				{

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id)
					{
						// TODO Auto-generated method stub
						int newPos = mCodeStyleSpinner
								.getSelectedItemPosition();
						if (mOldStylePos != newPos)
						{
							mOldStylePos = newPos;
							mOldStringPos = 0;

							if (newPos == 0)
							{
								mCodeStringSpinner
										.setAdapter(mCodeTwoDimensioneAdapter);
							}
							else
							{
								mCodeStringSpinner
										.setAdapter(mCodeOneDimensioneAdapter);
							}
							mCodeStringSpinner.setSelection(0, false);

							setEncdoeImage();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent)
					{
						// TODO Auto-generated method stub

					}
				});

		mCodeStringSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener()
				{

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id)
					{
						// TODO Auto-generated method stub
						int newPos = mCodeStringSpinner
								.getSelectedItemPosition();
						if (mOldStringPos != newPos)
						{
							mOldStringPos = newPos;

							setEncdoeImage();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent)
					{
						// TODO Auto-generated method stub

					}
				});

		setEncdoeImage();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mOldBitmap != null)
		{
			mOldBitmap.recycle();
			mOldBitmap = null;
		}
	}

	private void setEncdoeImage()
	{
		Bitmap bitmap = null;

		if (mCodeStyleSpinner.getSelectedItemPosition() == 1)
			bitmap = createOneQRCode((String) mCodeStringSpinner
					.getSelectedItem());
		else
			bitmap = createTwoQRCode((String) mCodeStringSpinner
					.getSelectedItem());

		mImageView.setImageBitmap(bitmap);
		if (mOldBitmap != null)
		{
			mOldBitmap.recycle();
			mOldBitmap = null;
		}
		mOldBitmap = bitmap;
	}

	private Bitmap createOneQRCode(String str)
	{
		try
		{
			BitMatrix matrix = new MultiFormatWriter().encode(str,
					BarcodeFormat.EAN_13, 500, 200);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					if (matrix.get(x, y))
					{
						pixels[y * width + x] = 0xff000000;
					}
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			
			return bitmap;
		}
		catch (WriterException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private Bitmap createTwoQRCode(String str)
	{
		try
		{
			BitMatrix matrix = new MultiFormatWriter().encode(
					(String) mCodeStringSpinner.getSelectedItem(),
					BarcodeFormat.QR_CODE, 300, 300);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					if (matrix.get(x, y))
					{
						pixels[y * width + x] = 0xff000000;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

			return bitmap;
		}
		catch (WriterException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
