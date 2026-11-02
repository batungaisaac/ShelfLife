# ShelfLife

ShelfLife is an Android app for keeping track of the food you have at home (fridge, freezer and cupboards) and reminding you before it expires. We wanted something that helps cut down on food waste and stops you buying stuff you already have.

## What it does

You add an item with its name, quantity, where it's stored, the expiry date and a photo. The home screen lists everything with a green, amber or red label depending on how close it is to expiring.

When you use something up or throw it out, you mark it as used or binned (partial use works too). Those records feed into a Waste Stats screen that compares used vs. wasted food month by month. Anything you finish gets added to the shopping list automatically.

## Features we're working on

- Home list with colour-coded expiry labels
- Adding and editing items, with photos
- Expiring Soon screen
- Swipe to mark an item as used or binned
- Waste Stats
- Shopping list
- Daily expiry notifications with quick actions
- Search, and filter by fridge / freezer / cupboard
- Location reminder near your usual supermarket showing what you already have
- Shake the phone to open Expiring Soon

## Tech

- Kotlin + Jetpack Compose
- Room (SQLite) for local storage
- Navigation Compose
- ViewModel / Lifecycle

## Running it

Clone the repo, open the folder in Android Studio and let Gradle sync. Needs Android 7.0 (API 24) or newer.

## Status

Working on milestone 1: project setup, data layer, navigation and the core screens.
