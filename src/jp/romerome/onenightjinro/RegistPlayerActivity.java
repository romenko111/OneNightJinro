package jp.romerome.onenightjinro;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RegistPlayerActivity extends Activity implements OnClickListener,OnSeekBarChangeListener
{

	private static final int ID_BTN_DELETE = -3;
	private static final int DIALOG_JOBEDIT = 0;

	private LinearLayout mPlayerListLayout;
	private EditText mEditText;
	private LayoutInflater mInflater;
	private Dialog mJobEditDialog;
	private SeekBar mSeekJinro;
	private SeekBar mSeekAugur;
	private SeekBar mSeekThief;
	private SeekBar mSeekLunatic;
	private SeekBar mSeekTeruteru;
	private SeekBar mSeekVillager;
	private TextView mTextJinro;
	private TextView mTextAugur;
	private TextView mTextThief;
	private TextView mTextLunatic;
	private TextView mTextTeruteru;
	private TextView mTextVillager;
	private int mOldJinro;
	private int mOldAugur;
	private int mOldThief;
	private int mOldLunatic;
	private int mOldTeruteru;
	private int mOldVillager;
	private boolean mJobEditFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		setContentView(R.layout.activity_player_regist);
		mPlayerListLayout = (LinearLayout) findViewById(R.id.playerList);
		((Button)findViewById(R.id.btn_add)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_start)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_jobEdit)).setOnClickListener(this);
		mEditText = (EditText) findViewById(R.id.edit_playerName);

		createDialog();
	}

	private void createDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = mInflater.inflate(R.layout.layout_dialog_jobedit, null);

		mSeekJinro = (SeekBar) view.findViewById(R.id.seek_jinro);
		mSeekAugur = (SeekBar) view.findViewById(R.id.seek_augur);
		mSeekThief = (SeekBar) view.findViewById(R.id.seek_thief);
		mSeekLunatic = (SeekBar) view.findViewById(R.id.seek_lunatic);
		mSeekTeruteru = (SeekBar) view.findViewById(R.id.seek_teruteru);
		mSeekVillager = (SeekBar) view.findViewById(R.id.seek_villager);

		mSeekJinro.setOnSeekBarChangeListener(this);
		mSeekAugur.setOnSeekBarChangeListener(this);
		mSeekThief.setOnSeekBarChangeListener(this);
		mSeekLunatic.setOnSeekBarChangeListener(this);
		mSeekTeruteru.setOnSeekBarChangeListener(this);
		mSeekVillager.setOnSeekBarChangeListener(this);

		mTextJinro =  (TextView) view.findViewById(R.id.tv_num_jinro);
		mTextAugur =  (TextView) view.findViewById(R.id.tv_num_augur);
		mTextThief =  (TextView) view.findViewById(R.id.tv_num_thief);
		mTextLunatic = (TextView) view.findViewById(R.id.tv_num_lunatic);
		mTextTeruteru = (TextView) view.findViewById(R.id.tv_num_teruteru);
		mTextVillager = (TextView) view.findViewById(R.id.tv_num_villager);

		view.findViewById(R.id.btn_ok).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
		builder.setView(view);
		mJobEditDialog = builder.create();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_add:
				add();
				break;

			case R.id.btn_start:
				start();
				break;

			case ID_BTN_DELETE:
				mPlayerListLayout.removeView((View)v.getParent());
				break;

			case R.id.btn_jobEdit:
				jobEdit();
				break;

			case R.id.btn_ok:
				ok();
				break;

			case R.id.btn_cancel:
				cancel();
				break;

			default:
				break;
		}
	}

	private void start()
	{
		if(mPlayerListLayout.getChildCount() < 3)
		{
			Toast.makeText(this, "3人から", Toast.LENGTH_SHORT).show();
			return;
		}

		if(mJobEditFlag)
		{
			int jobsNum = mSeekJinro.getProgress() +
							mSeekAugur.getProgress() +
							mSeekThief.getProgress() +
							mSeekLunatic.getProgress() +
							mSeekTeruteru.getProgress() +
							mSeekVillager.getProgress();
			if(jobsNum - 2 != mPlayerListLayout.getChildCount())
			{
				Toast.makeText(this, "人数が合いません", Toast.LENGTH_SHORT).show();
				return;
			}
		}


		ArrayList<Player> mPlayers = new ArrayList<Player>();
		for(int i=0;i<mPlayerListLayout.getChildCount();i++)
		{
			TextView tv_name =  (TextView) mPlayerListLayout.getChildAt(i).findViewById(R.id.tv_name);
			mPlayers.add(new Player(tv_name.getText().toString()));
		}
		//------- jobmap ---------
		HashMap<Integer, Integer> jobMap = new HashMap<Integer, Integer>();
		if(mJobEditFlag)
		{
			jobMap.put(Player.JOB_JINRO, mSeekJinro.getProgress());
			jobMap.put(Player.JOB_AUGUR, mSeekAugur.getProgress());
			jobMap.put(Player.JOB_THIEF, mSeekThief.getProgress());
			jobMap.put(Player.JOB_VILLAGER,mSeekVillager.getProgress());
			jobMap.put(Player.JOB_LUNATIC,mSeekLunatic.getProgress());
			jobMap.put(Player.JOB_TERUTERU,mSeekTeruteru.getProgress());
		}
		else
		{
			jobMap.put(Player.JOB_JINRO, 2);
			jobMap.put(Player.JOB_AUGUR, 1);
			jobMap.put(Player.JOB_THIEF, 1);
			jobMap.put(Player.JOB_VILLAGER,mPlayers.size() + 2 - 4);
			jobMap.put(Player.JOB_LUNATIC,0);
			jobMap.put(Player.JOB_TERUTERU,0);
		}
		//------------------------
		Intent intent = new Intent(RegistPlayerActivity.this,GameActivity.class);
		intent.putExtra("Players", mPlayers);
		intent.putExtra("jobMap", jobMap);
		startActivity(intent);
		finish();
	}

	private void add()
	{
		String name = mEditText.getText().toString();
		if(!name.equals(""))
		{
			mEditText.setText("");
			View view = mInflater.inflate(R.layout.layout_actionrow, null);
			TextView tv = (TextView) view.findViewById(R.id.tv_name);
			tv.setText(name);
			Button btn = (Button) view.findViewById(R.id.btn_action);
			btn.setText("削除");
			btn.setId(ID_BTN_DELETE);
			btn.setOnClickListener(this);
			mPlayerListLayout.addView(view);
		}
	}

	private void jobEdit()
	{
		int playerNum = mPlayerListLayout.getChildCount();
		if(!mJobEditFlag)
		{
			if(playerNum >= 3)
			{
				mSeekJinro.setProgress(2);
				mSeekAugur.setProgress(1);
				mSeekThief.setProgress(1);
				mSeekVillager.setProgress(playerNum + 2 - 4);
				mSeekLunatic.setProgress(0);
				mSeekTeruteru.setProgress(0);
			}
			mJobEditFlag = true;
		}
		mSeekJinro.setMax(playerNum + 2);
		mSeekAugur.setMax(playerNum + 2);
		mSeekThief.setMax(playerNum + 2);
		mSeekLunatic.setMax(playerNum + 2);
		mSeekTeruteru.setMax(playerNum + 2);
		mSeekVillager.setMax(playerNum + 2);
		mOldJinro = mSeekJinro.getProgress();
		mOldAugur = mSeekAugur.getProgress();
		mOldThief = mSeekThief.getProgress();
		mOldLunatic = mSeekLunatic.getProgress();
		mOldTeruteru = mSeekTeruteru.getProgress();
		mOldVillager = mSeekVillager.getProgress();
		mJobEditDialog.show();
	}

	private void ok()
	{
		if(mJobEditDialog != null && mJobEditDialog.isShowing())
		{
			int totalJobs = mSeekJinro.getProgress() +
							mSeekAugur.getProgress() +
							mSeekThief.getProgress() +
							mSeekLunatic.getProgress() +
							mSeekTeruteru.getProgress() +
							mSeekVillager.getProgress();
			if(totalJobs - 2 == mPlayerListLayout.getChildCount())
			{
				mJobEditDialog.dismiss();
			}
			else
			{
				Toast.makeText(this, "人数が合いません", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void cancel()
	{
		if(mJobEditDialog != null && mJobEditDialog.isShowing())
		{
			mJobEditDialog.dismiss();
			mSeekJinro.setProgress(mOldJinro);
			mSeekAugur.setProgress(mOldAugur);
			mSeekThief.setProgress(mOldThief);
			mSeekVillager.setProgress(mOldVillager);
			mSeekLunatic.setProgress(mOldLunatic);
			mSeekTeruteru.setProgress(mOldTeruteru);
		}
	}



	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser)
	{
		switch (seekBar.getId())
		{
			case R.id.seek_jinro:
				mTextJinro.setText(progress +"");
				break;

			case R.id.seek_augur:
				mTextAugur.setText(progress +"");
				break;

			case R.id.seek_thief:
				mTextThief.setText(progress +"");
				break;

			case R.id.seek_lunatic:
				mTextLunatic.setText(progress +"");
				break;

			case R.id.seek_teruteru:
				mTextTeruteru.setText(progress +"");
				break;

			case R.id.seek_villager:
				mTextVillager.setText(progress +"");
				break;

			default:
				break;
		}
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{

	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{

	}

}
