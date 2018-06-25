# Calculator App
A Simple Calculator with rich features for daily use.

The Calculator Plus provides simple and advanced mathematical functions for day to day use. It is packed with different themes and designs for everyone. Its design is inspired by Google's Material Design. It is very efficient calculator with advanced error handling. It is designed to handle errors intelligently.

## Why use Calculator Plus
1. **Light weight**  
It is very light app, only 2.5 MB of size.

2. **User friendly design**  
Inspired by Google's material design.

3. **No Ads whatsoever**  
Free to use with no hidden charges or ads.

4. **Smart calculations**  
It uses smart calculations to figure out what you are typing and automatically finds the result.

5. **Smart corrections**  
It tries to correct the equations if they are not valid.

6. **Very flexible**  
Very flexible design and can be easily used on any display size or even in multi-window mode.

7. **Very precise**  
It gives you precise results up to 10 decimal places

8. **Updated regularly**  
It is updated regularly with new designs and features, adapting new android versions.

9. **Very robust**  
It has a strong error handling methods which doesn't allow it it fail easily.

10. **Battery friendly**  
It is very efficient battery wise. It uses about 20% less battery compared to a normal calculator
<br/><br/>
>Uses **Smart Brackets resolution** to solve all types of equations

Here is the java code snippet for balancing the equation.
The code here tries to balance the user equation by checking the brackets pairs and then adding the rest open or close bracket to balance the equation.
```java
private  void  tryBalancingBrackets(String equ) {
	tempEqu = equ;
	int  a = 0, b = 0;
	
	if (tempEqu.charAt(tempEqu.length() - 1) == '(') {
		while (tempEqu.charAt(tempEqu.length() - 1) == '(') {
			tempEqu =  tempEqu.substring(0, tempEqu.length() - 1);
				if (tempEqu.length() == 0)
					return;
		}
	}
	int  numOfPairs = 0;
	int  openBracketCount = 0;

	for (int  i = 0; i < tempEqu.length(); i++) {
		char  c = tempEqu.charAt(i);
		
		if (c == '(') 
			openBracketCount++;
		
		if (c == ')'  && openBracketCount > 0) {
			openBracketCount--;
			numOfPairs++;
		}
	}

	for (int  i = 0; i < tempEqu.length(); i++) {
		char  c = tempEqu.charAt(i);
		
		if (c == '(')
			a++;

		if (c == ')')
			b++;
	}

	int  reqOpen = b - numOfPairs;

	int  reqClose = a - numOfPairs;

	while (reqOpen > 0) {
		tempEqu = "(" + tempEqu;
		reqOpen--;
	}

	while (reqClose > 0) {
		tempEqu = tempEqu + ")";
		reqClose--;
	}
}
```

## Download
Download the latest Version of the app from the play store.  
<a href='https://play.google.com/store/apps/details?id=com.gigaworks.tech.calculator&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height="100" width="250"/>
