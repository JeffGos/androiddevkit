package com.jaigo.androiddevkit;

import com.jaigo.androiddevkit.utils.ConvertUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeSpan implements Comparable<TimeSpan>
{
    public static final TimeSpan Zero = new TimeSpan(0L);
    public static final TimeSpan MaxValue = new TimeSpan(0x7fffffffffffffffL);
    public static final TimeSpan MinValue = new TimeSpan(-9223372036854775808L);
    
    private long ticks;
    
    public TimeSpan(long ticks)
    {
    	this.ticks = ticks;
    }
    
    public TimeSpan(int hours, int minutes, int seconds)
    {
    	this.ticks = timeToTicks(hours, minutes, seconds);
    }
    
    public TimeSpan(int days, int hours, int minutes, int seconds)
    {
    	this(days, hours, minutes, seconds, 0);
    }
    
    public TimeSpan(int days, int hours, int minutes, int seconds, int milliseconds)
    {
    	 long num = ((((((days * 0xe10L) * 0x18L) + (hours * 0xe10L)) + (minutes * 60L)) + seconds) * 0x3e8L) + milliseconds;
    	    
    	 if ((num > 0x346dc5d638865L) || (num < -922337203685477L))
    	 {
    		 throw new IllegalArgumentException("TimeSpan created too long");
    	 }
    	 
    	 this.ticks = num * 0x2710L;
    }
    
    public long getTicks() 
    {  
    	return this.ticks;
    }
    
    public int getDays() 
    {
    	return (int) (this.ticks / 0xc92a69c000L);
    }
    
    public int getHours() 
    {
    	 return (int) ((this.ticks / 0x861c46800L) % 0x18L);
    }
    
    public int getMilliseconds() 
    {
    	return (int) ((this.ticks / 0x2710L) % 0x3e8L);
    }
    
    public int getMinutes() 
    { 
    	return (int) ((this.ticks / 0x23c34600L) % 60L);
    }
    
    public int getSeconds() 
    { 
    	return (int) ((this.ticks / 0x989680L) % 60L); 
    }
    
    public double getTotalDays() 
    {
    	 return (this.ticks * 1.1574074074074074E-12);
    }
    
    public double getTotalHours() 
    {
    	return (this.ticks * 2.7777777777777777E-11);
    }
    
    public double getTotalMilliseconds() 
    {
    	 double num = this.ticks * 0.0001;
         if (num > 922337203685477L)
         {
             return 922337203685477L;
         }
         if (num < -922337203685477L)
         {
             return -922337203685477L;
         }
         return num;
    }
    
    public double getTotalMinutes() 
    {
    	return (this.ticks * 1.6666666666666667E-09);
    }
    
    public double getTotalSeconds() 
    {
    	 return (this.ticks * 1E-07);
    }
    
    public TimeSpan add(TimeSpan value)
    {
    	long ticks = this.ticks + value.ticks;
	    
    	if (((this.ticks >> 0x3f) == (value.ticks >> 0x3f)) && ((this.ticks >> 0x3f) != (ticks >> 0x3f)))
	    {
	        throw new IllegalArgumentException("Result timespan too long");
	    }
	    
	    return new TimeSpan(ticks);
    }
    
    public static int compare(TimeSpan t1, TimeSpan t2)
    {
    	if (t1.ticks > t2.ticks)
        {
            return 1;
        }
        
    	if (t1.ticks < t2.ticks)
        {
            return -1;
        }
        
        return 0;
    }
    
    public int compareTo(TimeSpan value)
	{
    	if (this.ticks > value.ticks)
        {
            return 1;
        }
        
    	if (this.ticks < value.ticks)
        {
            return -1;
        }
        
        return 0;
	}
    
    public static TimeSpan fromDays(double value)
    {
    	return interval(value, 0x5265c00);
    }
    
    public TimeSpan duration()
    {
    	if (this.ticks == MinValue.ticks)
        {
            throw new ArithmeticException("Overflow Duration");
        }
    	
        return new TimeSpan((this.ticks >= 0L) ? this.ticks : -this.ticks);
    }
    
    @Override
    public boolean equals(Object value)
    {
    	if ( this == value ) return true;
		if ( !(value instanceof TimeSpan) ) return false;
		
		return ((TimeSpan)value).ticks == this.ticks;
    }
    
    public boolean equals(TimeSpan value)
    {
		return value.ticks == this.ticks;
    }
    
    public static boolean equals(TimeSpan t1, TimeSpan t2)
    {
    	return t1.ticks == t2.ticks;
    }
    
    @Override
    public int hashCode()
    {
    	return (((int) this.ticks) ^ ((int) (this.ticks >> 0x20)));
    }
    
    public static TimeSpan fromHours(double value)
    {
    	return interval(value, 0x36ee80);
    }
    
    private static TimeSpan interval(double value, int scale)
    {
    	if (Double.isNaN(value))
        {
            throw new IllegalArgumentException("Arg_CannotBeNaN");
        }
        
    	double num = value * scale;
        double num2 = num + ((value >= 0.0) ? 0.5 : -0.5);
        
        if ((num2 > 922337203685477L) || (num2 < -922337203685477L))
        {
            throw new IllegalArgumentException("Overflow TimeSpanTooLong");
        }
        
        return new TimeSpan(((long) num2) * 0x2710L);
    }
    
    public static TimeSpan fromMilliseconds(double value)
    {
    	return interval(value, 1);
    }
    
    public static TimeSpan fromMinutes(double value)
    {
    	return interval(value, 0xea60);
    }
    
    public TimeSpan negate()
    {
    	if (this.ticks == MinValue.ticks)
        {
            throw new ArithmeticException("Overflow_NegateTwosCompNum");
        }
    	
        return new TimeSpan(-this.ticks);
    }
    
    public static TimeSpan parse(String value) throws IllegalArgumentException
    {
    	Pattern pattern = Pattern.compile("^P(([0-9\\.]*)Y)?(([0-9\\.]*)M)?(([0-9\\.]*)D)?(T)?(([0-9\\.]*)H)?(([0-9\\.]*)M)?(([0-9\\.]*)S)?$");
		Matcher matcher = pattern.matcher(value);
		
		double year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;

		if (matcher.find())
		{
			String s;

			s = matcher.group(2);

			if (s != null && s.length() > 0)
			{
				year = ConvertUtils.toDouble(s);
			}

			s = matcher.group(4);

			if (s != null && s.length() > 0)
			{
				month = ConvertUtils.toDouble(s);
			}

			s = matcher.group(6);

			if (s != null && s.length() > 0)
			{
				day = ConvertUtils.toDouble(s);
			}

			s = matcher.group(9);

			if (s != null && s.length() > 0)
			{
				hour = ConvertUtils.toDouble(s);
			}

			s = matcher.group(11);

			if (s != null && s.length() > 0)
			{
				minute = ConvertUtils.toDouble(s);
			}

			s = matcher.group(13);

			if (s != null && s.length() > 0)
			{
				second = ConvertUtils.toDouble(s);
			}
			
			return new TimeSpan((int)((year * (double)365.242199) + (month * (double)30.4368499) + day), (int)hour, (int)minute, 0).add(TimeSpan.fromTicks((long)(second * 0x989680)));
		}
		else
		{
			throw new IllegalArgumentException("Error parsing input string");
		}
    }
    
    public static TimeSpan fromSeconds(double value)
    {
    	return interval(value, 0x3e8);
    }
    
    public TimeSpan subtract(TimeSpan value)
    {
    	long ticks = this.ticks - value.ticks;
	    
    	if (((this.ticks >> 0x3f) != (value.ticks >> 0x3f)) && ((this.ticks >> 0x3f) != (ticks >> 0x3f)))
	    {
	        throw new IllegalArgumentException("Overflow TimeSpanTooLong");
	    }
	    
    	return new TimeSpan(ticks);
    }
    
    public static TimeSpan fromTicks(long value)
    {
    	return new TimeSpan(value);
    }
    
    protected static long timeToTicks(int hour, int minute, int second)
    {
    	long num = ((hour * 0xe10L) + (minute * 60L)) + second;
        
    	if ((num > 0xd6bf94d5e5L) || (num < -922337203685L))
        {
            throw new IllegalArgumentException("Overflow TimeSpanTooLong");
        }
        
        return (num * 0x989680L);
    }

    @Override
    public String toString()
    {
		StringBuffer retval = new StringBuffer();
		
		int num = (int)(this.getTicks() / (long)0xc92a69c000L);
		long num2 = this.getTicks() % (long)0xc92a69c000L;

		if (this.getTicks() < 0L)
		{
			retval.append("-");

			num = -num;
			num2 = -num2;
		}
		
		retval.append("P");
		retval.append(Integer.toString(num));
		
		retval.append("DT");
		
		retval.append(ConvertUtils.toString((int) ((num2 / (long) 0x861c46800L) % 0x18L), 2));
		retval.append("H");
		retval.append(ConvertUtils.toString((int) ((num2 / (long) 0x23c34600L) % 60L), 2));
		retval.append("M");
		retval.append(ConvertUtils.toString((int) ((num2 / (long) 0x989680L) % 60L), 2));
		
		int n = (int) (num2 % (long)0x989680L);

		if (n != 0)
		{
			retval.append(".");
			retval.append(ConvertUtils.toString(n, 7));
		}

		retval.append("S");

		return retval.toString();
    }
    
    public TimeSpan clone()
    {
    	return new TimeSpan(this.ticks);
    }
}


