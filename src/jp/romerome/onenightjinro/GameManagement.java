package jp.romerome.onenightjinro;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class GameManagement implements OnClickListener
{

	private final static String BR = System.getProperty("line.separator");

	private final static int STATE_NONE = -1;
	private final static int STATE_OPENJOB = 0;
	private final static int STATE_CONFIRM_OPENJOB = 1;
	private final static int STATE_CONFIRM_VOTE = 2;
	private final static int STATE_VOTE = 3;
	private final static int STATE_RESULT = 4;
	private final static int STATE_DISCUSSION = 5;

	private Context mContext;
	private Activity mGameActivity;
	private LayoutInflater mInflater;
	private View mJobOpenView;
	private JobOpenHolder mJobOpenHolder;
	private View mVoteView;
	private VoteHolder mVoteHolder;
	private View mResultView;
	private ResultHolder mResultHolder;
	private View mConfirmView;
	private ConfirmHolder mConfirmHolder;
	private View mDiscussionView;
	private DiscussionHolder mDiscussionHolder;
	private AlertDialog mConfirmDialog;
	private AlertDialog mResultDialog;
	private Drawable mJinroDrawable;
	private Drawable mVillagerDrawable;
	private Drawable mAugurDrawable;
	private Drawable mThiefDrawable;
	private Drawable mLunaticDrawable;
	private Drawable mTeruteruDrawable;
	HashMap<Integer, Integer> mJobMap;

	private ArrayList<Player> mPlayers;
	private int[] mLeftCards;
	private int who = -1;
	private int mToVote = -1;
	private int state = STATE_NONE;

	public GameManagement(Context context,Activity gameActivity)
	{
		mContext = context;
		mGameActivity = gameActivity;
		mLeftCards = new int[2];

		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mJobOpenView = mInflater.inflate(R.layout.layout_jobopen, null);
		mJobOpenHolder = new JobOpenHolder(mJobOpenView);
		mVoteView = mInflater.inflate(R.layout.layout_vote, null);
		mVoteHolder = new VoteHolder(mVoteView);
		mResultView = mInflater.inflate(R.layout.layout_result, null);
		mResultHolder = new ResultHolder(mResultView);
		mConfirmView = mInflater.inflate(R.layout.layout_confirm, null);
		mConfirmHolder = new ConfirmHolder(mConfirmView);
		mDiscussionView = mInflater.inflate(R.layout.layout_discussion, null);
		mDiscussionHolder = new DiscussionHolder(mDiscussionView);

		mJinroDrawable = mContext.getResources().getDrawable(R.drawable.jinro);
		mVillagerDrawable = mContext.getResources().getDrawable(R.drawable.villager);
		mAugurDrawable = mContext.getResources().getDrawable(R.drawable.augur);
		mThiefDrawable = mContext.getResources().getDrawable(R.drawable.thief);
		mLunaticDrawable = mContext.getResources().getDrawable(R.drawable.lunatic);
		mTeruteruDrawable = mContext.getResources().getDrawable(R.drawable.teruteru);

		createDialog();
	}

	public void createDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setPositiveButton("はい", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				mConfirmDialog.dismiss();
				if(state == STATE_VOTE)
				{
					mPlayers.get(mToVote).incVotes();
				}
				GameManagement.this.next();
			}
		});
		builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				mConfirmDialog.dismiss();
			}
		});
		mConfirmDialog = builder.create();

		builder = new AlertDialog.Builder(mContext);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				mResultDialog.dismiss();
				GameManagement.this.next();
			}
		});
		builder.setCancelable(false);
		mResultDialog = builder.create();
	}

	public void start(ArrayList<Player> players,HashMap<Integer, Integer> jobMap)
	{
		mResultHolder.playerListLayout.removeAllViews();
		mPlayers = players;
		mJobMap = jobMap;
		for (Player player : mPlayers)
		{
			player.resetJob();
			player.resetVotes();
		}
		mLeftCards[0] = Player.JOB_NONE;
		mLeftCards[1] = Player.JOB_NONE;
		distributeJobs();
		who = 0;
		state = STATE_CONFIRM_OPENJOB;
		mGameActivity.setContentView(mConfirmView);
		mConfirmHolder.tv_name.setText(mPlayers.get(who).getName());
	}

	private void distributeJobs()
	{
		//----- 人数決定 -----
		int[] jobNum = new int[Player.JOB_NUM];
		jobNum[Player.JOB_JINRO] = mJobMap.get(Player.JOB_JINRO);
		jobNum[Player.JOB_AUGUR] = mJobMap.get(Player.JOB_AUGUR);
		jobNum[Player.JOB_THIEF] = mJobMap.get(Player.JOB_THIEF);
		jobNum[Player.JOB_VILLAGER] = mJobMap.get(Player.JOB_VILLAGER);
		jobNum[Player.JOB_LUNATIC] = mJobMap.get(Player.JOB_LUNATIC);
		jobNum[Player.JOB_TERUTERU] = mJobMap.get(Player.JOB_TERUTERU);
		//--------------------

		for (Player player : mPlayers)
		{
			while(player.getFirstJob() == Player.JOB_NONE)
			{
				int job = (int)(Math.random() * Player.JOB_NUM);
				if(jobNum[job] > 0)
				{
					player.setFirstJob(job);
					jobNum[job]--;
				}
			}
		}
		for (int i = 0;i<jobNum.length;i++)
		{
			if(jobNum[i] > 0)
			{
				mLeftCards[0] = i;
				jobNum[i]--;
				break;
			}
		}
		for (int i = 0;i<jobNum.length;i++)
		{
			if(jobNum[i] > 0)
			{
				mLeftCards[1] = i;
				jobNum[i]--;
				break;
			}
		}
	}

	private void next()
	{
		switch (state)
		{
			case STATE_CONFIRM_OPENJOB:
				confirmOpenJob();
				break;

			case STATE_OPENJOB:
				openJob();
				break;

			case STATE_DISCUSSION:
				discussion();
				break;

			case STATE_CONFIRM_VOTE:
				confirmVote();
				break;

			case STATE_VOTE:
				vote();
				break;

			default:
				break;
		}
	}

	private void confirmOpenJob()
	{
		mJobOpenHolder.tv_job.setText("あなたは" +
				mPlayers.get(who).getFirstJobName() +
								"です。");
		mJobOpenHolder.tv_msg.setText("");
		if(mPlayers.get(who).getFirstJob() == Player.JOB_JINRO)
		{
			String msg = "";
			for(int i=0;i<mPlayers.size();i++)
			{
				if(i != who && mPlayers.get(i).getFirstJob() == Player.JOB_JINRO)
				{
					msg += mPlayers.get(i).getName() + "さんも" +
							mPlayers.get(i).getFirstJobName() + "です。";
				}
			}
			mJobOpenHolder.tv_msg.setText(msg);
		}
		mJobOpenHolder.actionListLayout.removeAllViews();
		String actionMsg = "";
		switch (mPlayers.get(who).getFirstJob())
		{
			case Player.JOB_JINRO:
				actionMsg = "疑う";
				mJobOpenHolder.iv_job.setImageDrawable(mJinroDrawable);
				break;

			case Player.JOB_VILLAGER:
				actionMsg = "疑う";
				mJobOpenHolder.iv_job.setImageDrawable(mVillagerDrawable);
				break;

			case Player.JOB_AUGUR:
				actionMsg = "占う";
				mJobOpenHolder.iv_job.setImageDrawable(mAugurDrawable);
				break;

			case Player.JOB_THIEF:
				actionMsg = "交換";
				mJobOpenHolder.iv_job.setImageDrawable(mThiefDrawable);
				break;

			case Player.JOB_LUNATIC:
				actionMsg="疑う";
				mJobOpenHolder.iv_job.setImageDrawable(mLunaticDrawable);
				break;

			case Player.JOB_TERUTERU:
				actionMsg="疑う";
				mJobOpenHolder.iv_job.setImageDrawable(mTeruteruDrawable);
				break;

			default:
				break;
		}
		for (int i=0;i<mPlayers.size();i++)
		{
			if(mPlayers.get(who).getFirstJob() == Player.JOB_THIEF)
			{
				View view = mInflater.inflate(R.layout.layout_actionrow, null);
				((TextView)view.findViewById(R.id.tv_name)).setText(mPlayers.get(i).getName());
				Button btn = (Button) view.findViewById(R.id.btn_action);
				if(i != who)
					btn.setText(actionMsg);
				else
					btn.setText("交換しない");
				btn.setOnClickListener(this);
				mJobOpenHolder.actionListLayout.addView(view);
			}
			else if(mPlayers.get(who).getFirstJob() == Player.JOB_AUGUR)
			{
				if(i != who)
				{
					View view = mInflater.inflate(R.layout.layout_actionrow, null);
					((TextView)view.findViewById(R.id.tv_name)).setText(mPlayers.get(i).getName());
					Button btn = (Button) view.findViewById(R.id.btn_action);
					btn.setText(actionMsg);
					btn.setOnClickListener(this);
					mJobOpenHolder.actionListLayout.addView(view);
				}
				if(i == mPlayers.size() -1)
				{
					View view = mInflater.inflate(R.layout.layout_actionrow, null);
					((TextView)view.findViewById(R.id.tv_name)).setText("残りの2枚");
					Button btn = (Button) view.findViewById(R.id.btn_action);
					btn.setText(actionMsg);
					btn.setOnClickListener(this);
					mJobOpenHolder.actionListLayout.addView(view);
				}
			}
			else if(i != who)
			{
				View view = mInflater.inflate(R.layout.layout_actionrow, null);
				((TextView)view.findViewById(R.id.tv_name)).setText(mPlayers.get(i).getName());
				Button btn = (Button) view.findViewById(R.id.btn_action);
				btn.setText(actionMsg);
				btn.setOnClickListener(this);
				mJobOpenHolder.actionListLayout.addView(view);
			}
		}
		mGameActivity.setContentView(mJobOpenView);
		state = STATE_OPENJOB;
	}

	private void openJob()
	{
		mJobOpenHolder.scrollView.scrollTo(0, 0);
		if(who == mPlayers.size() - 1)
		{
			//議論へ
			mGameActivity.setContentView(mDiscussionView);
			state = STATE_DISCUSSION;
		}
		else
		{
			who++;
			mConfirmHolder.tv_name.setText(mPlayers.get(who).getName());
			mGameActivity.setContentView(mConfirmView);
			state = STATE_CONFIRM_OPENJOB;
		}
	}

	private void discussion()
	{
		who = 0;
		mConfirmHolder.tv_name.setText(mPlayers.get(who).getName());
		mGameActivity.setContentView(mConfirmView);
		state = STATE_CONFIRM_VOTE;
	}

	private void confirmVote()
	{
		mVoteHolder.playerListLayout.removeAllViews();
		for (int i=0;i<mPlayers.size();i++)
		{
			if(i != who)
			{
				View view = mInflater.inflate(R.layout.layout_actionrow, null);
				((TextView)view.findViewById(R.id.tv_name)).setText(mPlayers.get(i).getName());
				Button btn = (Button) view.findViewById(R.id.btn_action);
				btn.setText("投票");
				btn.setOnClickListener(this);
				mVoteHolder.playerListLayout.addView(view);
			}
		}
		mGameActivity.setContentView(mVoteView);
		state = STATE_VOTE;
	}

	private void vote()
	{
		mVoteHolder.scrollView.scrollTo(0, 0);
		if(who == mPlayers.size() - 1)
		{
			//結果へ
			createResultView();
			mGameActivity.setContentView(mResultView);
			state = STATE_RESULT;
		}
		else
		{
			who++;
			mConfirmHolder.tv_name.setText(mPlayers.get(who).getName());
			mGameActivity.setContentView(mConfirmView);
			state = STATE_CONFIRM_VOTE;
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_ok:
			case R.id.btn_toVote:
				next();
				break;

			case R.id.btn_toTop:
				mGameActivity.finish();
				break;

			case R.id.btn_again:
				start(mPlayers,mJobMap);
				break;

			case R.id.btn_action:
				LinearLayout parent = (LinearLayout) v.getParent();
				int i;
				if(state == STATE_OPENJOB)
				{
					//よる時間
					for(i=0;i<mJobOpenHolder.actionListLayout.getChildCount();i++)
					{
						if(mJobOpenHolder.actionListLayout.getChildAt(i) == parent)
						{
							break;
						}
					}
					switch (mPlayers.get(who).getFirstJob())
					{
						case Player.JOB_JINRO:
						case Player.JOB_VILLAGER:
						case Player.JOB_LUNATIC:
						case Player.JOB_TERUTERU:
							if(i < who)
							{
								mConfirmDialog.setMessage(mPlayers.get(i).getName() +"さんを疑いますか？");
							}
							else
							{
								mConfirmDialog.setMessage(mPlayers.get(i+1).getName() +"さんを疑いますか？");
							}
							mConfirmDialog.show();
							break;

						case Player.JOB_AUGUR:
							if(i == mPlayers.size() -1)
							{
								mResultDialog.setMessage(
										Player.getJobName(mLeftCards[0]) + "と" +
										Player.getJobName(mLeftCards[1]) + "です。");
							}
							else if(i < who)
							{
								mResultDialog.setMessage(mPlayers.get(i).getName() +"さんは" + mPlayers.get(i).getFirstJobName() + "です。");
							}
							else
							{
								mResultDialog.setMessage(mPlayers.get(i+1).getName() +"さんは" + mPlayers.get(i+1).getFirstJobName() + "です。");
							}
							mResultDialog.show();
							break;

						case Player.JOB_THIEF:
							if(i == who)
								mResultDialog.setMessage("ｴｯ(ﾟДﾟ≡ﾟДﾟ)ﾏｼﾞ?");
							else
							{
								mPlayers.get(who).setSecondJob(mPlayers.get(i).getFirstJob());
								mPlayers.get(i).setSecondJob(mPlayers.get(who).getFirstJob());
								mResultDialog.setMessage(mPlayers.get(i).getName() +"さんは" + mPlayers.get(i).getFirstJobName() + "でした。");
							}
							mResultDialog.show();
							break;

						default:
							break;
					}
				}
				else if(state == STATE_VOTE)
				{
					//投票
					for(i=0;i<mVoteHolder.playerListLayout.getChildCount();i++)
					{
						if(mVoteHolder.playerListLayout.getChildAt(i) == parent)
						{
							break;
						}
					}
					if(i < who)
					{
						mConfirmDialog.setMessage(mPlayers.get(i).getName() +"さんに投票しますか？");
						mToVote = i;
					}
					else
					{
						mConfirmDialog.setMessage(mPlayers.get(i+1).getName() +"さんに投票しますか？");
						mToVote = i+1;
					}
					mConfirmDialog.show();
				}
				break;

			default:
				break;
		}
	}

	private void createResultView()
	{
		ArrayList<Integer> executionPlayer = whoExecution();
		int winnerJob = getWinnerJob(executionPlayer);
		String result = "";
		switch (executionPlayer.size())
		{
			case 0:
				result = "投票の結果、誰も処刑されませんでした。" + BR
						+ Player.getJobName(winnerJob) + "陣営の勝利です。";
				break;

			case 1:
				result = "投票の結果、" + mPlayers.get(executionPlayer.get(0)).getName() +"さんが処刑されました。" + BR
						+ Player.getJobName(winnerJob) + "陣営の勝利です。";
				break;

			default:
				String name = "";
				for(int i=0;i<executionPlayer.size()-1;i++)
				{
					name += mPlayers.get(executionPlayer.get(i)).getName() + "さんと";
				}
				name += mPlayers.get(executionPlayer.get(executionPlayer.size()-1)).getName() + "さん";
				result = "投票の結果、" + name + "が処刑されました。" + BR
						+ Player.getJobName(winnerJob) + "陣営の勝利です。";
				break;
		}
		mResultHolder.tv_result.setText(result);

		for (Player player : mPlayers)
		{
			View view = mInflater.inflate(R.layout.layout_resultrow, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			tv_name.setText(player.getName());
			TextView tv_job = (TextView) view.findViewById(R.id.tv_job);
			if(player.getFirstJob() == player.getSecondJob())
			{
				tv_job.setText(player.getFirstJobName());
			}
			else
			{
				tv_job.setText(player.getFirstJobName() +
								" → " +
								player.getSecondJobName());
			}
			mResultHolder.playerListLayout.addView(view);
		}
	}

	private ArrayList<Integer> whoExecution()
	{
		//吊る人
		ArrayList<Integer> executionPlayer = new ArrayList<Integer>();
		int max = 0;
		for (Player player : mPlayers)
		{
			if(player.getVotes() > max)
				max = player.getVotes();
		}
		if(max > 1)
		{
			for (int i = 0;i<mPlayers.size();i++)
			{
				if(mPlayers.get(i).getVotes() == max)
					executionPlayer.add(i);
			}
		}
		return executionPlayer;
	}

	private int getWinnerJob(ArrayList<Integer> executionPlayer)
	{
		if(executionPlayer.size() == 0)
		{
			//誰も吊られていない
			for (Player player : mPlayers)
			{
				if(player.getSecondJob() == Player.JOB_JINRO)
					return Player.JOB_JINRO;
			}
			return Player.JOB_VILLAGER;
		}
		for (Integer i : executionPlayer)
		{
			if(mPlayers.get(i).getSecondJob() == Player.JOB_TERUTERU)
				return Player.JOB_TERUTERU;
		}
		for (Integer i : executionPlayer)
		{
			if(mPlayers.get(i).getSecondJob() == Player.JOB_JINRO)
				return Player.JOB_VILLAGER;
		}
		return Player.JOB_JINRO;
	}

	private class JobOpenHolder
	{
		public TextView tv_job;
		public TextView tv_msg;
		public LinearLayout actionListLayout;
		public ImageView iv_job;
		public ScrollView scrollView;

		public JobOpenHolder(View view)
		{
			tv_job = (TextView) view.findViewById(R.id.tv_job);
			tv_msg = (TextView) view.findViewById(R.id.tv_msg);
			actionListLayout = (LinearLayout) view.findViewById(R.id.actionList);
			iv_job = (ImageView) view.findViewById(R.id.iv_job);
			scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		}
	}

	private class VoteHolder
	{
		public LinearLayout playerListLayout;
		public ScrollView scrollView;

		public VoteHolder(View view)
		{
			playerListLayout = (LinearLayout) view.findViewById(R.id.playerList);
			scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		}
	}

	private class ResultHolder
	{
		public LinearLayout playerListLayout;
		public Button btn_toTop;
		public Button btn_again;
		public TextView tv_result;

		public ResultHolder(View view)
		{
			playerListLayout = (LinearLayout) view.findViewById(R.id.playerList);
			btn_toTop = (Button) view.findViewById(R.id.btn_toTop);
			btn_toTop.setOnClickListener(GameManagement.this);
			btn_again = (Button) view.findViewById(R.id.btn_again);
			btn_again.setOnClickListener(GameManagement.this);
			tv_result = (TextView) view.findViewById(R.id.tv_result);
		}
	}

	private class ConfirmHolder
	{
		public TextView tv_name;
		public Button btn_ok;

		public ConfirmHolder(View view)
		{
			tv_name = (TextView) view.findViewById(R.id.tv_name);
			btn_ok = (Button) view.findViewById(R.id.btn_ok);
			btn_ok.setOnClickListener(GameManagement.this);
		}
	}

	private class DiscussionHolder
	{
		public Button btn_toVote;

		public DiscussionHolder(View view)
		{
			btn_toVote = (Button) view.findViewById(R.id.btn_toVote);
			btn_toVote.setOnClickListener(GameManagement.this);
		}
	}

}
