package idunno;

import java.io.IOException;

import webScraper.DataRetrieve;

public class Idunno {

	public static void main(String[] args) throws IOException {
		//Entry Point, program has no specific purpose
		DataRetrieve dRetrieve = new DataRetrieve();
		
		
		dRetrieve.getCoinData();

	}

}
