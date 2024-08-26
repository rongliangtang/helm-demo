import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloKubernetes {

    public static void main(String[] args) throws IOException {
        // 用数组来保存主机名，使其能够被内部类访问
        final String[] hostHolder = new String[1];
        try {
            hostHolder[0] = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostHolder[0] = "unknown";
        }

        // 获取环境变量
        String message = System.getenv("MESSAGE");
        String namespace = System.getenv("NAMESPACE");
        String dbURL = System.getenv("DB_URL");
        String dbPassword = System.getenv("DB_PASSWORD");

        // 创建一个 HttpServer 实例，监听端口 3000
        HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);

        // 定义处理函数，当访问根路径时返回响应
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = String.format(
                    "[v6] Hello, Helm! Message from helm values: %s, From namespace: %s, From host: %s, Get Database Connect URL: %s, Database Connect Password: %s",
                    message, namespace, hostHolder[0], dbURL, dbPassword
                );
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        // 启动服务器
        server.setExecutor(null); // 使用默认的执行器
        server.start();
    }
}
