/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.http;

import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.util.Key;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;

/**
 * Tests {@link HttpResponse}.
 *
 * @author Yaniv Inbar
 */
public class HttpResponseTest extends TestCase {

  public HttpResponseTest() {
  }

  public HttpResponseTest(String name) {
    super(name);
  }

  public void testParseAsString_none() throws IOException {
    HttpTransport transport = new MockHttpTransport();
    HttpRequest request = transport.createRequestFactory().buildGetRequest(new GenericUrl());
    HttpResponse response = request.execute();
    assertEquals("", response.parseAsString());
  }

  public static class MyHeaders extends HttpHeaders {

    @Key
    public String foo;

    @Key
    public Object obj;

    @Key
    String[] r;
  }

  public void testHeaderParsing() throws IOException {
    HttpTransport transport = new MockHttpTransport() {
      @Override
      public LowLevelHttpRequest buildGetRequest(String url) throws IOException {
        return new MockLowLevelHttpRequest() {
          @Override
          public LowLevelHttpResponse execute() throws IOException {
            MockLowLevelHttpResponse result = new MockLowLevelHttpResponse();
            result.addHeader("accept", "value");
            result.addHeader("foo", "bar");
            result.addHeader("goo", "car");
            result.addHeader("hoo", "dar");
            result.addHeader("hoo", "far");
            result.addHeader("obj", "o");
            result.addHeader("r", "a1");
            result.addHeader("r", "a2");
            return result;
          }
        };
      }
    };
    HttpRequest request = transport.createRequestFactory().buildGetRequest(new GenericUrl());
    request.setResponseHeaders(new MyHeaders());
    HttpResponse response = request.execute();
    assertEquals("value", response.getHeaders().getAccept());
    assertEquals("bar", ((MyHeaders) response.getHeaders()).foo);
    assertEquals(Arrays.asList("o"), ((MyHeaders) response.getHeaders()).obj);
    assertEquals(Arrays.asList("a1", "a2"), Arrays.asList(((MyHeaders) response.getHeaders()).r));
    assertEquals(Arrays.asList("car"), response.getHeaders().get("goo"));
    assertEquals(Arrays.asList("dar", "far"), response.getHeaders().get("hoo"));
  }
}