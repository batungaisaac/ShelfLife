# ShelfLife

ShelfLife is an Android app for keeping track of the food you have at home (fridge, freezer and cupboard) so you use it before it expires. The aim is to cut down on food waste and stop you buying things you already have.

## Milestone 1: what's built

**Tracking food**
- Add an item with its name, quantity, storage location, expiry date (date picker), and an optional price and notes
- Tap an item to edit it in the same form
- Home list of everything still in the kitchen, with expiry labels: red once an item has expired, orange when it's within 3 days of expiring
- Expiring Soon screen showing what expires in the next 3 days

**Interaction** (added after feedback that the first version was too simple)
- Swipe right to mark an item **Used**, swipe left to mark it **Binned**. Items aren't deleted; their status and resolved date are saved.
- An Undo snackbar after every swipe
- Partial use: "Use some" lowers the amount left, and the item is marked Used when it reaches zero
- Search by name, and filter by Fridge, Freezer or Cupboard

**Shopping and stats**
- Shopping List: add items, tick them off and clear the ticked ones
- After an item is marked Used, the app offers to add it to the Shopping List
- Waste Stats: how many items were used and binned this month, a bar comparing the two and the percentage binned

**Other**
- Bottom navigation with four tabs: Home, Expiring, Shopping, Stats
- Custom app icon

## Next (Milestone 2)

- Daily expiry notifications with quick actions
- Location reminder near your usual supermarket showing what you already have
- Shake the phone to open Expiring Soon
- Photos of items
- Waste Stats history across months

## Tech

- Kotlin + Jetpack Compose (Material 3)
- Room (SQLite) for local storage
- Navigation Compose
- ViewModel / Lifecycle

## Running it

Clone the repo, open the folder in Android Studio and let Gradle sync. Needs Android 7.0 (API 24) or newer.

## Documentation

Milestone documents and wireframes are in the [`docs`](docs/) folder.

## Status

Milestone 1 complete.
