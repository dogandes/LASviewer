package com.visualAnalitycs.demo;

public class WellInformation {

	private String mnemonic;
	private String units;
	private String data;
	private String description;
	
	/**
	 * @param mnemonic
	 * @param units
	 * @param data
	 * @param description
	 */
	public WellInformation(String mnemonic, String units, String data, String description) {
		super();
		this.mnemonic = mnemonic;
		this.units = units;
		this.data = data;
		this.description = description;
	}

	/**
	 * @return the mnemonic
	 */
	public String getMnemonic() {
		return mnemonic;
	}

	/**
	 * @param mnemonic the mnemonic to set
	 */
	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
