import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class StorageCondition {
	// ²Ö¿â×î´ó´æ´¢Á¿
	private final int MAX_SIZE = 10;
	// ²Ö¿â´æ´¢µÄÔØÌå
	private LinkedList<Object> list = new LinkedList<>();
	private ReentrantLock lock = new ReentrantLock();
	private final Condition produce = lock.newCondition();
	private final Condition consumer = lock.newCondition();
	
	public void consume() {
		lock.lock();
		while(list.size() == 0) {
			System.out.println("list is empyty");
			try {
				consumer.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		list.remove();
		System.out.println("consume size:" + list.size());
		produce.signalAll();
		System.out.println("produce could produce");
		lock.unlock();
	}
	
	public void produce() {
		lock.lock();
		while(list.size() == MAX_SIZE) {
			System.out.println("list is full");
			try {
				produce.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		list.add(new Object());
		System.out.println("produce size:" + list.size());
		consumer.signalAll();
		System.out.println("produce could consume");
		lock.unlock();
	}
}

public class StorageConditionDebug {
	public static void mainDebug(String[] args) {
		StorageCondition sc = new StorageCondition();
        for(int i=1;i<30;i++)
        {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sc.produce();
                }
            }).start();
        }

        for(int i=1;i<25;i++)
        {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sc.consume();
                }
            }).start();
        }
	}
}
