package com.excellence.downloader.exception;

/**
 * Created by ZhangWei on 2017/3/1.
 */
public class URLInvalidException extends DownloadError
{
	public URLInvalidException()
	{
		super("Download URL is empty or invalid.");
	}

	public URLInvalidException(String detailMessage)
	{
		super(detailMessage);
	}

}
