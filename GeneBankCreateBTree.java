public class GeneBankCreateBTree{




	public static void main (String[] args){

		int degree, seqLength, cacheSize, usesCache = 0;
		int debug = -1;
		String fileName = "";
	



	}



	public void setVariables(){

		usesCache = Integer.parseInt(args[0]);
		degree = Integer.parseInt(args[1]);
		fileName = args[2];
		seqLength = Integer.parseInt(args[3]);

		if(args[4] != null){
			cacheSize = Integer.parseInt(args[4]);
		}
		if(args[5] != null){
			debug = Integer.parseInt(args[5]);
		}
	}
}
