package com.excellence.downloader;

import static com.excellence.downloader.utils.HttpUtil.checkNULL;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.Executor;

import com.excellence.downloader.utils.IListener;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2017/8/9
 *     desc   : 下载管理
 * </pre>
 */

public class FileDownloader
{
	public static final String TAG = FileDownloader.class.getSimpleName();

	private Executor mExecutor = null;
	private final LinkedList<DownloadTask> mTaskQueue;
	private int mParallelTaskCount;
	private int mThreadCount;

	public FileDownloader(int parallelTaskCount, int threadCount)
	{
		final Handler handler = new Handler(Looper.getMainLooper());
		mExecutor = new Executor()
		{
			@Override
			public void execute(@NonNull Runnable command)
			{
				handler.post(command);
			}
		};
		mTaskQueue = new LinkedList<>();
		mParallelTaskCount = parallelTaskCount;
		mThreadCount = threadCount;
	}

	public DownloadTask addTask(File storeFile, String url, IListener listener)
	{
		DownloadTask task = new DownloadTask(storeFile, url, listener);
		synchronized (mTaskQueue)
		{
			mTaskQueue.add(task);
		}
		schedule();
		return task;
	}

	private synchronized void schedule()
	{
		// count run task
		int runTaskCount = 0;
		for (DownloadTask task : mTaskQueue)
		{
			if (task.isDownloading())
				runTaskCount++;
		}

		if (runTaskCount >= mParallelTaskCount)
			return;

		// deploy task to fill parallel task count
		for (DownloadTask task : mTaskQueue)
		{
			if (task.deploy() && ++runTaskCount == mParallelTaskCount)
				return;
		}
	}

	public DownloadTask get(File storeFile, String url)
	{
		if (storeFile == null || checkNULL(url))
			return null;
		for (DownloadTask task : mTaskQueue)
		{
			if (task.check(storeFile, url))
				return task;
		}
		return null;
	}

	public DownloadTask get(String filePath, String url)
	{
		return get(new File(filePath), url);
	}

	public LinkedList<DownloadTask> getTaskQueue()
	{
		return mTaskQueue;
	}
}
