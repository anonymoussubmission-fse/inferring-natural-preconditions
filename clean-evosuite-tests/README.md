This is used to clean the EvoSuite generated test suite to remove properties unrelated to the MUT.

For each method (test) in the EvoSuite generated test suite:   
        - Search for a method call to `func`. If the test doesn't have a call to `func`, then it's probably just asserting a property on the object. Remove it.   
    - Search for method calls to `assertEquals()` and remove them. Properties asserted on the `func` should only be `assertTrue` or` assertFalse` on the return value

   - Search for any try-catches and make two modifications:
        1. If the exception type ends with ERROR then remove it ??? I DON'T UNDERSTAND THIS! AND CANT FIND A TEST WITH ERROR IN IT YET ???
        2. Instead of "verifying the exception", throw it! So we can see the stack trace when we execute in order to wrap.

