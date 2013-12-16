package jp.romerome.onenightjinro;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Player implements Serializable
{
	public static final int JOB_NUM = 4;
	public static final int JOB_NONE = -1;
	public static final int JOB_JINRO = 0;
	public static final int JOB_AUGUR = 1;
	public static final int JOB_THIEF = 2;
	public static final int JOB_VILLAGER = 3;

	private String mName;
	private int mFirstJob;
	private int mSecondJob;
	private int mVotes;

	public Player(String name)
	{
		mName = name;
		mFirstJob = JOB_NONE;
		mSecondJob = JOB_NONE;
	}

	public void setFirstJob(int job)
	{
		mFirstJob = job;
		mSecondJob = job;
	}

	public int getFirstJob()
	{
		return mFirstJob;
	}

	public void setSecondJob(int job)
	{
		mSecondJob = job;
	}

	public int getSecondJob()
	{
		return mSecondJob;
	}

	public void resetJob()
	{
		mFirstJob = JOB_NONE;
		mSecondJob = JOB_NONE;
	}

	public String getName()
	{
		return mName;
	}

	@Override
	public String toString()
	{
		return mName + ":" + getFirstJobName();
	}

	public int getVotes()
	{
		return mVotes;
	}

	public int incVotes()
	{
		mVotes++;
		return mVotes;
	}

	public void resetVotes()
	{
		mVotes = 0;
	}

	public String getFirstJobName()
	{
		switch (mFirstJob)
		{
			case JOB_JINRO:
				return "人狼";

			case JOB_AUGUR:
				return "占い師";

			case JOB_THIEF:
				return "怪盗";

			case JOB_VILLAGER:
				return "村人";

			default:
				return "NONE";
		}
	}

	public String getSecondJobName()
	{
		switch (mSecondJob)
		{
			case JOB_JINRO:
				return "人狼";

			case JOB_AUGUR:
				return "占い師";

			case JOB_THIEF:
				return "怪盗";

			case JOB_VILLAGER:
				return "村人";

			default:
				return "NONE";
		}
	}

	public static String getJobName(int job)
	{
		switch (job)
		{
			case JOB_JINRO:
				return "人狼";

			case JOB_AUGUR:
				return "占い師";

			case JOB_THIEF:
				return "怪盗";

			case JOB_VILLAGER:
				return "村人";

			default:
				return "NONE";
		}
	}

}
