package wniemiec.util.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SchedulerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final int SCHEDULE_ID;
	private static final long DELAY_TIME;
	private static final long INTERVAL_TIME;
	private static final long MAX_WAIT_TIME;
	private volatile boolean insideRoutine;
	private int totInsideRoutine;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	static {
		SCHEDULE_ID = 1;
		DELAY_TIME = 1000L;
		INTERVAL_TIME = 100L;
		MAX_WAIT_TIME = 1000L;
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@Before
	public void beforeEachTest() {
		insideRoutine = false;
		totInsideRoutine = 0;
		Scheduler.clearAllTimeout();
		Scheduler.clearAllInterval();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void testTimeout() throws InterruptedException {
		Scheduler.setTimeout(() -> {
			insideRoutine = true;
		}, SCHEDULE_ID, DELAY_TIME);
		
		waitForTimeout();
		
		assertTrue(insideRoutine);
	}
	
	@Test
	public void testInterval() throws InterruptedException {
		totInsideRoutine = 0;
		
		Scheduler.setInterval(() -> {
			totInsideRoutine++;
			
			if (totInsideRoutine == 3)
				Scheduler.clearInterval(SCHEDULE_ID);
		}, SCHEDULE_ID, INTERVAL_TIME);
		
		waitForInterval(3);
		
		assertTrue(totInsideRoutine == 3);
	}
	
	@Test
	public void testClearTimeout() throws InterruptedException {
		Scheduler.setTimeout(() -> {
			insideRoutine = true;
		}, SCHEDULE_ID, DELAY_TIME);
		
		Scheduler.clearTimeout(SCHEDULE_ID);
		
		waitForTimeout();
		
		assertFalse(insideRoutine);
	}
	
	@Test
	public void testClearInterval() throws InterruptedException {
		totInsideRoutine = 0;
		
		Scheduler.setInterval(() -> {
			totInsideRoutine++;
		}, SCHEDULE_ID, INTERVAL_TIME);
		
		Scheduler.clearInterval(SCHEDULE_ID);
		
		waitForInterval(2);
		
		assertTrue(totInsideRoutine == 0);
	}
	
	@Test
	public void test2TimeoutsWithSameId() throws InterruptedException {
		boolean wasCreated = Scheduler.setTimeout(() -> {
			insideRoutine = true;
		}, SCHEDULE_ID+1, DELAY_TIME);
		
		boolean wasCreated2 = Scheduler.setTimeout(() -> {
			insideRoutine = true;
		}, SCHEDULE_ID+1, DELAY_TIME);
		
		Scheduler.clearTimeout(SCHEDULE_ID+1);
		
		waitForTimeout();
		
		assertTrue(wasCreated);
		assertFalse(wasCreated2);
	}
	
	@Test
	public void test2IntervalsWithSameId() throws InterruptedException {
		totInsideRoutine = 0;
		
		boolean wasCreated = Scheduler.setInterval(() -> {
			totInsideRoutine++;
		}, SCHEDULE_ID, INTERVAL_TIME);
		
		boolean wasCreated2 = Scheduler.setInterval(() -> {
			totInsideRoutine++;
		}, SCHEDULE_ID, INTERVAL_TIME);
		
		Scheduler.clearInterval(SCHEDULE_ID);
		
		waitForTimeout();
		
		assertTrue(wasCreated);
		assertFalse(wasCreated2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTimeoutWithNullRoutine() {
		Scheduler.setTimeout(null, SCHEDULE_ID, DELAY_TIME);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntervalWithNullRoutine() {
		Scheduler.setInterval(null, SCHEDULE_ID, INTERVAL_TIME);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTimeoutWithNegativeId() {
		Scheduler.setTimeout(() -> {}, -1, DELAY_TIME);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntervalWithNegativeId() {
		Scheduler.setInterval(() -> {}, -1, DELAY_TIME);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTimeoutWithNegativeDelay() {
		Scheduler.setTimeout(() -> {}, SCHEDULE_ID, -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntervalWithNegativeDelay() {
		Scheduler.setInterval(() -> {}, SCHEDULE_ID, -1);
	}

	@Test
	public void testSetTimeoutToRoutine() {
		boolean timeout = Scheduler.setTimeoutToRoutine(() -> {
			while (true)
				;
		}, MAX_WAIT_TIME);

		assertTrue(timeout);
	}

	@Test
	public void testSetTimeoutToRoutine2() {
		boolean timeout = Scheduler.setTimeoutToRoutine(() -> {
			int x = 1;

			while (x >= 0)
				x--;
		}, 99000);

		assertFalse(timeout);
	}


	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------\
	private void waitForInterval(int totalExecutions) throws InterruptedException {
		Thread.sleep((INTERVAL_TIME * totalExecutions) + 100);
	}

	private void waitForTimeout() throws InterruptedException {
		Thread.sleep(DELAY_TIME + 100);
	}

}
