package com.slobodastudio.forismaticqoutations;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class QuotationData {
	public static final String QUOTATION_TEXT = "QUOTATION_TEXT";
	public static final String QUOTATION_AUTHOR = "QUOTATION_AUTHOR";
	public static final String FAVOURITE = "FAVOURITE";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = QUOTATION_TEXT)
	private String quotation;
	
	@DatabaseField(dataType = DataType.STRING, columnName = QUOTATION_AUTHOR)
	private String author;
	
	@DatabaseField(dataType = DataType.BOOLEAN, columnName = FAVOURITE)
	private boolean favourite;

	QuotationData() {
		//ORM Lite required.
	}
	
	public void setQuotation(String quotation) {
		this.quotation = quotation;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void setFavourite(Boolean favourite) {
		this.favourite = favourite;
	}
	
	public int getId() {
		return id;
	}
	
	public String getQuotation() {
		return quotation;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public Boolean getFavourite() {
		return favourite;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id);
		sb.append(", ").append("text=").append(quotation);
		sb.append(", ").append("author=").append(author);
		sb.append(", ").append("favourite=").append(favourite);
		return sb.toString();
	}
}