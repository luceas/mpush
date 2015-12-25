package com.shinemo.mpush.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import com.shinemo.mpush.api.Connection;
import com.shinemo.mpush.api.ConnectionInfo;
import com.shinemo.mpush.api.protocol.Packet;

/**
 * Created by ohun on 2015/12/22.
 */
public class NettyConnection implements Connection {

	private static final Logger log = LoggerFactory.getLogger(NettyConnection.class);

	private ConnectionInfo info;
	private Channel channel;
	private int status = 0;

	private int hbTimes;
	
	@Override
	public void init(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void setConnectionInfo(ConnectionInfo info) {
		this.info = info;
	}

	@Override
	public String getId() {
		return channel.id().asLongText();
	}

	@Override
	public void send(final Packet packet) {
		if (packet != null) {
			if (channel.isWritable()) {
				ChannelFuture wf = channel.writeAndFlush(packet);
				wf.addListener(new ChannelFutureListener() {
					
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(!future.isSuccess()){
							if(!channel.isActive()){
								log.warn("send msg false:"+channel.remoteAddress().toString()+","+packet+",channel is not active");
								ConnectionManager.INSTANCE.remove(channel);
							}
							log.warn("send msg false:"+channel.remoteAddress().toString()+","+packet);
						}else{
							log.warn("send msg success:"+channel.remoteAddress().toString()+","+packet);
						}
					}
				});
			}else{
				log.warn("send msg false:"+channel.remoteAddress().toString()+","+packet+", channel is not writable");
			}
		}
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	public ConnectionInfo getInfo() {
		return info;
	}

	public void setInfo(ConnectionInfo info) {
		this.info = info;
	}

	public Channel getChannel() {
		return channel;
	}

	public int increaseAndGetHbTimes(){
		return ++hbTimes;
	}
	
	public void resetHbTimes(){
		hbTimes = 0;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void close() {
		this.channel.close();
	}

	@Override
	public int getHbTimes() {
		return hbTimes;
	}
	
	@Override
	public boolean isConnected(){
		return channel.isActive();
	}
	
	@Override
	public boolean isEnable(){
		return channel.isWritable();
	}

	@Override
	public String remoteIp() {
		return channel.remoteAddress().toString();
	}
}
