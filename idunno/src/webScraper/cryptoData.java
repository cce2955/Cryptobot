package webScraper;

public class cryptoData {
	private String last_updated;
	private String name;
	private String symbol;
	private double market_cap;
	private double quote;
	private double volume24h;
	private double total_supply;
	private double circulating_supply;
	private double max_supply;
	private boolean sha256;
	private boolean mineable;
	private boolean proof_of_work;
	
	public cryptoData(String last_updated, String name, String symbol, double market_cap, double quote,
			double volume24h, double total_supply, double circulating_supply, double max_supply, boolean sha256,
			boolean mineable, boolean proof_of_work) {
		super();
		this.last_updated = last_updated;
		this.name = name;
		this.symbol = symbol;
		this.market_cap = market_cap;
		this.quote = quote;
		this.volume24h = volume24h;
		this.total_supply = total_supply;
		this.circulating_supply = circulating_supply;
		this.max_supply = max_supply;
		this.sha256 = sha256;
		this.mineable = mineable;
		this.proof_of_work = proof_of_work;
	}
	
	
	public boolean isProof_of_work() {
		return proof_of_work;
	}


	public void setProof_of_work(boolean proof_of_work) {
		this.proof_of_work = proof_of_work;
	}


	public void setTotal_supply(double total_supply) {
		this.total_supply = total_supply;
	}


	public void setCirculating_supply(double circulating_supply) {
		this.circulating_supply = circulating_supply;
	}


	public void setMax_supply(double max_supply) {
		this.max_supply = max_supply;
	}


	public String getLast_updated() {
		return last_updated;
	}
	public void setLast_updated(String last_updated) {
		this.last_updated = last_updated;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getMarket_cap() {
		return market_cap;
	}
	public void setMarket_cap(double market_cap) {
		this.market_cap = market_cap;
	}
	public double getQuote() {
		return quote;
	}
	public void setQuote(double quote) {
		this.quote = quote;
	}
	public double getVolume24h() {
		return volume24h;
	}
	public void setVolume24h(double volume24h) {
		this.volume24h = volume24h;
	}
	public double getTotal_supply() {
		return total_supply;
	}
	public void setTotal_supply(int total_supply) {
		this.total_supply = total_supply;
	}
	public double getCirculating_supply() {
		return circulating_supply;
	}
	public void setCirculating_supply(int circulating_supply) {
		this.circulating_supply = circulating_supply;
	}
	public double getMax_supply() {
		return max_supply;
	}
	public void setMax_supply(int max_supply) {
		this.max_supply = max_supply;
	}
	public boolean isSha256() {
		return sha256;
	}
	public void setSha256(boolean sha256) {
		this.sha256 = sha256;
	}
	public boolean isMineable() {
		return mineable;
	}
	public void setMineable(boolean mineable) {
		this.mineable = mineable;
	}
	
	
}
