package com.trademanagement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "trade")
public class Trade {

    @Id
    private String tradeId;
    private int version;

    @Column(name = "counter_party_id")
    private String counterPartyId;

    @Column(name = "book_id")
    private String bookId;

    @Column(name = "maturity_date")
    private Date maturityDate;

    @Column(name = "created_date")
    private Date createdDate;
    private char expired;

    public Trade(){
        //default impl
    }

    public Trade(Builder builder) {
        this.tradeId = builder.tradeId;
        this.version = builder.version;
        this.counterPartyId = builder.counterPartyId;
        this.bookId = builder.bookId;
        this.maturityDate = builder.maturityDate;
        this.createdDate = builder.createdDate;
        this.expired = builder.expired;
    }
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCounterPartyId() {
        return counterPartyId;
    }

    public void setCounterPartyId(String counterPartyId) {
        this.counterPartyId = counterPartyId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public char getExpired() {
        return expired;
    }

    public void setExpired(char expired) {
        this.expired = expired;
    }


    //Using builder pattern here since
    //there are multiple fields to be taken care of
    //and validated for the constructor
    public static final class Builder{
        String tradeId;
        int version;
        String counterPartyId;
        String bookId;
        Date maturityDate;
        Date createdDate;
        char expired;

        public Builder(){

        }

        public Builder tradeId(String val){
            tradeId = val;
            return this;
        }

        public Builder version(int val){
            version = val;
            return this;
        }

        public Builder counterPartyId(String val){
            counterPartyId = val;
            return this;
        }

        public Builder bookId(String val){
            bookId = val;
            return this;
        }

        public Builder maturityDate(Date val){
            maturityDate = val;
            return this;
        }

        public Builder createdDate(Date val){
            createdDate = val;
            return this;
        }

        public Builder expired(char val){
            expired = val;
            return this;
        }

        public Trade build(){
            return new Trade(this);
        }
    }


    @Override
    public String toString() {
        return "com.trademanagement.Trade{" +
                "tradeId='" + tradeId + '\'' +
                ", version=" + version +
                ", counterPartyId='" + counterPartyId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", maturityDate=" + maturityDate +
                ", createdDate=" + createdDate +
                ", expired=" + expired +
                '}';
    }
}
