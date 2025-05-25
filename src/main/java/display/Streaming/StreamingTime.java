 package display.Streaming;

import digitalweatherstation.Time;

public class StreamingTime extends Time
 {
   private int myDayOfWeek;
   private int myStreamingDurationInMinutes;
   
   public StreamingTime(int hour, int minute, int dayOfWeek, int streamingDurationInMinutes) {
     super(hour, minute);
     
     this.myDayOfWeek = dayOfWeek;
     this.myStreamingDurationInMinutes = streamingDurationInMinutes;
   }
   
   public StreamingTime(int hour, int minute, int streamingDurationInMinutes) {
     this(hour, minute, 0, streamingDurationInMinutes);
   }
 
   
   public int getDayOfWeek() {
     return this.myDayOfWeek;
   }
   
   public int getStreamingDurationInMinutes() {
     return this.myStreamingDurationInMinutes * 60000;
   }
 }