package com.yj.homebasic.domain;

public class Drug {
	private int drugId;
	private String drugName;
	private String drugDescription;
	private String drugDosage;
	
	public Drug() {}
	public Drug(String drugName){
		this.drugName = drugName;
	}
	@Override
	public String toString() {
		return "Drug [drugId=" + drugId + ", drugName=" + drugName + ", drugDescription=" + drugDescription
				+ ", drugDosage=" + drugDosage + "]";
	}
	public int getDrugId() {
		return drugId;
	}
	public void setDrugId(int drugId) {
		this.drugId = drugId;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public String getDrugDescription() {
		return drugDescription;
	}
	public void setDrugDescription(String drugDescription) {
		this.drugDescription = drugDescription;
	}
	public String getDrugDosage() {
		return drugDosage;
	}
	public void setDrugDosage(String drugDosage) {
		this.drugDosage = drugDosage;
	}
	
}
