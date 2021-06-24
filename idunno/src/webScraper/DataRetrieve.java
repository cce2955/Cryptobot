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
