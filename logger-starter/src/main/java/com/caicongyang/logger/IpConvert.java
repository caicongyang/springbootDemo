package com.caicongyang.logger;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class IpConvert extends ClassicConverter {
    private static final Logger log = LoggerFactory.getLogger(IpConvert.class);
    private static String IP_AND_ADDRESS = null;

    public IpConvert() {
    }

    public String convert(ILoggingEvent event) {
        return IP_AND_ADDRESS;
    }

    public static String normalizeHostAddress(InetAddress localHost) {
        return localHost.getHostAddress() + " | " + localHost.getHostName();
    }

    static {
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            ArrayList ipv4Result = new ArrayList();

            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();
                Enumeration en = networkInterface.getInetAddresses();

                while (en.hasMoreElements()) {
                    InetAddress address = (InetAddress) en.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        ipv4Result.add(normalizeHostAddress(address));
                    }
                }
            }

            if (!ipv4Result.isEmpty()) {
                IP_AND_ADDRESS = (String) ipv4Result.get(0);
            } else {
                InetAddress localHost = InetAddress.getLocalHost();
                IP_AND_ADDRESS = normalizeHostAddress(localHost);
            }
        } catch (SocketException var5) {
            log.error("SocketException", var5);
        } catch (UnknownHostException var6) {
            log.error("UnknownHostException", var6);
        }

    }
}