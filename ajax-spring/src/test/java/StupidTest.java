
public class StupidTest {

	public static void main(String[] args) {
		/*
	      for(int i = 0; i < 2; i++) {
	         for(int j = 2; j>= 0; j--) {
	            if(i == j) break;
	            System.out.println("i=" + i + " j="+j);
	         }
	      }
	      
	      String s ="example";
	      System.out.println(s.substring(2,4));
	      */
	      t1();
	}
	
	private static void t1(){
	      int i=0, j=2;
	      do {
	         i=++i;
	         j--;
	      } while(j>0);
	      System.out.println(i);	
	      System.out.println(".............");
	      String s1 = "abc";
	      String s2 = "abc";
	      if(s1 == s2)
	          System.out.println(1);
	      else
	          System.out.println(2);
	      if(s1.equals(s2))
	          System.out.println(3);
	      else
	          System.out.println(4);
	      
	      int z = 10;
	      int b = 10;
	      System.out.println("KrK: " + (++b) + "....." + (z++) + ".."  +z);
	}

}
