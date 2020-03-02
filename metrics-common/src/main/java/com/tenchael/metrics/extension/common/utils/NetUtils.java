/*
 * Copyright 1999-2011 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tenchael.metrics.extension.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

import static com.tenchael.metrics.extension.common.SwallowExceptionHandler.swallow;

/**
 * IP and Port Helper for RPC,
 *
 * @author shawn.qianx
 */

public class NetUtils {

	public static final String LOCALHOST = "127.0.0.1";

	public static final String ANYHOST = "0.0.0.0";

	private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

	private static volatile InetAddress LOCAL_ADDRESS = null;


	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress())
			return false;
		String name = address.getHostAddress();
		return (name != null
				&& !ANYHOST.equals(name)
				&& !LOCALHOST.equals(name)
				&& IP_PATTERN.matcher(name).matches());
	}

	public static String getLocalHost() {
		InetAddress address = getLocalAddress();
		return address == null ? LOCALHOST : address.getHostAddress();
	}


	/**
	 * 遍历本地网卡，返回第一个合理的IP。
	 *
	 * @return 本地网卡IP
	 */
	public static InetAddress getLocalAddress() {
		if (LOCAL_ADDRESS != null)
			return LOCAL_ADDRESS;
		InetAddress localAddress = getLocalAddress0();
		LOCAL_ADDRESS = localAddress;
		return localAddress;
	}

	public static String getLogHost() {
		InetAddress address = LOCAL_ADDRESS;
		return address == null ? LOCALHOST : address.getHostAddress();
	}

	private static InetAddress getLocalAddress0() {
		InetAddress localAddress = null;
		try {
			localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress;
			}
		} catch (Throwable e) {
			swallow(String.format("Failed to retriving ip address, %s",
					e.getMessage()), e);
		}
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces != null) {
				while (interfaces.hasMoreElements()) {
					try {
						NetworkInterface network = interfaces.nextElement();
						Enumeration<InetAddress> addresses = network.getInetAddresses();
						if (addresses != null) {
							while (addresses.hasMoreElements()) {
								try {
									InetAddress address = addresses.nextElement();
									if (isValidAddress(address)) {
										return address;
									}
								} catch (Throwable e) {
									swallow(String.format("Failed to retriving ip address, %s",
											e.getMessage()), e);
								}
							}
						}
					} catch (Throwable e) {
						swallow(String.format("Failed to retriving ip address, %s",
								e.getMessage()), e);
					}
				}
			}
		} catch (Throwable e) {
			swallow(String.format("Failed to retriving ip address, %s",
					e.getMessage()), e);
		}
		swallow(String.format("Could not get local host ip address, will use 127.0.0.1 instead."), null);
		return localAddress;
	}


}