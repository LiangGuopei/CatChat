package cn.catver.cat.chat;

import cn.catver.cat.chat.db.DBControl;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
    public static void main(String[] args) throws IOException {
        DBControl.initDB();
        IoAcceptor ioAcceptor = new NioSocketAcceptor();

        ioAcceptor.getFilterChain().addLast("logger",new LoggingFilter());
        ioAcceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory(StandardCharsets.UTF_8)));

        ioAcceptor.setHandler(new ChatHandler());
        ioAcceptor.bind(new InetSocketAddress(23334));
    }
}