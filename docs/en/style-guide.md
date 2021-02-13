## Style guide for contributors

Thanks for your interest in contribution to Calculator Plus! Here are the principles we use when writing code. Please conform to these so we can merge your pull request
without going back and forth about style. The main principle is: _make your
code look like the surrounding code_.

### Kotlin

Most of the code of **Calculator Plus** is written in Kotlin. If you are not familiar with Kotlin, please familiarize yourself before attempting to modify the code. There are plenty of good, free resources out there. Some of them are listed below

- [Kotlin](https://kotlinlang.org/docs/home.html)
- [Kotlin for Android](https://developer.android.com/kotlin/campaign/learn)
- [TutorialsPoint](https://www.tutorialspoint.com/kotlin/)
  and so on.

### Rebasing

Commits in a pull request should consist of logical changes. If there are multiple authors, make sure each author has their own commit. It's not a good idea to modify author information. Merge commits should be rebased out of pull requests.

### Style notes

We use Java JDK 8

We use Android Studio, please follow the project level coding style in all the Calculator Plus commits.

This style guide must be followed diligently in all Calculator Plus contributions!

- Use two spaces for indentation, _no tabs_

- Use single spaces around operators
  ```kotlin
  val x = 1
  ```
  not
  ```kotlin
  val x=1
  ```

- Spaces after commas and colons in lists, objects, function calls, etc...
  ```kotlin
  val x = add(4, 5)
  ```
  not
  ```kotlin
  val x = add(4,5)
  ```

- Don't add semicolons at the end of the statements
  ```kotlin
  val x = 5
  ```
  not
  ```kotlin
  val x = 5;
  ```

- Brackets for `function`, `if`, etc... go on same line, `else` gets sandwiched
  ```kotlin
  if (foo == bar) {
    // do something
  } else {
    // do something else
  }
  ```
  not
  ```kotlin
  if (foo == bar)
  {
    // do something
  }
  else
  {
    // do something else
  }
  ```

- Space after `if`, `for`, and `function`:
  ```kotlin
  if (foo == bar) {
  ```
  ```kotlin
  for (i in list) {
  ```
  ```kotlin
  fun add (a: Int, b: Int) {
  ```
  not
  ```kotlin
  if(foo == bar) {
  ```
  ```kotlin
  for(i in list) {
  ```
  ```kotlin
  fun add(a: Int, b: Int) {
  ```

- Break up long strings like this:
  ```kotlin
  val string = "This is a really really long string. " +
  	    "So, it is better to break it up."
  ```

- Comments should line up with code
  ```kotlin
  if (foo == 5) {
    //adding foo
    add(foo, foo)
  }
  ```
  not
  ```kotlin
  if (foo == 5) {
  //adding foo
    myFunc(foo)
  }
  ```

- Variable names should be camelCased:
  ```kotlin
  val myVar = 42
  ```
  not
  ```kotlin
  val my_var = 42
  ```
