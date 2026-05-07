**Flaky Test Report**  
**Name:** Moritz, Lukas  
**Flaky Test 1**  
**Test name:** FileBettingServiceTest.test file betting with threads  
**Root cause:**  
   
 Race Conditions. Since both Threads work at the same time on the same file both of  
   
 them might access the file at the same time (eg. Current Value: 38), on thread than needs bit more time  
   
 while the second thread runs through the function multiple times. Once the first thread is than finished  
   
 the value of the file is 44, but one the first thread writes his value into the file its 39, since he  
   
 started on 38, eventhough its 44.  
**Fix:**  
   
 My fix adds a Lock to the Test, whenever one thread tries to run, if the other thread is currently working on it  
   
 the thread has to wait until the other one is finished. This however is just a fix for the test. If the  
   
 function should normally work with multiple threads the file it self should be locked in the function  
**Flaky Test 2**  
**Test name:** WorldCupTest.evaluate returns zero when no bets are placed  
**Root cause:**  
   
 The cachedResult in the BettingService.kt might have a value stored in it, when it was already called by  
   
 another test. Since the tests are cleared before each test, and most test overwrite the bets it dosn't  
   
 effect other test.  
**Fix:**  
   
 While the BettingService.clear() clears the list of bets it dosn't reset the chacedResults. Setting them  
   
 to null in the clear function fixes this.  
**Flaky Test 3**  
**Test name:** WorldCupTest.load json from network  
**Root cause:**  
   
 The problem is with the 300 millisecond timeout it times the network out to fast if the connection is "bad".  
   
 Therefore, it sometimes fails, or in my case fails always if the json isn't retrieved fast enough  
**Fix:**  
   
 Increase the timeout time, to the one used in the Function  
**Flaky Test 4**  
**Test name:** FileBettingServiceTest.fresh service has no bets  
**Root cause:**  
   
 Two tests (FileBettingServiceTest.fresh service has no bets, FileBettingServiceTest.save bets to the shared file) use the same companion object val SHARED_BET_FILE. Since the Tests dont always run in the same  
   
 order the test fails if FileBettingServiceTest.save bets to the shared file has already written something to  
   
 the File, since it then would not be empty any more. Since FileBettingServiceTest.save bets to the shared file  
   
 writes something in to the File it is not effected by FileBettingServiceTest.fresh service has no bets, therfore  
   
 only FileBettingServiceTest.fresh service has no bets is flaky.  
**Fix:**  
   
 The easiest fix was just to give both Testcases a different file, therefore it is always empty for the test  
   
 and both tests don't interfere with each other.  
**Flaky Test 5**  
**Test name:** WorldCupTest.standings are stable when multiple teams tie on all criteria  
**Root cause:**  
   
 The sorting at the end of the function StandingService.calculate is stable, but the hashMap isn't  
   
 always in the same order. Through this the test gets flaky because its based on non-deterministic values.  
**Fix:**  
   
 Changing the IdentityHashMap to a mutableMapOf fixes the problem.  
