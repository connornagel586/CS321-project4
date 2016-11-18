public class TreeObject {

	private long key = 0;

	public TreeObject(long key){
		this.key = key;
	}

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

}
