package com.github.bysrhq.anycart.android.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction {
	private String id;
	private boolean status;
	private double total;
	private Date date;
	private String customer;
	private List<TransactionDetail> transactionDetails = new ArrayList<TransactionDetail>();
	private User sales;
	private User cashier;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public List<TransactionDetail> getTransactionDetails() {
		return transactionDetails;
	}
	public void setTransactionDetails(List<TransactionDetail> transactionDetails) {
		this.transactionDetails = transactionDetails;
	}
	public User getSales() {
		return sales;
	}
	public void setSales(User sales) {
		this.sales = sales;
	}
	public User getCashier() {
		return cashier;
	}
	public void setCashier(User cashier) {
		this.cashier = cashier;
	}

}
