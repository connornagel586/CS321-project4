import java.io.File;

public class BTree<T> {
	private static int degree;
	BTreeNode<T> root, r , s, splitNode, child;
	int keyLength, nodeCount, maxKeys;
	File file;

	public BTree(int keyLength, int degree, File file) {
		root = new BTreeNode<T>();
		root.isLeaf = true;
		root.numKeys = 0;
		root.current = 1;
		this.keyLength = keyLength;
		maxKeys = 2 * degree - 1;
		this.file = file;
	}

	private void InsertNode(TreeObject o) {
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

	private void InsertNodeNonFull(BTreeNode<T> x , TreeObject k) {
		
		int i = x.numKeys;
		
		if (x.isLeaf){

			// shift everything over to the "right" up to the
			// point where the new key k should go

			while (i >= 1 && k.compareTo(x.keys[i]) < 0){ //use compare to.
				x.keys[i+1] = x.keys[i];
				i--;
			}

			// stick k in its right place and increase numKeys

			x.keys[i+1] = k;
			x.numKeys++;
			DiskWrite(x);
		}
		else{

			// find child where new key belongs:

			while (i >= 1 && k.compareTo(x.keys[i]) < 0){//use compare to.
				i--;
			}

			// if k is in ci[x], then k <= keyi[x] (from the definition)
			// we'll go back to the last key (least i) where we found this
			// to be true, then read in that child node

			i++;
			DiskRead(x.childPointers[i]);
		}
		if (x.childPointers[i].numKeys = 2 * degree - 1){

			// this child node is full, split the node

			SplitNode(x, i, y);

			// now ci[x] and ci+1[x] are the new children, 
			// and keyi[x] may have been changed. 
			// we'll see if k belongs in the first or the second
		}
		if (k.compareTo(x.keys[i]) > 0){
			i++;
		}

		// call method recursively to do the insertion

		InsertNodeNonFull(x, k);

	}

	private void DiskRead(int i) {
		// TODO Auto-generated method stub
		
	}

	private void DiskWrite(BTree<T>.BTreeNode<T> x) {
		// TODO Auto-generated method stub
		
	}

	private void SplitNode(BTreeNode<T> x, int i,  BTreeNode<T> y) {

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
		diskWrite(x);
		diskWrite(y);
		diskWrite(splitNode);

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

		TreeObject getKey(int i) {
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
