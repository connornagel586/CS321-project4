import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Stack;

public class BTree<T> {
	public static int degree;
	BTreeNode<T> root, s, r, z, child;
	int keyLength, nodeCount, maxKeys;
	File file;
	RandomAccessFile raf;
	long nodeSize;
	boolean useCache = false;
	// Cache Cache;
	Cache<BTreeNode> Cache;
	int sizeOfCache = 0;

	public BTree(int degree, File file) throws IOException {
		this.degree = degree;
		
		this.keyLength = keyLength;
		raf = new RandomAccessFile(file, "rw");
		maxKeys = 2 * degree - 1;
		this.file = file;
		int cacheSize = 1000;
		// Cache = new Cache(1000);
		sizeOfCache = cacheSize;
		if (sizeOfCache > 0) {
			useCache = true;
			this.Cache = new Cache<BTreeNode>(sizeOfCache);
		}
		raf.seek(16);
		root = new BTreeNode<T>();
		root.isLeaf = true;
		root.numKeys = 0;
		root.current = 1;
	}

	public void insertNode(TreeObject o) throws IOException {
		r = root;
		if (r.isFull()) {
			// uh-oh, the root is full, we have to split it
			s = new BTreeNode<T>();
			nodeCount++;
			s.current = nodeCount;
			root = s; // new root node
			s.isLeaf = false; // will have some children
			s.numKeys = 0; // for now
			s.childPointers[1] = r.current; // child is the old root node
			splitNode(s, 1, r); // r is split
			insertNodeNonFull(s, o); // s is clearly not full
		} else
			insertNodeNonFull(r, o);
	}

	public void insertNodeNonFull(BTreeNode<T> x, TreeObject o) throws IOException {

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
			diskWrite(x);
			// For the Cache
			if (useCache) {
				/*
				 * if (Cache.containsObject(x.current)) {
				 * Cache.removeObject(x.current); } Cache.addObject(x); }
				 */
			} else {
				while (i >= 0 && o.compareTo(x.keys[i]) < 0) {
					i--;
				}
				if (i != -1 && o.compareTo(x.keys[i]) == 0) {
					x.keys[i].increaseFrequency();
					diskWrite(x);
					// For the Cache
					if (useCache) {
						/*
						 * if (Cache.containsObject(x.current)) {
						 * Cache.removeObject(x.current); } Cache.addObject(x);
						 */ }
				} else {
					i++;
					// For the Cache
					if (useCache) {
						/*
						 * if (Cache.containsObject(x.childPointers[i])) { x =
						 * (BTree<T>.BTreeNode<T>)
						 * Cache.removeObject(x.childPointers[i]); } else {
						 * DiskRead(x.childPointers[i]); }
						 */
					} else {
						DiskRead(x.childPointers[i]);
					}
					if (x.numKeys == maxKeys) {
						splitNode(x, i, x);
						insertNodeNonFull(x, o);
					} else {
						insertNodeNonFull(x, o);
					}

				}
			}
		}
	}

	private void splitNode(BTreeNode<T> x, int i, BTreeNode<T> y) throws IOException {

		z = new BTreeNode<T>();
		nodeCount++; // We need to keep track of the amount of nodes.
		z.current = nodeCount;
		// new node is a leaf if old node was 
		z.isLeaf = y.isLeaf;
		// we since y is full, the new node must have t-1 keys
		z.numKeys = degree - 1;
		// copy over the "right half" of y into split
		for (int j = 0; j < degree - 1; j++) {
			z.keys[j] = y.keys[degree + j];
			y.keys[degree + j] = null;
		}
		// copy over the child pointers if y isn't a leaf
		if (!y.isLeaf) { // If not in a leaf go through the tree.
			for (int j = 0; j < degree; j++) {
				z.childPointers[j] = y.childPointers[degree + j];
				y.childPointers[degree + j] = 0;
			}
		}
		// having "chopped off" the right half of y, it now has t-1 keys
		y.numKeys = degree - 1;
		// shift everything in x over from i+1, then stick the new child in x;
		// y will half its former self as ci[x] and split will 
		// be the other half as ci+1[x]
		for (int j = x.numKeys; j > i; j--) {
			x.childPointers[j + 1] = x.childPointers[j];
		}
		
		x.childPointers[i + 1] = z.current;
		// the keys have to be shifted over as well...
		for (int j = x.numKeys - 1; j >= i; j--) {
			x.keys[j + 1] = x.keys[j];
		}
		// ...to accomodate the new key we're bringing in from the middle 
		// of y (if you're wondering, since (t-1) + (t-1) = 2t-2, where 
		// the other key went, its coming into x)
		x.keys[0] = y.keys[degree - 1];
		x.numKeys++;
		
		// write everything out to disk
		diskWrite(y);
		diskWrite(z);
		diskWrite(x);

	}

	private int diskWrite(BTreeNode<T> x) throws IOException {

		if (!x.isLeaf) {
			x.current = nodeCount++;
			x.isLeaf = true;
		}

		raf.seek(16 + x.current * nodeSize());

		raf.writeInt(degree);

		// Writing the KeyObject
		for (int i = 0; i < x.numKeys; i++) {
			if(x.keys[i] != null){
				raf.writeLong(x.keys[i].getKey());
				raf.writeInt(x.keys[i].getFreq());
			}
			}

		// Writing the pointers
		for (int i = 0; i < 2 * degree; i++) {
			if(x.childPointers[i] != 0){
				raf.writeLong(x.childPointers[i]);
			}
		}
		
		//Writing Meta Data
		raf.writeBoolean(x.isLeaf);
		raf.writeInt(x.numKeys);
		raf.writeBoolean(x.isLeaf);
		raf.writeInt(x.current);
		return x.current;
	}

	private BTreeNode<T> DiskRead(long offset) throws IOException {

		raf.seek(16 + offset * nodeSize());

		BTreeNode<T> node = new BTreeNode<T>();

		// Reading the degree from the file.
		degree = raf.readInt();

		// Reading the KeyObject
		for (int i = 0; i < 2 * degree - 1; i++) {
			node.keys[i] = new TreeObject(raf.readLong());
			node.keys[i].setFreq(raf.readInt());
		}

		// Reading the pointers
		for (int i = 0; i < 2 * degree; i++) {
			node.childPointers[i] = raf.readLong();
		}
		
		// Reading MetaData
		node.isLeaf = raf.readBoolean();
		node.numKeys = raf.readInt();
		node.isLeaf = raf.readBoolean();
		node.current = raf.readInt();

		return node;
	}

	public void debugPrintIOT(File travFile) throws IOException {
		FileWriter fWriter = new FileWriter(travFile);
		BufferedWriter out = new BufferedWriter(fWriter);

		Stack<Pair> stack = new Stack<Pair>();

		stack.push(new Pair(0, 0));

		while (!stack.isEmpty()) {
			Pair pair = stack.pop();
			BTreeNode<T> currNode = DiskRead(pair.getIndex());
			int keyPosition = pair.getKeyPosition();

			if (currNode.isLeaf) {
				for (int i = 0; i < currNode.numKeys; i++) {
					out.write(currNode.keys[i].getFreq() + "  " + currNode.keys[i].getKey());
					out.newLine();
				}

				continue;
			} else if (keyPosition > 0) {
				out.write(currNode.keys[keyPosition - 1].getFreq() + "  " + currNode.keys[keyPosition - 1].getKey());
				out.newLine();
			}

			if (keyPosition < currNode.numKeys)
				stack.push(new Pair(pair.getIndex(), keyPosition + 1));

			BTreeNode<T> childNode = DiskRead(currNode.childPointers[keyPosition]);

			stack.push(new Pair(childNode.current, 0));
		}
		out.close();
	}

	/**
	 * 
	 *
	 */
	private class Pair {
		private int i;
		private int keyCurrentPos;

		private Pair(int i, int keyCurrentPos) {
			this.i = i;
			this.keyCurrentPos = keyCurrentPos;
		}

		int getIndex() {
			return i;
		}

		int getKeyPosition() {
			return keyCurrentPos;
		}
	}

	/*
	 * public BTreeNode<T> readCache(BTreeNode<T> x){ if
	 * (Cache.removeObject(x)){ Cache.addObject(x);
	 * 
	 * }else{ //************************** Need to pass node offsete x =
	 * DiskRead(x.); BTreeNode<T> dump = Cache.addObject(x); if (dump!=null){
	 * DiskWrite(dump); } } return x;
	 * 
	 * } public void useingCache(BTreeNode<T> x) { if (Cache.removeObject(x)) {
	 * Cache.addObject(x); } else { BTreeNode<T> dump = Cache.addObject(x); if
	 * (dump != null) { DiskWrite(dump); } } }
	 * 
	 * Search
	 * 
	 * public TreeObject search(BTreeNode<T> x) { TreeObject k = new
	 * TreeObject(nodeSize); int i = 0; while (i < x.numKeys &&
	 * (k).compareTo(x.keys[i]) > 0) { i++; } if (i < x.numKeys &&
	 * k.compareTo(x.keys[i]) == 0) { x.keys[i].increaseFrequency();
	 * 
	 * if(useCache){ useingCache(x); }else{ DiskWrite(x); } return x.keys[i]; }
	 * else if (x.current == 1) { return null; } else { if(useCache){
	 * //************************** Need to Pass node array data readCache(x.);
	 * }else{ //************************** Need to Pass node offeset
	 * diskRead(x.); } //************************** Need to retrun node array
	 * data return search(x.); } }
	 */

	private long nodeSize() {
		int keyObjectSize = Long.BYTES + Integer.BYTES;
		int isLeafSize = 1;
		int pointer = Integer.BYTES;
		int numPointers = 2 * degree;
		int numKeys = 2 * degree - 1;
		int current = Integer.BYTES;

		int size = keyObjectSize * numKeys + pointer * numPointers + isLeafSize + current;
		return size;
	}

	@SuppressWarnings("hiding")
	private class BTreeNode<T> {

		TreeObject[] keys;
		public int current; // Keeps track of were we are at.
		long[] childPointers; // This will be useful for a couple of things
		int numKeys; // So we know when we are full.

		boolean isLeaf; // We will have to set this when we reach a leaf.

		// Not sure if we need both constructors lol just shotgunning this one.
		BTreeNode() {
			keys = new TreeObject[maxKeys];
			childPointers = new long[2 * degree];
			numKeys = 0;
		}

		BTreeNode(int i) {
			keys = new TreeObject[2 * i - 1];
			childPointers = new long[2 * i];
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
