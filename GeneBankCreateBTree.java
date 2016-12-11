import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.StringTokenizer;

public class GeneBankCreateBTree {
	int debugLevel = -1, cacheSize, degree = -1, sequenceLength = 0;
	boolean useCache = false;
	private File file;
	KeyMaker genKey;
	

	public GeneBankCreateBTree()throws IOException {
	
	}

	

	public File getFile() {
		return this.file;
	}

	public File setFile(File f) {
		return this.file = f;
	}

	public int getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public int getSequenceLength() {
		return sequenceLength;
	}

	public void setSequenceLength(int sequenceLength) {
		this.sequenceLength = sequenceLength;
	}

	public static void main(String[] args) throws IOException {

		GeneBankCreateBTree create = new GeneBankCreateBTree();

		// Check arg length
		try {
			if (args.length < 4 || args.length > 6) {
				printUsage();
				System.exit(0);
			}

			// Check for chache
			if (args[0].equals("0") || args[0].equals("1")) {
				if (args[0].equals("0")) {
					create.useCache = false;
				} else {
					create.useCache = true;
				}
			} else {
				printUsage();
				System.exit(0);
			}

			if (Integer.parseInt(args[1]) >= 0) {
				if (create.degree == 0) {
					create.degree = 127;
				} else {
					create.setDegree(Integer.parseInt(args[1]));
				}
			} else {
				printUsage();
				System.exit(0);
			}
			// Check file
			if (!args[2].contains(".gbk")) {
				printUsage();
				System.exit(0);
			}
			create.setFile(new File(args[2]));

			// Check sequence length
			if (Integer.parseInt(args[3]) > 1 || Integer.parseInt(args[3]) < 31) {
				create.sequenceLength = Integer.parseInt(args[3]);
			} else {
				printUsage();
				System.exit(0);
			}

			// Check Debug and Cache Size
			if (args.length == 6 && create.useCache == false) {
				printUsage();
				System.exit(0);
			} else if (args.length == 6 && create.useCache == true) {
				create.cacheSize = Integer.parseInt(args[4]);
				create.debugLevel = Integer.parseInt(args[5]);
				if (create.debugLevel > 1 || create.debugLevel < 0) {
					printUsage();
					System.exit(0);
				}
			} else if (args.length == 5 && create.useCache == true) {
				create.cacheSize = Integer.parseInt(args[4]);
			} else if (args.length == 5 && create.useCache == false) {
				create.debugLevel = Integer.parseInt(args[4]);
				if (create.debugLevel > 1 || create.debugLevel < 0) {
					printUsage();
					System.exit(0);
				}
			}
			
		
			BTree tree = new BTree(create.getDegree(), create.getFile());
			KeyMaker genKey = new KeyMaker(create);
			TreeObject o = new TreeObject(genKey.getNextKey());
			 try {
					 tree.insertNode(o);
					
					 } catch (IOException e) {
					 e.printStackTrace();
					 }
			

			//
			// while (scan.hasNextLine()) {
			// nextLine = scan.nextLine();
			// if (nextLine.contains(startflag)) {
			//
			// while(scan.hasNextLine()){
			// nextLine = scan.nextLine().toLowerCase().trim();
			// if(nextLine.contains(endflag)){
			// break;
			// }
			//
			// StringTokenizer strin = new StringTokenizer(nextLine,
			// "0123456789 \\s n");
			// seq = "";
			// int start = 0;
			// while(strin.hasMoreElements()){
			// seq += strin.nextElement();
			// }
			// int j = 0;
			// while(sequence2.length() != sequenceLength && j != seq.length()){
			// sequence2 += seq.charAt(j);
			// j++;
			// }
			//
			// // Converts DNA sequence to binary.
			// if (sequence2.length() == sequenceLength) { // makes sure
			// // sequence is k
			// // in length.
			// for (int i = 0; i < sequence2.length(); i++) {
			// char c = sequence2.charAt(i);
			//
			// switch (c) {
			// case ('a'):
			// sequence1 += "00";
			// break;
			// case ('c'):
			// sequence1 += "01";
			// break;
			// case ('g'):
			// sequence1 += "10";
			// break;
			// case ('t'):
			// sequence1 += "11";
			// break;
			// case ('n'):
			// sequence1 = ""; // Skips the n's.
			//
			// break;
			// }
			// }
			//
			// // Add the binary sequence to the tree.
			// if (sequence1.length() == sequenceLength * 2) {
			// TreeObject o = new TreeObject(Long.parseLong(sequence1));
			// try {
			// tree.insertNode(o);
			//
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// sequence1 = "";
			// sequence2 = ""; // clear sequences
			// }
			// }
			//
			//
			//
			// }
			// }
			//
			// }
			//

			if (create.debugLevel == 0) {
				// do some stuff
			}
			if (create.debugLevel == 1) {
				// File dump = new File("dump");
				// tree.debugPrintIOT(dump);
			}
			
			

		} catch (NumberFormatException e) {
			printUsage();
		} catch (Exception e) {
			e.printStackTrace();
			printUsage();
		}
	}

	
	

	private static void printUsage() {
		System.err.println("GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> "
				+ "<sequence length> 1<=k<=31>" + "[<cache size>] [<debug level>]" + "[<debug level>]\n");
	}
}
