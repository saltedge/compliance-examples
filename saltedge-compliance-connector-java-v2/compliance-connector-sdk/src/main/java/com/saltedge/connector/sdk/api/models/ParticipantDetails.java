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
import com.saltedge.connector.sdk.models.ParticipantAccount;

import static com.saltedge.connector.sdk.SDKConstants.KEY_ACCOUNT;
import static com.saltedge.connector.sdk.SDKConstants.KEY_NAME;

/**
 * Creditor/Debtor details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantDetails {
  /**
   * Name of the creditor if a "Debited" transaction or name of the debtor if a "Credited" transaction. Optional field.
   */
  @JsonProperty(KEY_NAME)
  public String name;

  /**
   * Wrapper of creditor/debtor account data
   */
  @JsonProperty(KEY_ACCOUNT)
  public ParticipantAccount account;

  public ParticipantDetails() {
  }

  public ParticipantDetails(String name, ParticipantAccount account) {
    this.name = name;
    this.account = account;
  }
}
