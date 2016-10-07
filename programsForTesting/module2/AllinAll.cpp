#include <stdio.h>
#include <stdlib.h>

// check whether string s is a subsequence of string t
// logic: check whether every chara in string s is also in string t, but keep the order of chara in s.
// once the current character of s is found in t, look for next character of s starting from the position right after where it stops in t.
// one pointer i for s, one pointer j for t.
// traverse through every character in s, when s[i] is found in t[j], i increase, j increase. start to look for s[i+1] from t[j+1] in t.
int insertBug = 0;
int matchString(char* s, char* t) {
    char* sptr = s;
    char* tptr = t;
    //while(sptr != 0) { // this condition does not work. since sptr point to null character, but sptr is not a null ptr, which does not point to null - 0X0 (special location).
    while(*sptr != '\0') {   
	    while(*tptr != '\0' && *sptr != *tptr) {
	    	tptr++;
	    }
	    if (*tptr == '\0') {
	       
	        break;    
        }
        // we found one character in s also in t
        sptr++;
	if (insertBug == 0) {
            tptr++; 
	}  
    }
    if (*sptr == '\0') { //which means we reach the end of string s
    	return 1;
    }
    return 0;
}
   
int main (int argc, char** argv) {
    
    char s[256];
    char t[256];
    while(scanf("%s %s", s, t) == 2) {
       int res = matchString(s, t);
       printf("%s\n", (res == 1)? "Yes": "No" );
      
    }   

	return 0;
}

