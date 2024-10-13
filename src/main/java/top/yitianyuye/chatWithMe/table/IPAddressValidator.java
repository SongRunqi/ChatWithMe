package top.yitianyuye.chatWithMe.table;

import java.net.*;
import java.util.*;

public class IPAddressValidator {

    public static void main(String[] args) {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        String hostAddress = localHost.getHostAddress();
        System.out.println("hostAddress = " + hostAddress);
        List<InetAddress> validIPs = getValidIPv4Addresses();
        System.out.println("有效的IPv4地址:");
        for (InetAddress ip : validIPs) {
            System.out.println(ip.getHostAddress());
        }

        InetAddress bestIP = selectBestIP(validIPs);
        if (bestIP != null) {
            System.out.println("\n最佳IP地址: " + bestIP.getHostAddress());
        } else {
            System.out.println("\n无法确定最佳IP地址");
        }
    }

    public static List<InetAddress> getValidIPv4Addresses() {
        List<InetAddress> validIPs = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                if (netint.isUp() && !netint.isLoopback()) {
                    for (InetAddress inetAddress : Collections.list(netint.getInetAddresses())) {
                        if (inetAddress instanceof Inet4Address && isValidIPv4(inetAddress)) {
                            validIPs.add(inetAddress);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("获取网络接口时出错: " + e.getMessage());
        }
        return validIPs;
    }

    private static boolean isValidIPv4(InetAddress inetAddress) {
        if (inetAddress.isLoopbackAddress() || inetAddress.isLinkLocalAddress()) {
            return false;
        }
        String ip = inetAddress.getHostAddress();
        String[] octets = ip.split("\\.");
        if (octets.length != 4) {
            return false;
        }
        for (String octet : octets) {
            int value = Integer.parseInt(octet);
            if (value < 0 || value > 255) {
                return false;
            }
        }
        return true;
    }

    public static InetAddress selectBestIP(List<InetAddress> validIPs) {
        // 优先选择非私有IP
        Optional<InetAddress> publicIP = validIPs.stream()
                .filter(ip -> !ip.isSiteLocalAddress())
                .findFirst();

        if (publicIP.isPresent()) {
            return publicIP.get();
        }

        // 如果没有公网IP，选择私有IP中的第一个
        Optional<InetAddress> privateIP = validIPs.stream()
                .filter(InetAddress::isSiteLocalAddress)
                .findFirst();

        return privateIP.orElse(null);
    }
}