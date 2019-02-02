# Bound Services with Binder - Play Audio

This app uses the Bound services to play audio streamed from web. As client `Activity` and bound `Service` run in the same process a `Binder` implementation is used to expose public methods (start, pause and stop audio) in `LocalBinder` class to the client.

The `Service` also communicates through `Handler` and `Message` objects, the current player position used to update the `ProgressBar` in the `Activity`. Toast messages are shown anytime the control Buttons are clicked.

## Screenshots

![Bound Services Audio  Phone](https://raw.githubusercontent.com/SamuelaAnastasi/BoundServiceBinderPlayAudio/master/previews/preview.jpg)  
