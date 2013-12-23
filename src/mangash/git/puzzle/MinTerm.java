package mangash.git.puzzle;

public class MinTerm implements Comparable<MinTerm>{
	public String key;
	public Double value;
	
	public MinTerm (String term, double value)
	{
		this.key=term;
		this.value=value;
	}
	
	public String toString()
	{
		return "{"+this.key+" , SCORE: "+this.value+"}"; 
	}

	@Override
	public int compareTo(MinTerm o) {
		if (o.value<this.value)
		{
			return 1;
		}
		if (o.value>this.value)
		{
			return -1;
		}
		return 0;
	}
}
