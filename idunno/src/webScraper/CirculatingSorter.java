package webScraper;

import java.util.Comparator;

public class CirculatingSorter implements Comparator<cryptoData>{

	@Override
	public int compare(cryptoData o, cryptoData o2) {

		if(o2.getCirculating_supply() < o.getCirculating_supply()) {
			return 1;
		}
		if (o.getCirculating_supply() < o2.getCirculating_supply()) {
			return -1;
		}
		return 0;
	}

}
