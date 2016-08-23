package com.csipsimple.f5chat.background;

import android.database.ContentObserver;
import android.net.Uri;

public class MyObserver extends ContentObserver
{
   public MyObserver() {
      super(null);
   }

   @Override
   public void onChange(boolean selfChange) {
      this.onChange(selfChange, null);
   }		

   @Override
   public void onChange(boolean selfChange, Uri uri) {
      // do s.th.
      // depending on the handler you might be on the UI
      // thread, so be cautious!

      System.out.println("<-- got changes");
   }
}