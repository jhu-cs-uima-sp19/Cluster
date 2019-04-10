# Firebase Paths and Information

    Information on Firebase and Firestore Implementation

## Firestore User Path

users/{example-user}   
> stores basic user info
    
users/{example-user}/events/{starred}
> references to events user has starred
    - must have way of checking if event was deleted
    
users/{example-user}/events/{created}
> references to events user has created

- *{example-user}* is unique user id
- *{document}* is document

## Firestore Event Path (beta)

**Method 1:**
events/{country}/COLLECTION/{unique-event}
- COLLECTION could be a search criteria? Like sports, etc?
- Country document is the country 

**Method 2:**
events/{country}/USA/{unique-event}
- country document is named country
- collection for each country 

**Event Sub-Path:**
../{unique event}
> Stores basic event info
> Array of searchable keywords for querying
> - Example: citiesRef.where("regions", "array-contains", "west_coast")

> .creator
>> - stores id of creator for easy profile referencing

../{unique event}/public/{star}/
> .numStars
> - number of stars

> .userStar[]
> - optional array of users that have starred the event

