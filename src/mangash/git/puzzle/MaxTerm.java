package mangash.git.puzzle;

public class MaxTerm implements Comparable<MaxTerm>{
	public String key;
	public Double value;
	
	public MaxTerm (String term, double value)
	{
		this.key=term;
		this.value=value;
	}
	
	public String toString()
	{
		return "{"+this.key+" , SCORE: "+this.value+"}";
	}

	@Override
	public int compareTo(MaxTerm o) {
		if (o.value>this.value)
		{
			return 1;
		}
		if (o.value<this.value)
		{
			return -1;
		}
		return 0;
	}
}
