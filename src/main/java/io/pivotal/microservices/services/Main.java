/*
 * File: Main.java
 * Creation Date: 12 Aug 2021
 *
 * Copyright (c) 2021 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses  this file to you under the Apache License, Version
 * 2.0 (the "License");  you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.microservices.services;

import java.net.InetAddress;

import io.pivotal.microservices.services.accounts.AccountsServer;
import io.pivotal.microservices.services.registration.RegistrationServer;
import io.pivotal.microservices.services.web.WebServer;

/**
 * Allow the servers to be invoked from the command-line. The jar is built with
 * this as the <code>Main-Class</code> in the jar's <code>MANIFEST.MF</code>.
 *
 * @author Paul Chapman
 */
public class Main {

    public static final String NO_VALUE = "NO-VALUE";

    public static void main(String[] args) {

        String serverName = NO_VALUE;
        String port = null;

        // Eureka server assumed to be on localhost
        System.setProperty(RegistrationServer.REG_SERVER_HOSTNAME_KEY, "localhost");

        // Look for server name and (optional) port property
        // Ignore any -- arguments intended for Spring Boot
        for (String arg : args) {
            if (arg.startsWith("--"))
                continue;

            if (serverName.equals(NO_VALUE))
                serverName = arg;
            else if (port == null)
                port = arg;
            else {
                System.out.println("Unexpected argument: " + arg);
                usage();
                return;
            }
        }

        // No server name supplied, print usage and exit
        if (serverName.equals(NO_VALUE)) {
            usage();
            return;
        }

        // Override port, if specified
        if (port != null)
            System.setProperty("server.port", port);

        // Get IP address, useful when running in containers
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("Running on IP: " + inetAddress.getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Determine which role this application will run as
        switch (serverName) {
            case "registration", "reg" -> RegistrationServer.main(args);
            case "accounts" -> AccountsServer.main(args);
            case "web" -> WebServer.main(args);
            default -> {
                // Unrecognized server type - print usage and exit
                System.out.println("Unknown server type: " + serverName);
                usage();
            }
        }
    }

    /**
     * Print application usage information to console.
     */
    protected static void usage() {
        System.out.println();
        System.out.println("Usage: java -jar ... <server-name> [server-port]");
        System.out.println("     where");
        System.out.println("       server-name is 'reg', 'registration', 'accounts' or 'web'");
        System.out.println("       server-port > 1024");
        System.out.println("     optionally specify --registration.server.hostname=<IP-address> if it is not running on localhost,");
        System.out.println();
    }
}
