## Style guide for contributors

Thanks for your contribution to Calculator Plus! Here are the principles we use when writing Java code. Please conform to these so we can merge your pull request
without going back and forth about style. The main principle is: *make your
code look like the surrounding code*.

### Java

Most of the Code of Calculator Plus is written in Java. If you are not familiar with Java,  please familiarize yourself before attempting to modify the code. There are plenty of good, free resources out there. Some of them are listed below
- [TutorialsPoint](https://www.tutorialspoint.com/java/)
- [Udemy](https://www.udemy.com/java-tutorial/)
- [EdX](https://www.edx.org/learn/java)
- [Java T Point](https://www.javatpoint.com/java-tutorial)
and so on.

### Rebasing

Commits in a pull request should consist of logical changes. If there are multiple authors, make sure each author has their own commit. It's not a good idea to modify author information. Merge commits should be rebased out of pull requests.

### Style notes

We use the latest Java JDK, ie [Java SE 10.0.1](http://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html)

This style guide must be followed diligently in all Calculator Plus contributions! 

*   Use two spaces for indentation, *no tabs*
*   Use single spaces around operators

    ```java
    int x = 1;
    ```
    not
    ```java
    int x=1;
    ```

*   Spaces after commas and colons in lists, objects, function calls, etc...

    ```java
    int x = myFunc("string", 5);
    ```
    not
    ```java
    int x = myFunc("string",5);
    ```

*   Always end statements with semicolons
*   Brackets for `function`, `if`, etc... go on same line, `else` gets sandwiched

    ```java
    if (foo === bar) {
      // do something
    } else {
      // do something else
    }
    ```
    not
    ```java
    if (foo === bar)
    {
      // do something
    }
    else
    {
      // do something else
    }
    ```

*   Space after `if`, `for`, and `function`:

    ```java
    if (foo == bar) {
    ```
    ```java
    for (int i = 0; i < 10; i ++) {
    ```
    ```java
    void myFunc (String string, int num) {
    ```
    not
    ```java
    if(foo === bar) {
    ```
    ```java
    for(int i = 0; i < 10; i ++) {
    ```
    ```java
    void myFunc(String string, int num) {
    ```
*   Break up long strings like this:
    ```java
    String string = "This is a really really long string. " + 
		    "So, it is better to break it up.";
    ```
*   Comments should line up with code
    ```java
    if (foo == 5) {
      myFunc(foo);
      // foo++;
    }
    ```
    not
    ```java
    if (foo == 5) {
      myFunc(foo);
    //foo++;
    }
    ```

*   Variable names should be camelCased:

    ```java
    int myVar = 42;
    ```
    not
    ```java
    int my_var = 42;
    ```