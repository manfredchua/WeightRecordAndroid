package com.squad22.fit.activity;

import java.io.File;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.Kii.Site;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.squad22.fit.R;
import com.squad22.fit.dao.ProfileDao;
import com.squad22.fit.entity.Profile;
import com.squad22.fit.utils.CommUtils;
import com.squad22.fit.utils.Constants;
import com.squad22.fit.utils.HeightPickerDialog;
import com.squad22.fit.utils.ImagesUtils;

@SuppressLint("ValidFragment")
public class CompleteProfileActivity extends FragmentActivity implements
		OnClickListener {
	private static final String TAG = "CompleteProfileActivity";
	ImageView ivHeadPortrait;
	EditText etName;
	EditText etBirthday;
	EditText etHeight;
	ImageView ivMale;
	ImageView ivFemale;
	EditText etWeight;
	ProfileDao dao = ProfileDao.getInstance();
	Profile info = new Profile();
	boolean sex = true;
	Calendar mCalendar;
	int curr_year;
	int curr_month;
	int curr_day;
	String imgbm;
	boolean isEdit = false;
	ProgressDialog proDialog;

	static {
		Kii.initialize(Constants.APP_ID, Constants.APP_KEY, Site.CN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WeightRecordApplication.getInstance().addActivity(this);

		setActionBar();

		setContentView(R.layout.complete_profile_layout);

		isEdit = getIntent().getBooleanExtra("isEdit", false);

		initView();
		if (isEdit) {
			SharedPreferences sp = getSharedPreferences(Constants.KIIUSER,
					MODE_PRIVATE);
			String userName = sp.getString(Constants.USERNAME, "");
			info = ProfileDao.getInstance().getProfile(this, userName);
			if (info.name != null) {
				setValue(info);
			}
			etWeight.setVisibility(View.GONE);
		} else {
			etWeight.setVisibility(View.VISIBLE);
		}
	}

	private void initView() {

		ivHeadPortrait = (ImageView) findViewById(R.id.iv_thumbnail);
		etName = (EditText) findViewById(R.id.et_name);
		etBirthday = (EditText) findViewById(R.id.et_birthday);
		etHeight = (EditText) findViewById(R.id.et_high);
		ivMale = (ImageView) findViewById(R.id.iv_male);
		ivFemale = (ImageView) findViewById(R.id.iv_female);
		etWeight = (EditText) findViewById(R.id.et_weight);

		etWeight.setFocusable(false);
		etWeight.setOnClickListener(this);
		etHeight.setFocusable(false);
		etHeight.setOnClickListener(this);
		etBirthday.setFocusable(false);
		ivHeadPortrait.setOnClickListener(this);
		etBirthday.setOnClickListener(this);
		ivMale.setOnClickListener(this);
		ivFemale.setOnClickListener(this);

	}

	private void setValue(Profile profile) {
		if (profile.image != null && profile.image.length() > 0) {
			Bitmap bm = ImagesUtils.locDecodeImage(profile.image);
			bm = ImagesUtils.toRoundBitmap(bm);
			ivHeadPortrait.setImageBitmap(bm);
		} else {
			ivHeadPortrait.setImageResource(R.drawable.default_thumbnail);
		}

		etName.setText(profile.name);

		try {
			String[] date = profile.birthday.split("-");
			String birthday = "";
			if (date.length == 3) {
				birthday = date[0] + getString(R.string.year) + date[1]
						+ getString(R.string.month) + date[2]
						+ getString(R.string.day);
			}

			etBirthday.setText(birthday);
		} catch (Exception e) {
		}

		SharedPreferences sp = getSharedPreferences("profile", MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("target", profile.birthday);
		editor.commit();

		String height = "";
		String weight = "";
		if (profile.unit != null && profile.unit.equals(Constants.IMPERIAL)) {
			height = profile.height + getString(R.string.imperial_height);
			weight = profile.weight + getString(R.string.imperial_weight);
		} else {
			height = profile.height + getString(R.string.metric_height);
			weight = profile.weight + getString(R.string.metric_weight);
		}
		etHeight.setText(height);
		etWeight.setText(weight);

		if (profile.sex != null && profile.sex.equals(getString(R.string.male))) {
			sex = true;
			ivMale.setImageResource(R.drawable.male_tab_selected);
			ivFemale.setImageResource(R.drawable.female_tab);
		} else {
			sex = false;
			ivMale.setImageResource(R.drawable.male_tab);
			ivFemale.setImageResource(R.drawable.female_tab_selected);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_thumbnail:
			CommUtils.showPhoto(this);
			break;
		case R.id.et_birthday:
			showDateDialog();
			break;
		case R.id.iv_male:
			sex = true;
			ivMale.setImageResource(R.drawable.male_tab_selected);
			ivFemale.setImageResource(R.drawable.female_tab);
			break;
		case R.id.iv_female:
			sex = false;
			ivFemale.setImageResource(R.drawable.female_tab_selected);
			ivMale.setImageResource(R.drawable.male_tab);
			break;
		case R.id.et_high:
			showHeightDialog();
			break;
		case R.id.et_weight:
			showWeightDialog();
			break;
		default:
			break;
		}
	}

	public void showDateDialog() {
		mCalendar = Calendar.getInstance();
		curr_year = mCalendar.get(Calendar.YEAR);
		curr_month = mCalendar.get(Calendar.MONTH);
		curr_day = mCalendar.get(Calendar.DATE);
		DialogFragment newFragment = new DateDialog(curr_year, curr_month,
				curr_day);
		newFragment.show(getFragmentManager(), "dialog");
	}

	class DateDialog extends DialogFragment implements OnDateSetListener {
		int year;
		int month;
		int day;

		public DateDialog(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@SuppressWarnings("static-access")
		public void onDateSet(DatePicker view, int year, int month, int day) {
			String birthday = year + getString(R.string.year) + (month + 1)
					+ getString(R.string.month) + day + getString(R.string.day);
			SharedPreferences sp = getActivity().getSharedPreferences(
					"profile", getActivity().MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("birthday", birthday);
			editor.putString("target", year + "-" + (month + 1) + "-" + day);
			editor.commit();
			handler.sendEmptyMessage(0);
		}

	}

	public void showHeightDialog() {
		String nowNumber = "0.0";
		String number = etHeight.getText().toString();

		if (!number.equals("")) {
			number = number.substring(0, number.length() - 1);
			nowNumber = number;
		}

		String unit = "";
		if (info.unit != null && info.unit.equals(Constants.IMPERIAL)) {
			unit = getString(R.string.imperial_height);
		} else {
			unit = getString(R.string.metric_height);
		}
		DialogFragment newFragment = new HeightPickerDialog(this,
				new HeightPickerDialog.OnMyNumberSetListener() {
					@Override
					public void onNumberSet(String number, String mode) {
						etHeight.setText("");
						etHeight.setText(number + mode);
						info.height = Double.parseDouble(number);
					}
				}, nowNumber, unit);
		newFragment.show(getFragmentManager(), "dialog");

	}

	public void showWeightDialog() {
		String nowNumber = "0.0";
		String number = etWeight.getText().toString();

		if (!number.equals("")) {
			number = number.substring(0, number.length() - 2);
			nowNumber = number;
		}
		String unit = "";
		if (info.unit != null && info.unit.equals(Constants.IMPERIAL)) {
			unit = getString(R.string.imperial_weight);
		} else {
			unit = getString(R.string.metric_weight);
		}
		DialogFragment newFragment = new HeightPickerDialog(this,
				new HeightPickerDialog.OnMyNumberSetListener() {
					@Override
					public void onNumberSet(String number, String mode) {
						etWeight.setText("");
						etWeight.setText(number + mode);
						info.weight = Double.parseDouble(number);
					}
				}, nowNumber, unit);
		newFragment.show(getFragmentManager(), "dialog");

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			SharedPreferences sp = getSharedPreferences("profile", MODE_PRIVATE);
			String birthday = sp.getString("birthday", "");
			etBirthday.setText(birthday);

		};
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.GALLERY:
			if (data != null) {
				Uri uri = data.getData();
				Log.d(TAG, "uri: " + uri.toString() + ",Media.uri: "
						+ Media.EXTERNAL_CONTENT_URI.toString());
				String columns[] = new String[] { Media.DATA };
				Cursor cursor = getContentResolver().query(uri, columns, null,
						null, null);
				while (cursor.moveToNext()) {
					// 图库中图片路径
					imgbm = cursor.getString(cursor.getColumnIndex(Media.DATA));
				}

				if (cursor != null) {
					cursor.close();
				}
				if (imgbm != null) {
					Bitmap photo = ImagesUtils.decodeImage(imgbm);
					if (photo != null) {
						byte[] photos = ImagesUtils.convertImageToByte(photo);
						String path = imgbm
								.substring(imgbm.lastIndexOf("/") + 1);
						File localFile = ImagesUtils.getFileFromBytes(photos,
								Constants.DOWNLOAD_PHOTO_PATH + "" + path);

						info.image = localFile.getAbsolutePath();
						photo = ImagesUtils.toRoundBitmap(photo);
						ivHeadPortrait.setImageBitmap(photo);
					}
				}
			}
			break;

		case Constants.TAKE_A_PICTURE:
			// 图库中图片路径
			imgbm = CommUtils.mCurrentPhotoFile.getAbsolutePath();
			if (imgbm != null) {
				Bitmap photo = ImagesUtils.decodeImage(imgbm);
				if (photo != null) {
					byte[] photos = ImagesUtils.convertImageToByte(photo);
					String path = imgbm.substring(imgbm.lastIndexOf("/") + 1);
					File localFile = ImagesUtils.getFileFromBytes(photos,
							Constants.DOWNLOAD_PHOTO_PATH + "" + path);

					info.image = localFile.getAbsolutePath();
					photo = ImagesUtils.toRoundBitmap(photo);
					ivHeadPortrait.setImageBitmap(photo);
				}
			}
			break;
		}
	}

	private void setActionBar() {
		final LayoutInflater inflater = (LayoutInflater) getActionBar()
				.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = inflater.inflate(
				R.layout.actionbar_custom_view_done_cancel, null);
		customActionBarView.findViewById(R.id.actionbar_done)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						SharedPreferences sp = getSharedPreferences(
								Constants.KIIUSER, MODE_PRIVATE);
						String token = sp.getString(Constants.TOKEN, "");
						info.name = etName.getText().toString().trim();
						sp = getSharedPreferences("profile", MODE_PRIVATE);
						String birthday = sp.getString("target", "");
						info.birthday = birthday;

						if (sex) {
							info.sex = getString(R.string.male);
						} else {
							info.sex = getString(R.string.female);
						}

						if (info.name.equals("") || info.name == null) {
							etName.setError(getString(R.string.name_error));
							etName.requestFocus();
						} else if (info.birthday.equals("")
								|| info.birthday == null) {
							CommUtils.showToast(CompleteProfileActivity.this,
									getString(R.string.birthday_error));
						} else if (info.height == 0.0) {
							CommUtils.showToast(CompleteProfileActivity.this,
									getString(R.string.weight_error));
						} else {

							if (isEdit) {
								int rowId = dao.updateProfile(
										CompleteProfileActivity.this, info);
								if (rowId > 0) {
									proDialog = new ProgressDialog(
											CompleteProfileActivity.this);
									proDialog.show();
									new KiiUserTask(
											CompleteProfileActivity.this, info,
											KiiUser.getCurrentUser().toUri(),
											proDialog).execute();
								} else {
									CommUtils.showToast(
											CompleteProfileActivity.this,
											getString(R.string.save_failure));
								}
							} else {

								if (info.weight == 0.0) {
									CommUtils.showToast(
											CompleteProfileActivity.this,
											getString(R.string.height_error));
								}

								KiiUser.loginWithToken(new KiiUserCallBack() {
									@Override
									public void onLoginCompleted(int token,
											KiiUser user, Exception exception) {
										super.onLoginCompleted(token, user,
												exception);

										if (user.isEmailVerified()) {

											info.unit = Constants.METRIC;
											info.userName = user.getUsername();
											if (dao.insert(
													CompleteProfileActivity.this,
													info)) {
												proDialog = new ProgressDialog(
														CompleteProfileActivity.this);
												proDialog.show();
												new KiiUserTask(
														CompleteProfileActivity.this,
														info,
														KiiUser.getCurrentUser()
																.toUri(),
														proDialog).execute();

											}
										} else {
											AlertDialog.Builder dialog = new AlertDialog.Builder(
													CompleteProfileActivity.this);
											dialog.setTitle(getString(R.string.verify_email));
											dialog.setMessage(getString(R.string.verify_message));
											dialog.setNegativeButton(
													getString(R.string.cancel),
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.dismiss();
														}
													});

											dialog.setNeutralButton(
													getString(R.string.return_login),
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.dismiss();
															CompleteProfileActivity.this
																	.finish();
														}
													});

											dialog.setPositiveButton(
													getString(R.string.send_email),
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															KiiUser.requestResendEmailVerification(new KiiUserCallBack() {
																public void onRequestResendEmailVerificationCodeCompleted(
																		int token,
																		Exception exception) {
																	if (exception != null) {
																		Log.i("onRequestResendEmailVerificationCodeCompleted",
																				"--"
																						+ exception
																								.getMessage());
																	}

																};
															});

														}

													});
											dialog.show();
										}

									}
								}, token);

							}
						}
					}
				});
		customActionBarView.findViewById(R.id.actionbar_cancel)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CommUtils.showAlertDialog(CompleteProfileActivity.this);
					}
				});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.barColor));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView,
				new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
	}
}
