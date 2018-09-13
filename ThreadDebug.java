import java.time.chrono.IsoChronology;

public class ThreadDebug {

	public static void mainDebug(String[] args) {
		// TODO Auto-generated method stub
		N2Thread nThread = new N2Thread();
		nThread.start();
		System.out.println(Thread.currentThread().getName() + " before interrupted " + System.currentTimeMillis());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nThread.setCancel();
	}
	static class NThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!interrupted()) {
				System.out.println(Thread.currentThread().getName() + " still alive");
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println(Thread.currentThread().getName() + " InterruptedException");
					Thread.currentThread().interrupt();
				}
			}
			System.out.println(Thread.currentThread().getName() + " after interrupted " + System.currentTimeMillis());
		}
	}
	
	static class N2Thread extends Thread {
		
		private boolean isCancel = false;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!isCancel) {
				System.out.println(Thread.currentThread().getName() + " still alive");
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println(Thread.currentThread().getName() + " InterruptedException");
					Thread.currentThread().interrupt();
				}
			}
			System.out.println(Thread.currentThread().getName() + " after interrupted " + System.currentTimeMillis());
		}
		
		public void setCancel() {
			isCancel=true;
		}
	}
}
