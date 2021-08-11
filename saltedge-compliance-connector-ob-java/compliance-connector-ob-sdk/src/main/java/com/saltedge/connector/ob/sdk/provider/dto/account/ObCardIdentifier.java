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
import com.saltedge.connector.ob.sdk.SDKConstants;

import javax.validation.constraints.NotEmpty;

/**
 * Open Banking Card identifier
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ObCardIdentifier {
  /**
   * Name of the card scheme.
   * Allowed values: AmericanExpress, Diners, Discover, MasterCard, VISA
   */
  @NotEmpty
  @JsonProperty("card_scheme_name")
  public String cardSchemeName;

  /**
   * The account name is the name or names of the account owner(s) represented at an account level, as displayed by the ASPSP's online channels.
   * Note, the account name is not the product name or the nickname of the account.
   */
  @JsonProperty(SDKConstants.KEY_NAME)
  public String name;

  /**
   * Identification assigned by an institution to identify the card instrument used in the transaction.
   * This identification is known by the account owner, and may be masked.
   */
  @JsonProperty(SDKConstants.KEY_IDENTIFICATION)
  public String identification;

  public ObCardIdentifier() {
  }

  public ObCardIdentifier(String cardSchemeName, String name, String identification) {
    this.cardSchemeName = cardSchemeName;
    this.name = name;
    this.identification = identification;
  }
}
