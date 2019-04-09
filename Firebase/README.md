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

    *{example-user}* is unique user id
    *{document}* is document
