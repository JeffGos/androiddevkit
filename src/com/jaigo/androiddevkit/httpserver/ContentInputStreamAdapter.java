package com.jaigo.androiddevkit.httpserver;
// InputStreamAdapter
//
// Created by jeff.gosling on 15/04/13
// Copyright (c) 2012 DDN Ltd. All rights reserved.
//

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ContentInputStreamAdapter
{
	protected String rootFilesDirectory;
	protected HashMap<String, String> mimeTypes;

	public class ContentInputStream
	{
		public InputStream content;
		public String filename;
		public String mime;
		public long contentLength;
		public long lastModifiedDate;
	};

	public ContentInputStreamAdapter()
	{
		init();
	}

	public ContentInputStreamAdapter(String rootFilesDirectory)
	{
		this.rootFilesDirectory = rootFilesDirectory;
		init();
	}

	private void init()
	{
		mimeTypes = new HashMap<String, String>();
		mimeTypes.put(".ogg", "audio/ogg");
		mimeTypes.put(".mp3", "audio/mpeg");
		mimeTypes.put(".m4a", "audio/x-m4a");
	}

	public ContentInputStream getContentInputStream(String source) throws IOException
	{
		ContentInputStream result = null;

		File file = new File(rootFilesDirectory, source);

		if (file.exists())
		{
			result = new ContentInputStream();
			result.content = new FileInputStream(source);
			result.filename = source;
			result.mime = getMimeType(source);
			result.contentLength = file.length();
			result.lastModifiedDate = file.lastModified();
		}

		return result;
	}

	protected String getMimeType(String filename)
	{
		String extension = "";

		int i = filename.lastIndexOf('.');
		if (i > 0)
		{
			extension = filename.substring(i+1);
			mimeTypes.get(extension);
		}

		return null;
	}
}
