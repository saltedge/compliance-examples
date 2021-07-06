/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2020 Salt Edge.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.saltedge.connector.sdk.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.saltedge.connector.sdk.SDKConstants.KEY_ACCOUNT;
import static com.saltedge.connector.sdk.SDKConstants.KEY_NAME;

/**
 * Creditor/Debtor address.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantAddress {

  /**
   * Street of Creditor/Debtor
   */
  @JsonProperty("street_name")
  private String street;

  /**
   * Building number of Creditor/Debtor
   */
  @JsonProperty("building_number")
  private String buildingNumber;

  /**
   * City of Creditor/Debtor
   */
  @JsonProperty("town_name")
  private String town;

  /**
   * Postal code of Creditor/Debtor
   */
  @JsonProperty("post_code")
  private String postCode;

  /**
   * Country code of Creditor/Debtor
   */
  @JsonProperty("country")
  private String country;

  public ParticipantAddress() {
  }

  public ParticipantAddress(String country) {
    this.country = country;
  }

  public ParticipantAddress(String street, String buildingNumber, String town, String postCode, String country) {
    this.street = street;
    this.buildingNumber = buildingNumber;
    this.town = town;
    this.postCode = postCode;
    this.country = country;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getBuildingNumber() {
    return buildingNumber;
  }

  public void setBuildingNumber(String buildingNumber) {
    this.buildingNumber = buildingNumber;
  }

  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
