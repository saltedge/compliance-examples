/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2021 Salt Edge.
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
package com.saltedge.connector.ob.sdk.provider.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information that locates and identifies a specific address, as defined by postal services or in free format text.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ObAddress {

  /**
   * Information that locates and identifies a specific address, as defined by postal services, presented in free format text.
   */
  @JsonProperty("address_line")
  public String addressLine;

  /**
   * Identifies the nature of the postal address.
   * Allowed values: Business, Correspondence, DeliveryTo, MailTo, POBox, Postal, Residential, Statement
   */
  @JsonProperty("address_type")
  public String addressType;

  /**
   * Identification of a division of a large organisation or building.
   */
  @JsonProperty("department")
  public String department;

  /**
   * Identification of a sub-division of a large organisation or building.
   */
  @JsonProperty("sub_department")
  public String subDepartment;

  /**
   * Name of a street or thoroughfare.
   */
  @JsonProperty("street_name")
  public String streetName;

  /**
   * Number that identifies the position of a building on a street.
   */
  @JsonProperty("building_number")
  public String buildingNumber;

  /**
   * Identifier consisting of a group of letters and/or numbers that is added to a postal address to assist the sorting of mail.
   */
  @JsonProperty("post_code")
  public String postCode;

  /**
   * Name of a built-up area, with defined boundaries, and a local government.
   */
  @JsonProperty("town_name")
  public String townName;

  /**
   * Identifies a subdivision of a country such as state, region, county.
   */
  @JsonProperty("country_sub_division")
  public String countrySubDivision;

  /**
   * Nation with its own government.
   */
  @JsonProperty("country")
  public String country;
}
