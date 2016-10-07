
import java.util.*;
import java.lang.*; // for String Buffer


public class NoRepeat{
      
    public static int insertBug = 0;  // 0 means we do not insert bug. 1 means we do.
    public static void main ( String args[]) {

         HashMap<Character, Integer> hm = new HashMap<Character, Integer>();
	 String str = args[0];

         System.out.println(str);
         for (int i = 0; i < str.length(); i++) {

              Character ch = (Character)str.charAt(i);
	      if (hm.get(ch) == null) {
                   hm.put(ch, new Integer(1));
	      } 
	      else {
	          int val =(int)hm.get(ch);
	          hm.put(ch, new Integer(val + 1));
              } 

	 }
         int res = 0;
	 StringBuffer substr =  new StringBuffer("");
	 for( int i = 0 ; i < str.length(); i++ ) {

             Character ch = (Character)str.charAt(i);
	     if ((int)hm.get(ch) >= 1) {
                 if (insertBug == 0 ) {
	              hm.put(ch, new Integer(0)); // we do not want to count it again.
                 }

		 res ++;
		 substr.append((char) ch);
             }
         
	 }
	 
         System.out.println("non repetitive subsequence is : \""+ substr.toString() + "\" with length of "+ res);
          	
        // System.out.println("NonRepetitive subsequence :"+ substr.toString());
    
    }


}
