package webScraper;

import java.util.Comparator;

public class MarketCapSorter implements Comparator<cryptoData> {

	@Override
	public int compare(cryptoData o, cryptoData o2) {
		
		if(o2.getMarket_cap() < o.getMarket_cap()) {
			return 1;
		}
		if (o.getMarket_cap() < o2.getMarket_cap()) {
			return -1;
		}
		return 0;
	}

}
