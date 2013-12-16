package jp.romerome.onenightjinro;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

public class GameActivity extends Activity
{

	private final static int DIALOG_BACK = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		ArrayList<Player> players = (ArrayList<Player>) getIntent().getSerializableExtra("Players");
		HashMap<Integer, Integer> jobMap = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("jobMap");
		GameManagement gameManagement = new GameManagement(this, this);
		gameManagement.start(players,jobMap);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_BACK:
				showDialog(DIALOG_BACK);
				return true;

			default:
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case DIALOG_BACK:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("ゲームを終了しますか？");
				builder.setPositiveButton("はい", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						GameActivity.this.finish();
					}
				});
				builder.setNegativeButton("いいえ", null);
				return builder.create();

			default:
				break;
		}
		return super.onCreateDialog(id);
	}


}
