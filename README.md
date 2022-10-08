# Sophie's Stavi's Searcher
_An application to check when my wife's favorite sandwich food truck is in the area_

__Background__

One day when I was driving home from work, I noticed my wife's favorite food truck in the parking lot of our neighborhood's clubhouse. Upon telling her that she missed it since we already had dinner plans, she became disappointed. Neither she nor I were aware of the truck's schedule. I get emails from the clubhouse, but there are so many and I don't have time to go through all of them. As a result, I built this application in my free time over the last couple weeks to periodically check the truck's schedule and send out email and text notifications to myself and my wife if the truck was making an appearence soon.

__Basic Overview__

The application makes use of the `ScheduledExecutorService` to run periodically and follows the following flow:
1. Create a JWT Token (if a valid access token does not already exist)
2. Sign the JWT Token using the SHA256WithRSA signing algorithm
3. Exchange the JWT Token with Google for an access token
4. Use the access token to invoke Google's Calendar API to retrieve Stavi's schedule
5. Parse through the response payload to determine if the food truck will be in our neighborhood
6. Send out email & text notifications based on the results

Another runnable scheduled with the same executor periodically sends me notifications so that I know the application is still running. I took advantage of Java's Write-Once-Run-Anywhere property and developed the entirety of the application on my Windows laptop, but created an executable jar which is now running as a systemd service on my raspberry pi. 

__Other info__

I recently got my Oracle Java SE 11 Professional Certification, so I decided to put it to use! I took this as a learning opportunity, so I kept dependencies to a minimum and built many things from scratch, like creating a JWT token, signing it with a SHA256WithRSA algorithm, and using it to retrieve an access token from Google to later invoke their APIs. I also tried my hand at applying a few design patterns for practice. I plan on making minor updates to this as the need arises. 

__Backlog__
1. Property changes without needing to restart the application
2. Better and more consistent SMS solution
3. Notifications that better inform my wife of the food truck's schedule outside our neighborhood
4. Logs that rotate based on date
5. Query the Google Calendar API with more than one keyword
6. Notify my wife of other events in which she may be interested
7. ~~Implement the `EncryptedFileReader` to read encrypted data instead of storing in plaintext files~~
8. Implement a way to check the health status of the application without waiting for the scheduled health check notification
