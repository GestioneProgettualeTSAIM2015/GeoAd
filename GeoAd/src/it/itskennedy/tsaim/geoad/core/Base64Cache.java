package it.itskennedy.tsaim.geoad.core;

import it.itskennedy.tsaim.geoad.entity.Thumb;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.util.SparseArray;

public class Base64Cache 
{
	private SparseArray<List<Thumb>> mThumbCache;
	private SparseArray<String> mImageCache;
	
	public Base64Cache()
	{
		mThumbCache = new SparseArray<List<Thumb>>();
		mImageCache = new SparseArray<String>();
	}
	
	public List<Thumb> getThumbs(int aId) 
	{
		return mThumbCache.get(aId);
	}
	
	public String getImage(int aId)
	{
		return mImageCache.get(aId);
	}

	public void cacheThumb(Thumb aThumb, int aLocationId)
	{
		List<Thumb> vCached = mThumbCache.get(aLocationId);
		if(vCached == null)
		{
			vCached = new ArrayList<Thumb>();
		}
		vCached.add(aThumb);
		mThumbCache.put(aLocationId, vCached);
	}
	
	public JSONArray getThumbIdList(int aLocationId)
	{
		JSONArray vJsonList = new JSONArray();
		List<Thumb> vCached = mThumbCache.get(aLocationId);
		
		if(vCached != null)
		{
			for(int i = 0; i < vCached.size(); ++i)
			{
				vJsonList.put(vCached.get(i).mId);
			}
		}
		
		return vJsonList;
	}

	public void cacheImage(String aBase64, int mId)
	{
		mImageCache.put(mId, aBase64);
	}

	public void remove(int aImageId, int aLocId)
	{
		mImageCache.remove(aImageId);
		List<Thumb> vList = mThumbCache.get(aLocId);
		
		for(int i = 0; i < vList.size(); ++i)
		{
			if(vList.get(i).mId == aImageId)
			{
				vList.remove(i);
				continue;
			}
		}
		
		mThumbCache.put(aLocId, vList);
	}
}
