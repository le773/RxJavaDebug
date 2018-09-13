import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Storage {
	private final int MAX_SIZE=10;
	private LinkedList<Object> list = new LinkedList<>();
	
	public void produce(String producer) {
		synchronized (list) {
			while(list.size() == MAX_SIZE) {
				try {
					System.out.println(Thread.currentThread().getName() + " list is full");
					list.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			list.add(new Object());
			System.out.println(Thread.currentThread().getName() + " add to list, size: " + list.size());
			list.notifyAll();
		}
	}
	
	public void consume(String consumer) {
		synchronized (list) {
			while(list.size() == 0) {
				try {
					System.out.println(Thread.currentThread().getName() + " list is empty");
					list.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			list.remove();
			System.out.println(Thread.currentThread().getName() + " list remove one: " + list.size());
			list.notifyAll();
		}
	}
	
	class Producer implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			produce("producer");
		}
	}
	
	class Consumer implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			consume("consumer");
		}
	}
}
//	
//	class Go {
//		boolean iContinue = true, jContinue=true;
//		int i=0, j=0;
//		public void produce() {
//			ThreadPoolExecutor exec = new ThreadPoolExecutor(2, 4, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
//			while(iContinue) {
//				i++;
//				exec.submit(new Producer());
//				if(i == 100) {
//					iContinue=false;
//				}
//			}
//		}
//		
//		public void consume() {
//			ThreadPoolExecutor exec = new ThreadPoolExecutor(2, 4, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
//			while(iContinue) {
//				j++;
//				exec.submit(new Consumer());
//				if(j == 100) {
//					jContinue=false;
//				}
//			}
//		}
//	}

public class StorageDebug {
	public static void mainDebug(String[] args) {
		Storage sd = new Storage();
		for(int i=0; i<40; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					sd.produce("p");
				}
			}).start();
		}
		for(int i=0; i<20; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					sd.consume("c");
				}
			}).start();
		}
	}
}
