// Create a new thread.
class NewThread implements Runnable 
{
   Thread t;
   NewThread() 
   {
      // Create a new, second thread
      t = new Thread(this, "Demo Thread");
      System.out.println("Child thread: " + t);
      t.start(); // Start the thread
   }
   
   // This is the entry point for the second thread.
   public void run() 
   {
      try 
      {
         for(int i = 5; i > 0; i--) 
         {
            System.out.println("Child Thread: " + i);
            // Let the thread sleep for a while.
            Thread.sleep(500);
         }
     } 
      catch (InterruptedException e) 
      {
         System.out.println("Child interrupted.");
      }
      System.out.println("Exiting child thread.");
   }
}


