package concurrent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class App {
	protected static Logger logger = LoggerFactory.getLogger(App.class);
	public static void main (String[] args) {
		URL[] topSites = null;
		try {
			topSites = new URL[] {new URL("http://www.google.com")};
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		logger.debug("in main");
		ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
		for (final URL siteUrl : topSites) {
		    final ListenableFuture<String> future = pool.submit(new Callable<String>() {
		        @Override
		        public String call() throws Exception {
		        	logger.debug("in call()");
		        	Thread.sleep(100);
		        	return siteUrl.toString();
		        }
		    });

		    future.addListener(new Runnable() {
		        @Override
		        public void run() {
		            try {
		                final String contents = future.get();
		                logger.debug("listener[1]: {}", contents);
		                Thread.sleep(100);
		            } catch (InterruptedException e) {
		            } catch (ExecutionException e) {
		            } finally {
		            	logger.debug("job completed.");
		            }
		        }
		    }, MoreExecutors.sameThreadExecutor());

		    future.addListener(new Runnable() {
		        @Override
		        public void run() {
		            try {
		                final String contents = future.get();
		                logger.debug("listener[2]: {}", contents);
		                Thread.sleep(100);
		            } catch (InterruptedException e) {
		            } catch (ExecutionException e) {
		            } finally {
		            	logger.debug("job completed.");
		            }
		        }
		    }, MoreExecutors.sameThreadExecutor());
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pool.shutdownNow();
	}
}
