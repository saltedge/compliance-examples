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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saltedge.connector.ob.sdk.SDKConstants;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * Open Banking Account identifier
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ObAccountIdentifier {
  /**
   * Name of the identification scheme, in a coded form as published in an external list.
   * Allowed values: UK.OBIE.SortCodeAccountNumber, UK.OBIE.IBAN, UK.OBIE.PAN, UK.OBIE.BICFI
   */
  @JsonProperty(SDKConstants.KEY_SCHEME_NAME)
  @NotEmpty
  public String schemeName;

  /**
   * Identification assigned by an institution to identify an account. This identification is known by the account owner.
   */
  @JsonProperty(SDKConstants.KEY_IDENTIFICATION)
  @NotEmpty
  public String identification;

  /**
   * The account name is the name or names of the account owner(s) represented at an account level, as displayed by the ASPSP's online channels.
   * Note, the account name is not the product name or the nickname of the account.
   */
  @JsonProperty(SDKConstants.KEY_NAME)
  public String name;

  /**
   * This is secondary identification of the account, as assigned by the account servicing institution.
   * This can be used by building societies to additionally identify accounts with a roll number (in addition to a sort code and account number combination).
   */
  @JsonProperty("secondary_identification")
  public String secondaryIdentification;

  public ObAccountIdentifier() {
  }

  public ObAccountIdentifier(String schemeName, String identification) {
    this.schemeName = schemeName;
    this.identification = identification;
  }

  public ObAccountIdentifier(String schemeName, String identification, String name, String secondaryIdentification) {
    this.schemeName = schemeName;
    this.identification = identification;
    this.name = name;
    this.secondaryIdentification = secondaryIdentification;
  }

  @JsonIgnore
  public boolean isIban() {
    return "UK.OBIE.IBAN".equals(schemeName);
  }

  @JsonIgnore
  public boolean isAccountNumber() {
    return "UK.OBIE.SortCodeAccountNumber".equals(schemeName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ObAccountIdentifier that = (ObAccountIdentifier) o;
    return Objects.equals(schemeName, that.schemeName) && Objects.equals(identification, that.identification) && Objects.equals(name, that.name) && Objects.equals(secondaryIdentification, that.secondaryIdentification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemeName, identification, name, secondaryIdentification);
  }
}
