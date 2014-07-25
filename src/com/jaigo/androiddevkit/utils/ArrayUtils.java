package com.jaigo.androiddevkit.utils;


public class ArrayUtils
{
	public static void set4fArray(float[] destination, float[] source)
	{
		System.arraycopy(source, 0, destination, 0, 4);
	}

	public static float[] componentWiseAdd(float[] fv1, float[] fv2)
	{
		float[] answer = new float[4];
		for (int i = 3; i >= 0; --i)
		{
			answer[i] = fv1[i] + fv2[i];
		}
		return answer;
	}

	public static void componentWiseAddToDestinationArray(float[] src1, float[] src2, float[] dst)
	{
		for (int i = 3; i >= 0; --i)
		{
			dst[i] = src1[i] + src2[i];
		}
	}

	public static float[] componentWiseSubtract(float[] end, float[] start)
	{
		float[] answer = new float[4];
		for (int i = 3; i >= 0; --i)
		{
			answer[i] = end[i] - start[i];
		}
		return answer;
	}

	public static float[] componentWiseMultiply(float[] fv1, float[] fv2)
	{
		float[] answer = new float[4];
		for (int i = 3; i >= 0; --i)
		{
			answer[i] = fv1[i] * fv2[i];
		}
		return answer;
	}

	public static float[] array4fTimesNumber(float[] array, float multiplier)
	{
		float[] newArray = new float[4];
		for (int i = 3; i >= 0; --i)
		{
			newArray[i] = array[i] * multiplier;
		}
		return newArray;
	}
}
