package webScraper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;








public class DataRetrieve {
	private final static String apiKey = "d1f411d8-a125-4c53-8dda-af5d1992fcee";
	private final String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
	 
	
	
	public void getCoinData() throws IOException {
	 ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		    parameters.add(new BasicNameValuePair("start","1"));
		    parameters.add(new BasicNameValuePair("limit","1000"));
		    parameters.add(new BasicNameValuePair("convert","USD"));
	
		try {
				//Get JSON Data
		      	String result = makeAPICall(uri, parameters);
		      	//System.out.println(result);
		      	
		      
		    } catch (IOException e) {
		      System.out.println("Error: cannont access content - " + e.toString());
		    } catch (URISyntaxException e) {
		      System.out.println("Error: Invalid URL " + e.toString());
		    }

	}
	
	//Code is kind of a mess, will be making better modules in the future
	public static String makeAPICall(String uri, ArrayList<NameValuePair> parameters)
		      throws URISyntaxException, IOException {
		    String response_content = "";

		    URIBuilder query = new URIBuilder(uri);
		    query.addParameters(parameters);
		    CloseableHttpClient client = HttpClients.createDefault();
		    
		    HttpGet request = new HttpGet(query.build());
		    
		    request.setHeader(HttpHeaders.ACCEPT, "application/json");
		    
		    request.addHeader("X-CMC_PRO_API_KEY", apiKey);

		    CloseableHttpResponse response = client.execute(request);

		    try {
		      System.out.println(response.getStatusLine());
		      
		      HttpEntity entity = response.getEntity();
		      //Get the data and throw it in here
		      response_content = EntityUtils.toString(entity);
		      //Get that data and throw it in here
		      JSONObject jsonObject = new JSONObject(response_content);
		      //get THAT data and throw IT in HERE
		      JSONArray arr = new JSONArray().putAll(jsonObject.get("data"));
		      //Some places to store stuff
		      HashMap<String, String> cryptoValues = new HashMap<>();
		      //Coins array
		      ArrayList<cryptoData> coins = new ArrayList<>();
		      //Coins Market Cap
		      ArrayList<cryptoData> coinsMarketCapArr = new ArrayList<>();
		      //Coins Circ Supply
		      ArrayList<cryptoData> coinsCirculatingArr = new ArrayList<>();
		      StringBuilder sb = new StringBuilder();
		 
		      for(int i = 0; i < arr.length();i++) {
		    	  sb.append(arr.get(i));
		    	  //I was going to do an actual parse but since the strings are static I'll just do this
		    	  //no doubt it'll break in a few years when CMC changes their API but whatever
		    	  double maxSupply; 
		    	  double totalSupply;
		    	  double circulatingSupply;
		    	  double volume24h;
		    	  double marketCap;
		    	  
		    	  try {
		    		  volume24h = Double.parseDouble(sb.substring(sb.indexOf("volume_24h\":") + 12, sb.indexOf(",\"percent_change_90d")).toString());
		    	  }catch(NumberFormatException e) {
		    		  volume24h = -1;
		    	  }
		    	  try {
		    		  totalSupply = Double.parseDouble(sb.substring(sb.indexOf("total_supply\":") + 14, sb.indexOf(",\"cmc_rank")).toString());
		    	  }catch(NumberFormatException e) {
		    		  totalSupply = -1;
		    	  }
		    	  try {
		    		  circulatingSupply = Double.parseDouble(sb.substring(sb.indexOf("circulating_supply\":") + 20, sb.indexOf(",\"last_updated")).toString());
		    	  }catch(NumberFormatException e) {
		    		  circulatingSupply = -1;
		    	  }
		    	  try {
		    		  maxSupply = Double.parseDouble(sb.substring(sb.indexOf("max_supply\":") + 12, sb.lastIndexOf(",\"id")).toString());
		    	  }catch(NumberFormatException e) {
		    		  maxSupply = -1;
		    	  }
		    	  try {
		    		  marketCap = 	  Double.parseDouble(sb.substring(sb.indexOf("market_cap\":") + 12, sb.indexOf(",\"price")).toString());
		    	  }catch (NumberFormatException e) {
		    		  marketCap = -1;
		    	  }
		    	  
		    	  System.out.println(sb.toString());
		    	  coins.add(new cryptoData(
		    			  //Last updated
		    			  
		    			  sb.substring(sb.indexOf("last_updated\":\""), sb.indexOf(",\"percent_change_24h")),
		    			  //Name
		    			  sb.substring(sb.indexOf("name\":\"") + 7, sb.indexOf("\",\"max_supply")), 
		    			  //Symbol
		    			  sb.substring(sb.indexOf("\"symbol\":\"") + 10, sb.indexOf(",\"circulating_supply") - 1).toString(),
		    			  //Market Cap
		    			  Double.parseDouble(sb.substring(sb.indexOf("market_cap\":") + 12, sb.indexOf(",\"price")).toString()),
		    			  //Quote
		    			  //Quote is a sub object, keeping it in case I need to make another class for it
		    			  i,
		    			  //So never call it..for now
		    			  volume24h,
		    			  totalSupply,
		    			  circulatingSupply,
		    			  maxSupply,
		    			  //All booleans are set below
		    			  //Mineable
		    			  false, 
		    			  //Sha-256
		    			  false,
		    			  //Proof of work
		    			  false));
		    	  
		    	  if(sb.indexOf("mineable") > -1) {
		    		  coins.get(i).setMineable(true);
		    	  }
		    	  if(sb.indexOf("sha-256") > -1) {
		    		  coins.get(i).setSha256(true);
		    	  }
		    	  if(sb.indexOf("pow") > -1) {
		    		  coins.get(i).setProof_of_work(true);
		    	  }
		    	  
		    	  sb.delete(0, sb.length());
		    	  
		      }
		      coinsMarketCapArr.addAll(coins);
		      coinsCirculatingArr.addAll(coins);
		      coinsMarketCapArr.sort(new MarketCapSorter());
		      coinsCirculatingArr.sort(new CirculatingSorter());
		      double circulatingBaseline = 0;
		      double marketCapBaseline = 0;
		      for(int i = 0; i < coins.size(); i++) {
		    	  System.out.println(i + " " + coins.get(i).getSymbol());
		      }
		      for(int i = 0; i < coinsMarketCapArr.size(); i++) {
		    	  System.out.println(coinsMarketCapArr.get(i).getSymbol() + " " + coinsMarketCapArr.get(i).getMarket_cap());
		    	  if(i == coinsMarketCapArr.size()/2) {
		    		  marketCapBaseline = coinsMarketCapArr.get(i).getCirculating_supply();
		    	  }
		      }
		      
		      for(int i = 0; i < coinsCirculatingArr.size(); i++) {
		    	  System.out.println(coinsCirculatingArr.get(i).getSymbol() + " " + coinsCirculatingArr.get(i).getCirculating_supply());
		    	  if(i == coinsCirculatingArr.size()/10) {
		    		  circulatingBaseline = coinsCirculatingArr.get(i).getCirculating_supply();
		    	  }
		      }
		      
		      System.out.println("Market Cap Baseline: " + marketCapBaseline + " || Circulating Cap Baseline: " + circulatingBaseline);
		      for(int i = 0; i < coinsCirculatingArr.size(); i++) {  
		    	  if(coinsCirculatingArr.get(i).getCirculating_supply() < circulatingBaseline & coinsCirculatingArr.get(i).getMarket_cap() < marketCapBaseline) {
		    		  System.out.println(coinsCirculatingArr.get(i).getSymbol()  + " || Market Cap: " +
		    				  	coinsCirculatingArr.get(i).getMarket_cap() +" ||Circulating: " + 
		    				  	coinsCirculatingArr.get(i).getCirculating_supply());
		    		  
		    	  }
		      }
		      //Organize coins by market cap
		      //coinsMarketCapArr.sort()
		     
		      		    
		     //Low Market Cap, low holders,  audited, doxxed, low circulating supply, key burned, fsystoken suppply burn, utility 
		      EntityUtils.consume(entity);
		    	}catch(Exception e){
		    		System.out.println("Could not get data");  
		    		}
		    return response_content;
	}
}

/*
 * private static String readAll(Reader rd) throws IOException {
		
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
 * private String getData() throws IOException {
		//This is a web scrape example I'll use it as a base for my twitter bot
		Document doc = Jsoup.connect("https://etherscan.io/gastracker").get();
		
		StringBuilder sb = new StringBuilder();
		//Add body in stringbuilder, I suspect there's a much better way
		//but this was just for testing purposes
		sb.append(doc.body());
		//Delete irrelevant stuff before and after
		sb.delete(0, sb.indexOf("Uniswap Swap") + 12);
		sb.delete(sb.indexOf("</tr>"), sb.length());
		//Get rid of HTML tags
		for (int i = 0; i < sb.length(); i++) {
			if(sb.indexOf("<td>") == i){
				sb.delete(sb.indexOf("<td>") - 1, sb.indexOf("<td>") + 4);
			}
			if(sb.indexOf("</td>") == i){
				sb.delete(sb.indexOf("</td>") , sb.indexOf("</td>") + 5);
			}	
			
		}
		//Up to the second date for gas
		sb.insert(0, "The Gas Range for Swapping tokens via Uniswap on " + new Date() + " is ");
		//System.out.println(sb);
		//and done!!
		return sb.toString();
	}	
	
 */




















































/*
public void shame() {
	//Everything in here is my shame, I will not delete it out of pride
	//I am grabbing a simple JSON parser I'm done
	StringBuilder sb = new StringBuilder(result);
  	
  	//There is some sort of weird way the data is being given to me, so a copy
  	//of the string builder will be made for reference.
  	
  	//System.out.println(result);
  	//Creating a hashmap to store data
  	
  	ArrayList<cryptoData> cryptoArray = new ArrayList<cryptoData>();
  	ArrayList<String> lastUpdatedArray = new ArrayList<>();
  	ArrayList<String> nameArray = new ArrayList<>();
  	ArrayList<String> symbolArray = new ArrayList<>();
  	ArrayList<Double> marketCapArray = new ArrayList<>();
  	ArrayList<Double> quoteArray = new ArrayList<>();
  	ArrayList<Double> volumeArray = new ArrayList<>();
  	ArrayList<Double> totalSupplyArray = new ArrayList<>();
  	ArrayList<Double> circulatingSupplyArray = new ArrayList<>();
  	ArrayList<Double> maxSupplyArray = new ArrayList<>();
  	ArrayList<Boolean> isSha256Array = new ArrayList<>();
  	ArrayList<Boolean> isMineableArray = new ArrayList<>();
  	//Counter to make sure all variables are populated correctly
  	int counter = 0;
  	
  	String stringToDouble = "";
  	
  	for(int i = 0; i < sb.length();i++) {
  		
  		if(sb.indexOf("data")== i) {
  			sb.delete(0, sb.indexOf("data") + 9);
  			i = 0;
  		}		      		
  		if(sb.indexOf("name") == i) {
  			nameArray.add(sb.substring(sb.indexOf("name")+7, sb.indexOf("symbol")-3));
  			sb.delete(sb.indexOf("name")+7, sb.indexOf("symbol")-3);
  			counter++;
  		}
  		if(sb.indexOf("symbol")==i) {
  			System.out.println("symbol " + sb.substring(sb.indexOf("symbol") + 9, sb.indexOf("slug")-3).toString());
  			symbolArray.add(sb.substring(sb.indexOf("symbol") + 9, sb.indexOf("slug")-3).toString());
  			sb.delete(sb.indexOf("symbol") + 9, sb.indexOf("slug")-3).toString();
  			counter++;
  		}
  		if(sb.indexOf("sha-256") == i) {
  			isSha256Array.add(true);
  		}
  		if(sb.indexOf("mineable") == i) {
  			isMineableArray.add(true);
  		}
  		if(sb.indexOf("max_supply")==i) {
  			System.out.println("Max Supply " + sb.substring(sb.indexOf("max_supply") + 12, sb.indexOf("circulating_supply") - 2).toString());
  			maxSupplyArray.add(Double.parseDouble(sb.substring(sb.indexOf("max_supply") + 12, sb.indexOf("circulating_supply") - 2).toString()));
  			sb.delete(sb.indexOf("max_supply") + 12, sb.indexOf("circulating_supply") - 2);
  			counter++;
  		}
  		if(sb.indexOf("total_supply")==i) {
  			System.out.println("Total Supply " + sb.substring(sb.indexOf("total_supply") + 14, sb.indexOf("platform") - 2));
  			totalSupplyArray.add(Double.parseDouble(sb.substring(sb.indexOf("total_supply") + 14, sb.indexOf("platform") - 2)));
  			sb.delete(sb.indexOf("total_supply") + 14, sb.indexOf("platform") - 2);     			counter++;
  		}
  		if(sb.indexOf("quote")==i) {
  			System.out.println("Quote " + sb.substring(sb.indexOf("price") + 7, sb.indexOf("volume_24h") -2));
  			quoteArray.add(Double.parseDouble(sb.substring(sb.indexOf("price") + 7, sb.indexOf("volume_24h") - 2)));
  			sb.delete(sb.indexOf("price") + 7, sb.indexOf("volume_24h") - 2);		      			counter++;
  		}
  		if(sb.indexOf("volume_24h")==i) {
  			System.out.println("24hr Volume " +sb.substring(sb.indexOf("volume_24h") + 12, sb.indexOf("percent_change_1h") - 2));
  			volumeArray.add(Double.parseDouble(sb.substring(sb.indexOf("volume_24h") + 12, sb.indexOf("percent_change_1h") -2 )));
  			sb.delete(sb.indexOf("volume_24h") + 12, sb.indexOf("percent_change_1h") -2 );		      			counter++;
  		}
  		if(sb.indexOf("circulating_supply")==i) {
  			System.out.println("Circulating Supply " +sb.substring(sb.indexOf("circulating_supply") + 20, sb.indexOf("total_supply") - 2));
  			circulatingSupplyArray.add(Double.parseDouble(sb.substring(sb.indexOf("circulating_supply") + 20, sb.indexOf("total_supply") -2 )));
  			sb.delete(sb.indexOf("circulating_supply") + 20, sb.indexOf("total_supply") -2 );
  			counter++;
  		}
  		if(sb.indexOf("market_cap")==i) {
  			if(sb.indexOf("market_cap") > sb.indexOf("last_updated")) {
  				//Ruin the spelling of "last_updated" so it'll append to the CORRECT market cap
  				//oh my god this was a frustrating bug.
  				sb.delete(sb.indexOf("last_updated"), sb.indexOf("last_updated") + 4);
  			}
  			System.out.println("Market Cap " + sb.substring(sb.indexOf("market_cap") + 12, sb.indexOf("last_updated") - 2));
  			marketCapArray.add(Double.parseDouble(sb.substring(sb.indexOf("market_cap") + 12, sb.indexOf("last_updated") - 2)));
  			sb.delete(sb.indexOf("market_cap") + 12, sb.indexOf("last_updated") - 2);
  			counter++;
  		}
  		
  		if(counter == 8) {
  			
  			if(sb.indexOf("id") < sb.indexOf("last_updated")) {
  				System.out.println(sb.toString());
  				//I have to cheat again because java REFUSES to recognize }}} in an "indexOf()" search
  				sb.delete(sb.indexOf("id"), sb.indexOf("id") + 2);
  				System.out.println(sb.toString());
  				sb.delete(0, sb.indexOf("id") - 1);
  				i = 0;
  				
  			}
  			counter = 0;
  			System.out.println(sb.toString());
  		}
  		
  }
  	for(int i = 0; i < nameArray.size(); i++) {
  		cryptoArray.add(new cryptoData("", nameArray.get(i), symbolArray.get(i), marketCapArray.get(i), 
  				quoteArray.get(i), volumeArray.get(i), totalSupplyArray.get(i), circulatingSupplyArray.get(i),
  				maxSupplyArray.get(i), isSha256Array.get(i), isMineableArray.get(i)));
  	}
  	
  	System.out.println(cryptoArray.size());
  	//This loop will be optimized soon enough, but when all variables satisfy 
  	//the max count, this loop will be active and CryptoArray will populate
//  	for (int i = 0; i < counter[0]; i++) {
//  		//updated, name, symbol, marketcap, quote, volume(24h), 
//  		//total supply, circulating supply, max supply, sha256, mineable
//	cryptoArray.add(new cryptoData("last updated", "name", "symbol"
//			, marketCapArray.get(i),0, 0, 0, 0, 0, false, false));  
//  }
//  	System.out.println(cryptoArray.get(0).getMarket_cap());
//  	System.out.println(cryptoArray.get(1).getMarket_cap());
//  	System.out.println(cryptoArray.get(2).getMarket_cap());
//  	System.out.println(cryptoArray.get(3).getMarket_cap());
}*/
