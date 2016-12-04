import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BTree<T> {
	private static int degree;
	BTreeNode<T> root, r , s, splitNode, child;
	int keyLength, nodeCount, maxKeys;
	File file;
	final int nodeSize = 0;
	RandomAccessFile raf;
	long nodeSize;
	boolean useCache = false;

	public BTree(int keyLength, int degree, File file) throws IOException {
		root = new BTreeNode<T>();
		root.isLeaf = true;
		root.numKeys = 0;
		root.current = 1;
		this.keyLength = keyLength;
		maxKeys = 2 * degree - 1;
		this.file = file;
		raf = new RandomAccessFile(file.getName() + ".btree.data." + keyLength + "." + degree + ".bin" , "rw");
		raf.seek(16);
	}

	public void InsertNode(TreeObject o) {
		r = root;
		if (r.isFull()) {
			// uh-oh, the root is full, we have to split it
			s = new BTreeNode<T>();
			nodeCount++;
			s.current = nodeCount;
			root = s; // new root node
			s.isLeaf = false; // will have some children
			s.numKeys = 0; // for now
			r.parent = s.current;
			s.childPointers[1] = r.current; // child is the old root node
			SplitNode(s, 1, r); // r is split
			InsertNodeNonFull(s, o); // s is clearly not full
		} else
			InsertNodeNonFull(r, o);
	}

	public void InsertNodeNonFull(BTreeNode<T> x, TreeObject o) {

		int i = x.numKeys - 1;
		if (x.isLeaf) {
			// find child where new key belongs:
			while (i >= 0 && o.compareTo(x.keys[i]) < 0) {
				i--;
			}
			if (i != -1 && o.compareTo(x.keys[i]) == 0) {
				x.keys[i].increaseFrequency();
			} else {
				i = x.numKeys - 1;
				// shift everything over to the "right" up to the
				// point where the new key k should go
				while (i >= 0 && o.compareTo(x.keys[i]) < 0) {
					x.keys[i + 1] = x.keys[i];
					i--;
				}
				// stick k in its right place and increase numKeys
				x.setKey(o, i + 1);
				x.numKeys++;
			}
			DiskWrite(x);
			// For the Cache
			if (useCache) {
				if (Cache.containsObject(x.current)) {
					Cache.removeObject(x.current);
				}
				Cache.addObject(x);
			}

		} else {
			while (i >= 0 && o.compareTo(x.keys[i]) < 0) {
				i--;
			}
			if (i != -1 && o.compareTo(x.keys[i]) == 0) {
				x.keys[i].increaseFrequency();
				DiskWrite(x);
				// For the Cache
				if (useCache) {
					if (Cache.containsObject(x.current)) {
						Cache.removeObject(x.current);
					}
					Cache.addObject(x);
				}
			} else {
				i++;
				// For the Cache
				if (useCache) {
					if (Cache.containsObject(x.childPointers[i])) {
						x = (BTree<T>.BTreeNode<T>) Cache.removeObject(x.childPointers[i]);
					} else {
						DiskRead(x.childPointers[i]);
					}
				} else {
					DiskRead(x.childPointers[i]);
				}
				if (x.numKeys == maxKeys) {
					SplitNode(x, i, x);
					InsertNodeNonFull(x, o);
				} else {
					InsertNodeNonFull(x, o);
				}
			}
		}
	}

	private void SplitNode(BTreeNode<T> x, int i,  BTreeNode<T> y) throws IOException {

		splitNode = new BTreeNode<T>();
		nodeCount++; // We need to keep track of the amount of nodes.
		splitNode.current = nodeCount;
		splitNode.isLeaf = y.isLeaf; //Set our isLeaf flag.
		splitNode.numKeys = degree - 1;
		for (int j = 0; j < degree - 1; j++) {
			splitNode.keys[j] = y.keys[degree + j];
		}
		if (!y.isLeaf) { //If not in a leaf go through the tree.
			for (int j = 0; j < degree; j++) {
				splitNode.childPointers[j] = y.childPointers[degree + j];
			}
		}
		y.numKeys = degree - 1;
		for (int j = x.numKeys; j > i; j--) { 
												
			x.childPointers[j + 1] = x.childPointers[j];
		}
		x.childPointers[i + 1] = splitNode.current; 
		splitNode.parent = x.current;
		for (int j = x.numKeys - 1; j >= i; j--) {
			x.keys[j + 1] = x.keys[j];
		}
		x.keys[i] = y.keys[degree - 1];
		x.numKeys++;
		DiskWrite(x);
		DiskWrite(y);
		DiskWrite(splitNode);

	}
	
	private void DiskRead(BTreeNode<T> x) {
		

	}

	private void DiskWrite(BTreeNode<T> x) throws IOException {
		
		raf.seek(x.current * x.nodeSize() + 16);

		for(int i = 0; i < x.numKeys; i++){
		raf.writeLong(x.getKeys(i).getKey());
		raf.writeInt(x.keys[i].getFreq());
		}
		raf.writeInt(x.current);
		for(int i = 0; i < x.childPointers.length; i++ ){
		raf.writeInt(x.childPointers[i]);
		}
		raf.writeInt(x.numKeys);
		raf.writeInt(x.parent);
		raf.writeBoolean(x.isLeaf);

		
	}
	
	private void WriteMetaData() throws IOException {
		raf.seek(0);

		raf.writeInt(root.current);
		raf.writeInt(keyLength);
		raf.writeInt(degree);
		raf.writeInt(nodeCount);
	}
	
		private long nodeSize(){
		int keyObjectSize = Long.BYTES + Integer.BYTES;
		int isLeafSize = 1;
		int pointer = Integer.BYTES;
		int numPointers = 2*degree ;
		int numKeys = 2*degree -1;
		int current = Integer.BYTES;
		
		int size =  keyObjectSize * numKeys + pointer * numPointers + isLeafSize + current; 
		return size ; 
	}

	private class BTreeNode<T> {

		TreeObject[] keys;
		public int current; // Keeps track of were we are at.
		int[] childPointers; // This will be useful for a couple of things
		int numKeys, parent; // So we know when we are full.
		
		

		boolean isLeaf; // We will have to set this when we reach a leaf.

		// Not sure if we need both constructors lol just shotgunning this one.
		BTreeNode() {
			keys = new TreeObject[2 * degree - 1];
			childPointers = new int[2 * degree];
			numKeys = 0;
		}

		BTreeNode(int i) {
			keys = new TreeObject[2 * i - 1];
			childPointers = new int[2 * i];
			numKeys = 0;
		}

		TreeObject getKeys(int i) {
			return keys[i];

		}

		void setKey(TreeObject k, int i) {
			keys[i] = k;
		}

		boolean isFull() {
			if (numKeys == keys.length) {
				return true;
			} else {
				return false;
			}
		}
	}
}
