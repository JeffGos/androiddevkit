package com.jaigo.androiddevkit;

public final class Urn implements Cloneable
{
	public static final String UrnDivider = ":";
	public static final Urn Empty = new Urn();

	protected int hashCode;

	protected String resourceName;
	protected String idType;
	protected String idValue;
	protected String urnString;
	
 
	public Urn()
	{
		this.idType = new String();
		this.idValue = new String();
		this.urnString = new String();
		this.resourceName = new String();
	}

	public Urn(final Urn urn)
	{
		this.idType = urn.idType;
		this.idValue = urn.idValue;
		this.urnString = urn.urnString;
		this.resourceName = urn.resourceName;
		this.hashCode = urn.hashCode;
	}

	public Urn(final String urnString)
	{
		Urn urn = parse(urnString);

		if (!urn.isEmpty())
		{
			this.idType = urn.idType;
			this.idValue = urn.idValue;
			this.urnString = urn.urnString;
			this.resourceName = urn.resourceName;

			this.hashCode = this.urnString.hashCode();
		}
	}

	public Urn(final String resourceName, final String idType, final String idValue)
	{
		this.idType = idType;
		this.idValue = idValue;
		this.resourceName = resourceName;

		this.urnString = this.resourceName + Urn.UrnDivider + this.idType + Urn.UrnDivider
				+ this.idValue;

		this.hashCode = this.urnString.hashCode();
	}

	public final String getResourceName()
	{
		return this.resourceName;
	}

	public final String getIdType()
	{
		return this.idType;
	}

	public final String getIdValue()
	{
		return this.idValue;
	}

	public final Urn replaceIdType(final String newIdType)
	{
		return new Urn(this.resourceName, newIdType, this.idValue);
	}

	public final Urn replaceIdValue(final String newIdValue)
	{
		return new Urn(this.resourceName, this.idType, newIdValue);
	}

	public final Urn replaceIdTypeAndValue(final String newIdType, final String newIdValue)
	{
		return new Urn(this.resourceName, newIdType, newIdValue);
	}

	public final Urn replaceResourceName(final String resourceName)
	{
		return new Urn(resourceName, this.idType, this.idValue);
	}

	public final boolean isResourceType(final String resourceName)
	{
		return this.resourceName.compareToIgnoreCase(resourceName) == 0;
	}

	public final static Urn parse(final String urnString)
	{
		if (urnString == null)
		{
			return null;
		}
		
		String[] parts = urnString.split(Urn.UrnDivider);

		int length = parts.length;

		if (length == 3)
		{
			Urn retval = new Urn();

			retval.resourceName = parts[0];
			retval.idType = parts[1];
			retval.idValue = parts[2];
			retval.urnString = urnString;
			retval.hashCode = urnString.hashCode();

			return retval;
		}

		return new Urn();
	}

	public final boolean isIdType(final String idTypeName)
	{
		return this.idType.compareToIgnoreCase(idTypeName) == 0;
	}

	public final boolean isEmpty()
	{
		return this.urnString.length() == 0;
	}

	@Override
	public int hashCode()
	{
		return this.hashCode;
	}

	@Override
	public boolean equals(Object compObj)
	{
		if (this == compObj)
        {
            return true;
        }

		if (!(compObj instanceof Urn))
        {
            return false;
        }

		return this.equals((Urn) compObj);
	}

	public boolean equals(Urn urn)
	{
		return this.resourceName.equals(urn.resourceName) && this.idType.equals(urn.idType) && this.idValue.equals(urn.idValue);
	}

	@Override
	public final String toString()
	{
		return urnString;
	}

	public Urn clone()
	{
		Urn newUrn = new Urn();

		newUrn.urnString = this.urnString;
		newUrn.hashCode = this.hashCode;
		newUrn.idType = this.idType;
		newUrn.idValue = this.idValue;
		newUrn.resourceName = this.resourceName;

		return newUrn;
	}
}