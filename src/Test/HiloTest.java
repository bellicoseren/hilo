package Test;

import org.junit.Test;
import org.junit.runner.RunWith;

import Model.ThreadRunner;
import Model.Threads;

@RunWith(ThreadRunner.class)
public class HiloTest {
	@Test
    @Threads(30)
    public void test() {
        System.out.println("Probando el test");
    }
}
