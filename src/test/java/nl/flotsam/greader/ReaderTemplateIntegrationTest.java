/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.flotsam.greader;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ReaderTemplateIntegrationTest {

    private static String email;
    private static String password;

    @BeforeClass
    public static void configureExternalProperties() throws IOException {
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(new File(System.getProperty("user.home"), ".greader" + File.separator + "config.properties"));
            properties.load(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
        email = properties.getProperty("email");
        password = properties.getProperty("password");
    }

    @Test
    public void shouldObtainToken() {
        ReaderTemplate template = new ReaderTemplate(email, password);
        assertThat(template.getToken(), is(not(nullValue())));
    }

    @Test
    public void shouldAllowForSubscription() {
        ReaderTemplate template = new ReaderTemplate(email, password);
        String token = template.getToken();
        boolean result = template.subscribe("http://www.martinfowler.com/bliki/bliki.atom", "Martin", token);
        assertThat(result, is(true));
    }

    @Test
    public void shouldAllowForTaggingSubscription() {
        ReaderTemplate template = new ReaderTemplate(email, password);
        String token = template.getToken();
        template.subscribe("http://www.martinfowler.com/bliki/bliki.atom", "Martin", token);
        boolean result = template.tag("http://www.martinfowler.com/bliki/bliki.atom", "scala-tribes", token);
        assertThat(result, is(true));
    }

}

