import java.io.File;

public class BTree<T> {
	private static int degree;
	BTreeNode<T> root, current, splitNode, splitChild, child;
	int keyLength, nodeCount, maxKeys;
	File file;

	public BTree(int keyLength, int maxKeys, int degree, File file) {

		this.keyLength = keyLength;
		maxKeys = 2 * degree - 1;
		this.file = file;
	}

private void InsertNode(){
/*		BTree<T>.BTreeNode<T> r = root;
			if n[r] = 2t - 1 then
				// uh-oh, the root is full, we have to split it
				s = allocate-node ()
				root[T] = s 	// new root node
				leaf[s] = False // will have some children
				n[s] = 0	// for now
				c1[s] = r // child is the old root node
				B-Tree-Split-Child (s, 1, r) // r is split
				B-Tree-Insert-Nonfull (s, k) // s is clearly not full
			else
				B-Tree-Insert-Nonfull (r, k)*/
}

	private void InsertNodeNonFull() {

	/*		B-Tree-Insert-Nonfull (x, k)
		i = n[x]

		if leaf[x] then

			// shift everything over to the "right" up to the
			// point where the new key k should go

			while i >= 1 and k < keyi[x] do
				keyi+1[x] = keyi[x]
				i--
			end while

			// stick k in its right place and bump up n[x]

			keyi+1[x] = k
			n[x]++
		else

			// find child where new key belongs:

			while i >= 1 and k < keyi[x] do
				i--
			end while

			// if k is in ci[x], then k <= keyi[x] (from the definition)
			// we'll go back to the last key (least i) where we found this
			// to be true, then read in that child node

			i++
			Disk-Read (ci[x])
			if n[ci[x]] = 2t - 1 then

				// uh-oh, this child node is full, we'll have to split it

				B-Tree-Split-Child (x, i, ci[x])

				// now ci[x] and ci+1[x] are the new children, 
				// and keyi[x] may have been changed. 
				// we'll see if k belongs in the first or the second

				if k > keyi[x] then i++
			end if

			// call ourself recursively to do the insertion

			B-Tree-Insert-Nonfull (ci[x], k)*/

	}

	private void SplitNode() {
/*		B-Tree-Split-Child (x, i, y)
		z = allocate-node ()

		// new node is a leaf if old node was 

		leaf[z] = leaf[y]

		// we since y is full, the new node must have t-1 keys

		n[z] = t - 1

		// copy over the "right half" of y into z

		for j in 1..t-1 do
			keyj[z] = keyj+t[y]
		end for

		// copy over the child pointers if y isn't a leaf

		if not leaf[y] then
			for j in 1..t do
				cj[z] = cj+t[y]
			end for
		end if

		// having "chopped off" the right half of y, it now has t-1 keys

		n[y] = t - 1

		// shift everything in x over from i+1, then stick the new child in x;
		// y will half its former self as ci[x] and z will 	
		// be the other half as ci+1[x]

		for j in n[x]+1 downto i+1 do
			cj+1[x] = cj[x]
		end for
		ci+1 = z

		// the keys have to be shifted over as well...

		for j in n[x] downto i do
			keyj+1[x] = keyj[x]
		end for

		// ...to accomodate the new key we're bringing in from the middle 
		// of y (if you're wondering, since (t-1) + (t-1) = 2t-2, where 
		// the other key went, its coming into x)
		
		keyi[x] = keyt[y]
		n[x]++

		// write everything out to disk

		Disk-Write (y)
		Disk-Write (z)
		Disk-Write (x)*/
	}

	private class BTreeNode<T> {

		TreeObject[] keys; 
		public int current; // Keeps track of were we are at.
		int[] childPointers; // This will be useful for a couple of things including know if we are in a leaf. 
		int numKeys; // So we know when we are full.
		boolean isLeaf; // We will have to set this when we reach a leaf. 
		
		//Not sure if we need both constructors lol just shotgunning this one.
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
	}
}
