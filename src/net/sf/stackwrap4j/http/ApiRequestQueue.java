package net.sf.stackwrap4j.http;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import net.sf.stackwrap4j.http.HttpClient.ApiRequest;

public class ApiRequestQueue extends Thread {

	private static final long WAIT_TIME = 170;
	private static final long SLEEP_TIME = 1700;
	private Queue<Future> requests;

	public ApiRequestQueue() {
		requests = new LinkedList<Future>();
		start();
	}

	public Future offer(HttpClient.ApiRequest req) {
		Future fut = new Future(req);
		requests.offer(fut);
		return fut;
	}

	public void run() {
		while (true) {
			if (requests.isEmpty()) {
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
				}
				continue;
			}
			Future fut = requests.poll();
			try {
				HttpClient.ApiRequest req = fut.getReq();
				String result = req.makeRequest();
				fut.set(result);
			} catch (IOException e) {
				fut.setException(e);
			}
			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
			}
		}
	}

	public static class Future {
		private final ApiRequest req;
		private IOException e;
		private String reqResult;

		public Future(HttpClient.ApiRequest req) {
			this.req = req;
		}

		public String get() {
			while (reqResult == null && e == null) {

			}
			return reqResult;
		}

		public ApiRequest getReq() {
			return req;
		}

		public IOException getException(){
			return e;
		}
		
		protected void set(String result) {
			reqResult = result;
		}
		
		protected void setException(IOException e){
			this.e = e;
		}
	}
}
