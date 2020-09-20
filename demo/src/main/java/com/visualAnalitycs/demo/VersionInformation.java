package com.visualAnalitycs.demo;

public class VersionInformation {
	
	private String versionDescription;
	private double version;
	private boolean wrap;
	
	public VersionInformation(String versionDescription, double version, boolean wrap) {
		this.versionDescription = versionDescription;
		this.version = version;
		this.wrap = wrap;
	}

	/**
	 * @return the versionDescription
	 */
	public String getVersionDescription() {
		return versionDescription;
	}

	/**
	 * @param versionDescription the versionDescription to set
	 */
	public void setVersionDescription(String versionDescription) {
		this.versionDescription = versionDescription;
	}

	/**
	 * @return the version
	 */
	public double getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(double version) {
		this.version = version;
	}

	/**
	 * @return the wrap
	 */
	public boolean isWrap() {
		return wrap;
	}

	/**
	 * @param wrap the wrap to set
	 */
	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}
	
}
