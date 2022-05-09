package com.kve.master.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IpUtil {
	private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

	/**
	 * 获取本机ip
	 * @return ip
	 */
	public static String getIp() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress address = null;
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				Enumeration<InetAddress> addresses = ni.getInetAddresses();
				while (addresses.hasMoreElements()) {
					address = addresses.nextElement();
					if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
						return address.getHostAddress();
					}
				}
			}
			logger.info("xxl job getHostAddress fail");
			return null;
		} catch (Throwable t) {
			logger.error("xxl job getHostAddress error, {}", t);
			return null;
		}
	}

}
