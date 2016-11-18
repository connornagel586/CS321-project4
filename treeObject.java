public class TreeObject implements comparable{

	private long key = 0;
	private int freq = 0;

	public TreeObject(long key){
		this.key = key;
	}

	@Override
	public int compareTo(long key){
		if(this.key == key){
			return 0;
		}
		if(this.key < key){
			return -1;
		}
		if(this.key > key){
			return 1;
		}
	}
	public boolean equals(long key){
		if(this.key == key){
			return true;
		}

		return false;
	}

	public int getFreq(){
		return this.freq;
	}

	public int increaseFreq(){
	return freq++;
	}
	
	@Override
	public String toString(){
		return key.toString();
	}

}
