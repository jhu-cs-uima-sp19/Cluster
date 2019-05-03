# Firebase Paths and Information

    Information on Firebase and Firestore Implementation

## Firestore User Path

users/{example-user}   
> stores basic user info
    
users/{example-user}/events/{interested}
> references to events user has starred
    - must have way of checking if event was deleted
    
users/{example-user}/events/{created}
> references to events user has created

- *{example-user}* is unique user id
- *{document}* is document

## Firestore Event Path (beta)

events/{country}/USA/{unique-event}
- country document is named country
- collection for each country 

**Event:**

../{unique event}
> Stores basic event info
> Array of searchable keywords for querying
> - Example: citiesRef.where("regions", "array-contains", "west_coast")

> .creator
>> - stores id of creator for easy profile referencing


**Event Sub-Paths:**

../{unique event}/public/{star}/
> .numStars
> - number of stars

> .userStar[]
> - optional array of users that have starred the event




## Firebase Storage

**User Profile Images:**

users/
>image name is users unique id

**Event Image(s) _beta_:**

events/{country}/{eventId}/{userId}/
> country may be removed to simplify things

> eventId is the unque event identfier

> userId is the id of the user that created the event

> image name will be a set name or number


**REMEMBER WHEN DELETING PARENT DIRECTORIES, SUB-DIRECTORIES AND STORAGE MUST ALSO BE MANUALLY DELETED AS WELL**


## Firebase Functions
Generates compressed thumbnails for uploaded images for optimized image retrieval
> New Image Name: thumb_<old image name>
