import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.StringTokenizer;

public class GeneBankCreateBTree {
	int debugLevel = -1, cacheSize, degree = -1, sequenceLength = 0;
	boolean useCache = false;

	public enum States {
		START, SEQUENCE, UNKNOWN_CHARS, IN_SEQUENCE, END_SEQUENCE, END
	}

	public GeneBankCreateBTree() {
		this.degree = degree;
		this.sequenceLength = sequenceLength;
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;
		this.useCache = useCache;
	}

	private File file;

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

	public static void main(String[] args) {

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
					create.degree = Integer.parseInt(args[1]);
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

	class KeyMaker {
		States state = States.START;
		Scanner scan;
		BTree tree;
		File file;
		String filename;
		String line;
		String key;

		public KeyMaker(GeneBankCreateBTree create) throws IOException {
			filename = (file.getName() + ".btree.data." + sequenceLength + "." + degree);
			file = create.getFile();
			tree = new BTree(degree, file);
			scan = new Scanner(file);

		}

		public long getNextKey() throws Exception {
			switch (state) {

			case START: {
				while (state == States.START) {

					line = scan.nextLine();

					if (line == null) {
						state = States.END;

					} else if (line.contains("ORIGIN")) {
						state = States.SEQUENCE;
					}
				}
			}
			case SEQUENCE: {

				while (state == States.IN_SEQUENCE) {

					String ch = scan.next();
					if (ch.contains("/")) {
						state = States.END_SEQUENCE;
						break;
					} else if (ch.contains("n")) {

						key = "";
						state = States.UNKNOWN_CHARS;
						break;
					} else if (ch.contains("a") || ch.contains("t")
							|| ch.contains("c") || ch.contains("g")) {
						if (key.length() == sequenceLength) {
							key = key.substring(1, sequenceLength);
							key += ch;
							return encode(key);
						} else {

							key += ch;
							if (key.length() == sequenceLength) {

								return encode(key);
							}
						}
					}

				}
			}
			case IN_SEQUENCE: {

				while (state == States.IN_SEQUENCE) {

					String ch = scan.next();
					if (ch.contains("/")) {

						state = States.END_SEQUENCE;
					} else if (ch.contains("a") || ch.contains("t")
							|| ch.contains("c") || ch.contains("g")) {
						key += ch;
						state = States.SEQUENCE;
					}
				}
				break;

			}

			case END_SEQUENCE: {

				line = scan.nextLine();
				if (line != null) {

					line.trim();
					if (line.equals("ORIGIN")) {

						state = States.SEQUENCE;
						break;
					}
				}else{
					
					state = States.END;
					break;
				}
			}

			}
			return 0; //for now
		}
	}

	public long encode(String seq) throws Exception {
		if (seq.length() != sequenceLength) {
			throw new Exception("String of length " + seq.length()
					+ " was passed to btree with sequenceLenght of "
					+ sequenceLength);
		}
		long sequence = 0;
		for (int i = 0; i < seq.length(); i++) {
			sequence = sequence << 2;
			char c = seq.charAt(i);
			if (c == 'a' || c == 'A') {
				sequence = sequence | 0x0L;
			} else if (c == 't' || c == 'T') {
				sequence = sequence | 0x3L;
			} else if (c == 'c' || c == 'C') {
				sequence = sequence | 0x1L;
			} else if (c == 'g' || c == 'G') {
				sequence = sequence | 0x2L;
			} else {
				throw new Exception("Unexpected character: " + c);
			}
		}
		return sequence;
	}

	private static void printUsage() {
		System.err
				.println("GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> "
						+ "<sequence length> 1<=k<=31>"
						+ "[<cache size>] [<debug level>]"
						+ "[<debug level>]\n");
	}
}
