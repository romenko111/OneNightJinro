package jp.romerome.onenightjinro;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegistPlayerActivity extends Activity implements OnClickListener
{

	private static final int ID_BTN_DELETE = -3;

	private LinearLayout mPlayerListLayout;
	private EditText mEditText;
	private LayoutInflater mInflater;

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
		mEditText = (EditText) findViewById(R.id.edit_playerName);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_add:
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
				break;

			case R.id.btn_start:
				if(mPlayerListLayout.getChildCount() < 3)
				{
					Toast.makeText(this, "3人から", Toast.LENGTH_SHORT).show();
					break;
				}

				ArrayList<Player> mPlayers = new ArrayList<Player>();
				for(int i=0;i<mPlayerListLayout.getChildCount();i++)
				{
					TextView tv_name =  (TextView) mPlayerListLayout.getChildAt(i).findViewById(R.id.tv_name);
					mPlayers.add(new Player(tv_name.getText().toString()));
				}
				//------- jobmap ---------
				HashMap<Integer, Integer> jobMap = new HashMap<Integer, Integer>();
				jobMap.put(Player.JOB_JINRO, 2);
				jobMap.put(Player.JOB_AUGUR, 1);
				jobMap.put(Player.JOB_THIEF, 1);
				jobMap.put(Player.JOB_VILLAGER,
						mPlayers.size() + 2 - 4);
				//------------------------
				Intent intent = new Intent(RegistPlayerActivity.this,GameActivity.class);
				intent.putExtra("Players", mPlayers);
				intent.putExtra("jobMap", jobMap);
				startActivity(intent);
				finish();
				break;

			case ID_BTN_DELETE:
				mPlayerListLayout.removeView((View)v.getParent());
				break;

			default:
				break;
		}
	}

}
